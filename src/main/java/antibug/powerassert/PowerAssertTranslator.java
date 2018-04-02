/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug.powerassert;

import static net.bytebuddy.jar.asm.Opcodes.*;

import java.lang.reflect.Array;

import antibug.bytecode.Agent.Translator;
import antibug.bytecode.Bytecode;
import antibug.bytecode.LocalVariable;
import net.bytebuddy.jar.asm.Handle;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.Type;

/**
 * @version 2012/01/14 22:48:47
 */
class PowerAssertTranslator extends Translator {

    /** The state. */
    private boolean startAssertion = false;

    /** The state. */
    private boolean skipNextJump = false;

    /** The state. */
    private boolean processAssertion = false;

    /** The state. */
    private boolean compare = false;

    /** The context for code log. */
    private Journal journal;

    /**
     * <p>
     * Compute simple class name.
     * </p>
     * 
     * @return
     */
    private String computeClassName(String internalName) {
        int index = internalName.lastIndexOf('$');

        if (index == -1) {
            index = internalName.lastIndexOf('/');
        }
        return index == -1 ? internalName : internalName.substring(index + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        super.visitFieldInsn(opcode, owner, name, desc);

        if (!startAssertion && opcode == GETSTATIC && name.equals("$assertionsDisabled")) {
            startAssertion = true;
            skipNextJump = true;
            return;
        }

        if (processAssertion) {
            // store current value
            LocalVariable local = copy(Type.getType(desc));

            switch (opcode) {
            case GETFIELD:
                journal.field(name, desc, local, hashCode());
                break;

            case GETSTATIC:
                String className = computeClassName(owner);

                journal.fieldStatic(className, this.className.equals(owner) ? name : className + "." + name, desc, local);
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitJumpInsn(int opcode, Label label) {
        super.visitJumpInsn(opcode, label);

        if (processAssertion) {
            switch (opcode) {
            case IFEQ:
                if (!compare) {
                    journal.constant(insn(ICONST_0));
                }
                compare = false;
                journal.condition("==");
                break;

            case IF_ICMPEQ:
            case IF_ACMPEQ:
                journal.condition("==");
                break;

            case IFNE:
                if (!compare) {
                    journal.constant(insn(ICONST_0));
                }
                compare = false;
                journal.condition("!=");
                break;

            case IF_ICMPNE:
            case IF_ACMPNE:
                journal.condition("!=");
                break;

            case IF_ICMPLT:
                journal.condition("<");
                break;

            case IF_ICMPLE:
                journal.condition("<=");
                break;

            case IF_ICMPGT:
                journal.condition(">");
                break;

            case IF_ICMPGE:
                journal.condition(">=");
                break;

            case IFNULL:
                // recode null constant
                journal.constant(insn(ACONST_NULL));

                // recode == expression
                journal.condition("==");
                break;

            case IFNONNULL:
                // recode null constant
                journal.constant(insn(ACONST_NULL));

                // recode != expression
                journal.condition("!=");
                break;
            }
        }

        if (skipNextJump) {
            skipNextJump = false;
            processAssertion = true;

            // create new journal for this assertion
            journal = instantiate(Journal.class, PowerAssertContext.class);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitTypeInsn(int opcode, String type) {
        if (processAssertion && opcode == NEW && type.equals("java/lang/AssertionError")) {
            processAssertion = false;

            // replace AssertionError with PowerAssertionError
            super.visitTypeInsn(opcode, Type.getType(PowerAssertionError.class).getInternalName());
            return;
        }

        super.visitTypeInsn(opcode, type);

        if (processAssertion) {
            switch (opcode) {
            case INSTANCEOF:
                journal.instanceOf(computeClassName(type));
                break;

            case ANEWARRAY:
                LocalVariable local = copy(Type.getObjectType(type));

                journal.arrayNew(computeClassName(type), local);
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean access) {
        // replace invocation of AssertionError constructor.
        if (startAssertion && opcode == INVOKESPECIAL && owner.equals("java/lang/AssertionError")) {
            load(journal); // load context

            // append parameter for context
            StringBuilder builder = new StringBuilder(desc);
            builder.insert(builder.length() - 2, "L");
            builder.insert(builder.length() - 2, Type.getType(PowerAssertContext.class).getInternalName());
            builder.insert(builder.length() - 2, ";");

            // instantiate PowerAssertError
            super.visitMethodInsn(opcode, Type.getType(PowerAssertionError.class).getInternalName(), name, builder.toString(), access);

            // reset state
            startAssertion = false;
            skipNextJump = false;
            processAssertion = false;
            return;
        }
        super.visitMethodInsn(opcode, owner, name, desc, access);

        if (processAssertion) {
            Type type = Type.getType(desc);
            boolean constructor = name.charAt(0) == '<';

            // save current value
            LocalVariable local = copy(constructor ? Type.getObjectType(owner) : type.getReturnType());

            switch (opcode) {
            case INVOKESTATIC:
                journal.methodStatic(computeClassName(owner), name, desc, local);
                break;

            case INVOKESPECIAL:
                if (constructor) {
                    journal.constructor(computeClassName(owner), desc, local);
                    break;
                }
                // fall-through for private method call
            default:
                journal.method(name, desc, local);
                break;
            }
        }
    }

    /**
     * @see net.bytebuddy.jar.asm.MethodVisitor#visitIincInsn(int, int)
     */
    @Override
    public void visitIincInsn(int index, int increment) {
        super.visitIincInsn(index, increment);

        if (processAssertion) {
            journal.increment(hashCode(), index, increment);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitIntInsn(int opcode, int operand) {
        super.visitIntInsn(opcode, operand);

        if (processAssertion) {
            switch (opcode) {
            case NEWARRAY:
                LocalVariable local = copy(Bytecode.OBJECT_TYPE);

                switch (operand) {
                case T_BOOLEAN:
                    journal.arrayNew("boolean", local);
                    break;

                case T_BYTE:
                    journal.arrayNew("byte", local);
                    break;

                case T_CHAR:
                    journal.arrayNew("char", local);
                    break;

                case T_DOUBLE:
                    journal.arrayNew("double", local);
                    break;

                case T_FLOAT:
                    journal.arrayNew("float", local);
                    break;

                case T_INT:
                    journal.arrayNew("int", local);
                    break;

                case T_LONG:
                    journal.arrayNew("long", local);
                    break;

                case T_SHORT:
                    journal.arrayNew("short", local);
                    break;
                }
                break;

            default:
                journal.constant(intInsn(opcode, operand));
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);

        if (processAssertion) {
            switch (opcode) {
            case ICONST_M1:
            case ICONST_0:
            case ICONST_1:
            case ICONST_2:
            case ICONST_3:
            case ICONST_4:
            case ICONST_5:
            case LCONST_0:
            case LCONST_1:
            case FCONST_0:
            case FCONST_1:
            case FCONST_2:
            case DCONST_0:
            case DCONST_1:
            case ACONST_NULL:
                journal.constant(insn(opcode));
                break;

            case IALOAD:
                journal.arrayIndex(copy(Type.INT_TYPE));
                break;

            case LALOAD:
                journal.arrayIndex(copy(Type.LONG_TYPE));
                break;

            case FALOAD:
                journal.arrayIndex(copy(Type.FLOAT_TYPE));
                break;

            case DALOAD:
                journal.arrayIndex(copy(Type.DOUBLE_TYPE));
                break;

            case BALOAD:
                journal.arrayIndex(copy(Type.BOOLEAN_TYPE));
                break;

            case CALOAD:
                journal.arrayIndex(copy(Type.CHAR_TYPE));
                break;

            case AALOAD:
                journal.arrayIndex(copy(Bytecode.OBJECT_TYPE));
                break;

            case IASTORE:
            case LASTORE:
            case DASTORE:
            case FASTORE:
            case BASTORE:
            case CASTORE:
            case AASTORE:
                journal.arrayStore();
                break;

            case ARRAYLENGTH:
                journal.field("length", "I", copy(Type.INT_TYPE), hashCode());
                break;

            case IADD:
            case LADD:
            case FADD:
            case DADD:
                journal.operator("+");
                break;

            case ISUB:
            case LSUB:
            case FSUB:
            case DSUB:
                journal.operator("-");
                break;

            case IMUL:
            case LMUL:
            case FMUL:
            case DMUL:
                journal.operator("*");
                break;

            case IDIV:
            case LDIV:
            case FDIV:
            case DDIV:
                journal.operator("/");
                break;

            case IREM:
            case LREM:
            case FREM:
            case DREM:
                journal.operator("%");
                break;

            case INEG:
            case LNEG:
            case FNEG:
            case DNEG:
                journal.negative();
                break;

            case ISHL:
            case LSHL:
                journal.operator("<<");
                break;

            case ISHR:
            case LSHR:
                journal.operator(">>");
                break;

            case IUSHR:
            case LUSHR:
                journal.operator(">>>");
                break;

            case IOR:
            case LOR:
                journal.operator("|");
                break;

            case IXOR:
            case LXOR:
                journal.operator("^");
                break;

            case IAND:
            case LAND:
                journal.operator("&");
                break;

            case LCMP:
            case FCMPL:
            case DCMPL:
                compare = true;
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitLdcInsn(Object value) {
        super.visitLdcInsn(value);

        if (processAssertion) {
            journal.constant(ldc(value));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitVarInsn(int opcode, int index) {
        super.visitVarInsn(opcode, index);

        if (processAssertion) {
            journal.local(hashCode(), index, local(opcode, index));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(name, desc, signature, start, end, index);

        PowerAssertContext.registerLocalVariable(hashCode(), name, desc, index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitInvokeDynamicInsn(String name, String description, Handle bsm, Object... bsmArgs) {
        super.visitInvokeDynamicInsn(name, description, bsm, bsmArgs);

        PowerAssertContext.registerLocalVariable(hashCode(), name, description, -1);

        if (processAssertion) {
            Handle handle = (Handle) bsmArgs[1];
            Type functionalInterfaceType = (Type) bsmArgs[0];
            Type lambdaType = Type.getMethodType(handle.getDesc());
            Type callerType = Type.getMethodType(description);
            int parameterDiff = lambdaType.getArgumentTypes().length - functionalInterfaceType.getArgumentTypes().length;
            boolean useContext = callerType.getArgumentTypes().length - Math.max(parameterDiff, 0) == 1;

            // detect functional interface
            Class interfaceClass = convert(callerType.getReturnType());
            System.out.println(interfaceClass + "  " + name + "   " + description);

            // detect lambda method
            Class lambdaClass = convert(handle.getOwner());
            String lambdaMethodName = handle.getName();
            String lambdaMethodSignature = handle.getDesc();
            System.out.println(useContext + "  " + lambdaClass + "  " + lambdaMethodName + "  " + lambdaMethodSignature);

            if (handle.getTag() == H_INVOKESTATIC) {
                // lambda
                journal.lambda(handle.getName(), handle.getDesc());
            } else {
                // method reference
                journal.methodReference(handle.getName(), handle.getDesc());
            }
        }
    }

    /**
     * Convert parameter type to class.
     * 
     * @param type A parameter {@link Type}.
     * @return A parameter {@link Class}.
     */
    static final Class convert(Type type) {
        switch (type.getSort()) {
        case Type.INT:
            return int.class;

        case Type.LONG:
            return long.class;

        case Type.FLOAT:
            return float.class;

        case Type.DOUBLE:
            return double.class;

        case Type.CHAR:
            return char.class;

        case Type.BYTE:
            return byte.class;

        case Type.SHORT:
            return short.class;

        case Type.BOOLEAN:
            return boolean.class;

        case Type.VOID:
            return void.class;

        case Type.ARRAY:
            return Array.newInstance(convert(type.getElementType()), new int[type.getDimensions()]).getClass();

        default:
            try {
                return Class.forName(type.getClassName());
            } catch (ClassNotFoundException e) {
                // If this exception will be thrown, it is bug of this program. So we must
                // rethrow the wrapped error in here.
                throw new Error(e);
            }
        }
    }

    /**
     * <p>
     * Helper method to convert the specified class name to {@link Class}.
     * </p>
     * 
     * @param className A fully qualified internal class name.
     * @return Java class.
     */
    static final Class convert(String className) {
        if (className == null) {
            return null;
        }

        try {
            return Class.forName(className.replace('/', '.'));
        } catch (ClassNotFoundException e) {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error(e);
        }
    }
}
