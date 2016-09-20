/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.powerassert2;

import static antibug.bytecode.Bytecode.*;
import static jdk.internal.org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import antibug.bytecode.Agent.Translator;

/**
 * @version 2012/01/14 22:48:47
 */
class PowerAssertTranslator extends Translator {

    /** The zero object. */
    private static final Integer Zero = Integer.valueOf(0);

    private static final String ErrorName = Type.getInternalName(PowerAssertionError.class);

    private static final String ContextName = Type.getInternalName(PowerAssertContext.class);

    /** The state. */
    private boolean startAssertion = false;

    /** The state. */
    private boolean skipNextJump = false;

    /** The state. */
    private boolean processAssertion = false;

    /** The state. */
    private boolean compare = false;

    /** The state. */
    private boolean initialize = true;

    /** The operand stack frame. */
    private ArrayDeque<String> stack = new ArrayDeque();

    /** The using operand list. */
    private ArrayList<Operand> operands = new ArrayList();

    /**
     * <p>
     * Helper method to remove last stack.
     * </p>
     * 
     * @return
     */
    private String last() {
        return stack.pollLast();
    }

    /**
     * <p>
     * Load initialization flag.
     * </p>
     * 
     * @return
     */
    private void loadInitFlag() {
        load(initialize);

        initialize = false;
    }

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
            initialize = true;
            return;
        }

        if (processAssertion) {
            // store current value
            // LocalVariable local = copy(Type.getType(desc));

            switch (opcode) {
            case GETFIELD:
                break;

            case GETSTATIC:
                String className = computeClassName(owner);

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
                    // journal.constant(insn(ICONST_0));
                }
                compare = false;
                // journal.condition("==");
                break;

            case IF_ICMPEQ:
            case IF_ACMPEQ:
                // record value
                System.out.println("change");
                mv.visitInsn(ICONST_1);
                Label l5 = new Label();
                mv.visitJumpInsn(GOTO, l5);
                mv.visitLabel(label);
                mv.visitFrame(Opcodes.F_APPEND, 2, new Object[] {Opcodes.INTEGER, Opcodes.INTEGER}, 0, null);
                mv.visitInsn(ICONST_0);
                mv.visitLabel(l5);
                // mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {Opcodes.INTEGER});
                mv.visitInsn(ICONST_0);
                mv.visitLdcInsn("==");
                mv.visitMethodInsn(INVOKESTATIC, "antibug/powerassert2/PowerAssertContext", "log", "(ZZLjava/lang/String;)Z", false);

                break;

            case IFNE:
                if (!compare) {
                    // journal.constant(insn(ICONST_0));
                }
                compare = false;
                // journal.condition("!=");
                break;

            case IF_ICMPNE:
            case IF_ACMPNE:
                // record code
                stack.add(last() + " == " + last());

                break;

            case IF_ICMPLT:
                // journal.condition("<");
                break;

            case IF_ICMPLE:
                // journal.condition("<=");
                break;

            case IF_ICMPGT:
                // journal.condition(">");
                break;

            case IF_ICMPGE:
                // journal.condition(">=");
                break;

            case IFNULL:
                // recode null constant
                // journal.constant(insn(ACONST_NULL));

                // recode == expression
                // journal.condition("==");
                break;

            case IFNONNULL:
                // recode null constant
                // journal.constant(insn(ACONST_NULL));

                // recode != expression
                // journal.condition("!=");
                break;
            }
        }

        if (skipNextJump) {
            skipNextJump = false;
            processAssertion = true;

            // create new journal for this assertion
            // journal = instantiate(Journal.class, PowerAssertContext.class);
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
            super.visitTypeInsn(opcode, ErrorName);
            return;
        }

        super.visitTypeInsn(opcode, type);

        if (processAssertion) {
            switch (opcode) {
            case INSTANCEOF:
                // journal.instanceOf(computeClassName(type));
                break;

            case ANEWARRAY:
                // LocalVariable local = copy(Type.getType(type));

                // journal.arrayNew(computeClassName(type), local);
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
            // instantiate PowerAssertError
            super.visitMethodInsn(opcode, ErrorName, name, desc, access);

            // reset state
            startAssertion = false;
            skipNextJump = false;
            processAssertion = false;
            return;
        }

        super.visitMethodInsn(opcode, owner, name, desc, access);

        if (processAssertion) {
            // Type type = Type.getType(desc);
            boolean constructor = name.charAt(0) == '<';

            // save current value
            // LocalVariable local = copy(constructor ? Type.getType(owner) : type.getReturnType());

            switch (opcode) {
            case INVOKESTATIC:
                // journal.methodStatic(computeClassName(owner), name, desc, local);
                break;

            case INVOKESPECIAL:
                if (constructor) {
                    // journal.constructor(computeClassName(owner), desc, local);
                    break;
                }
                // fall-through for private method call
            default:
                // journal.method(name, desc, local);
                break;
            }
        }
    }

    /**
     * @see org.objectweb.asm.MethodVisitor#visitIincInsn(int, int)
     */
    @Override
    public void visitIincInsn(int index, int increment) {
        super.visitIincInsn(index, increment);

        if (processAssertion) {
            // journal.increment(hashCode(), index, increment);
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
                // LocalVariable local = copy(Bytecode.OBJECT_TYPE);

                switch (operand) {
                case T_BOOLEAN:
                    // journal.arrayNew("boolean", local);
                    break;

                case T_BYTE:
                    // journal.arrayNew("byte", local);
                    break;

                case T_CHAR:
                    // journal.arrayNew("char", local);
                    break;

                case T_DOUBLE:
                    // journal.arrayNew("double", local);
                    break;

                case T_FLOAT:
                    // journal.arrayNew("float", local);
                    break;

                case T_INT:
                    // journal.arrayNew("int", local);
                    break;

                case T_LONG:
                    // journal.arrayNew("long", local);
                    break;

                case T_SHORT:
                    // journal.arrayNew("short", local);
                    break;
                }
                break;

            default:
                // journal.constant(intInsn(opcode, operand));
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
                // journal.constant(insn(opcode));
                break;

            case IALOAD:
                // journal.arrayIndex(copy(Type.INT_TYPE));
                break;

            case LALOAD:
                // journal.arrayIndex(copy(Type.LONG_TYPE));
                break;

            case FALOAD:
                // journal.arrayIndex(copy(Type.FLOAT_TYPE));
                break;

            case DALOAD:
                // journal.arrayIndex(copy(Type.DOUBLE_TYPE));
                break;

            case BALOAD:
                // journal.arrayIndex(copy(Type.BOOLEAN_TYPE));
                break;

            case CALOAD:
                // journal.arrayIndex(copy(Type.CHAR_TYPE));
                break;

            case AALOAD:
                // journal.arrayIndex(copy(Bytecode.OBJECT_TYPE));
                break;

            case IASTORE:
            case LASTORE:
            case DASTORE:
            case FASTORE:
            case BASTORE:
            case CASTORE:
            case AASTORE:
                // journal.arrayStore();
                break;

            case ARRAYLENGTH:
                // journal.field("length", "I", copy(Type.INT_TYPE), hashCode());
                break;

            case IADD:
            case LADD:
            case FADD:
            case DADD:
                // journal.operator("+");
                break;

            case ISUB:
            case LSUB:
            case FSUB:
            case DSUB:
                // journal.operator("-");
                break;

            case IMUL:
            case LMUL:
            case FMUL:
            case DMUL:
                // journal.operator("*");
                break;

            case IDIV:
            case LDIV:
            case FDIV:
            case DDIV:
                // journal.operator("/");
                break;

            case IREM:
            case LREM:
            case FREM:
            case DREM:
                // journal.operator("%");
                break;

            case INEG:
            case LNEG:
            case FNEG:
            case DNEG:
                // journal.negative();
                break;

            case ISHL:
            case LSHL:
                // journal.operator("<<");
                break;

            case ISHR:
            case LSHR:
                // journal.operator(">>");
                break;

            case IUSHR:
            case LUSHR:
                // journal.operator(">>>");
                break;

            case IOR:
            case LOR:
                // journal.operator("|");
                break;

            case IXOR:
            case LXOR:
                // journal.operator("^");
                break;

            case IAND:
            case LAND:
                // journal.operator("&");
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
            // journal.constant(ldc(value));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitVarInsn(int opcode, int index) {
        super.visitVarInsn(opcode, index);

        if (processAssertion) {
            String name = getLocalName(index);

            // record code
            stack.add(name);

            // record value
            loadInitFlag();
            load(name);

            Type type = opcode == ALOAD ? OBJECT_TYPE : getLocalType(index);

            super.visitMethodInsn(INVOKESTATIC, ContextName, "log", "(" + type.getDescriptor() + "ZLjava/lang/String;)" + type.getDescriptor(), false);

            if (opcode == ALOAD) {
                super.visitTypeInsn(CHECKCAST, getLocalType(index).getInternalName());
            }
        }
    }

    /**
     * @param operand
     * @param hint
     * @return
     */
    private static Operand infer(Operand operand, Operand hint) {
        return infer(operand, hint.getType());
    }

    /**
     * @param operand
     * @param hint
     * @return
     */
    private static Operand infer(Operand operand, Type hint) {
        // Integer value represents various types (int, char and boolean).
        // We have to check the opposite term' type to infer its actual type.
        if (operand instanceof Constant && operand.value instanceof Integer) {
            Integer value = (Integer) operand.value;

            if (hint == Type.CHAR_TYPE) {
                return new Constant((char) value.intValue());
            } else if (hint == Type.BOOLEAN_TYPE) {
                return new Constant(value.intValue() == 1);
            }
        }
        return operand;
    }

    /**
     * <p>
     * Represents constant value.
     * </p>
     * 
     * @version 2012/01/22 15:15:01
     */
    private static class Constant extends Operand {

        /**
         * @param value An actual value.
         */
        private Constant(Object value) {
            super(String.valueOf(value), value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean isVariableHolder() {
            return false;
        }

        /**
         * @see testament.powerassert.Operand#toString()
         */
        @Override
        public String toString() {
            if (value == null) {
                return "null";
            }

            if (value instanceof String) {
                return "\"" + value + "\"";
            }

            if (value instanceof Character) {
                return "'" + value + "'";
            }

            if (value instanceof Class) {
                return ((Class) value).getSimpleName() + ".class";
            }

            return super.toString();
        }
    }

    /**
     * <p>
     * Represents variable value.
     * </p>
     * 
     * @version 2012/01/22 14:15:51
     */
    private static class Variable extends Operand {

        /** The variable type for inference. */
        private final Type type;

        /**
         * @param name A variable name.
         * @param type A variable type.
         * @param value An actual value.
         */
        private Variable(String name, Type type, Object value) {
            super(name, value, type);

            this.type = type;
        }

        /**
         * @see testament.powerassert.Operand#getType()
         */
        @Override
        Type getType() {
            return type;
        }
    }

    /**
     * <p>
     * Represents Array initialization expression.
     * </p>
     * 
     * @version 2012/01/19 16:18:02
     */
    private class NewArray extends Operand {

        /** The array type. */
        private final Type type;

        /** The array size. */
        private final int size;

        /** The actual array elements. */
        private final List<Operand> elements = new ArrayList();

        /**
         * @param type An array type.
         * @param value An actual array value.
         */
        private NewArray(String type, Object value) {
            super(type, value);

            this.type = Type.getType(value.getClass().getComponentType());

            // Boolean array is initialized with false values, other type arrays are initialized
            // with null values. Array store operation will be invoked array length times but false
            // value will not be invoked. So we should fill with false values to normalize setup.
            this.size = Array.getLength(value);

            for (int i = 0; i < size; i++) {
                elements.add(new Constant(false));
            }
        }

        /**
         * <p>
         * Add element value.
         * </p>
         * 
         * @param operand
         */
        private void add(int index, Operand operand) {
            elements.set(index, infer(operand, type));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        boolean isVariableHolder() {
            return false;
        }

        /**
         * @see testament.powerassert.Operand#toString()
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("new ");
            builder.append(name).append("[] {");

            Iterator<Operand> iterator = elements.iterator();

            if (iterator.hasNext()) {
                builder.append(iterator.next());

                while (iterator.hasNext()) {
                    builder.append(", ").append(iterator.next());
                }
            }

            builder.append('}');
            return builder.toString();
        }
    }
}
