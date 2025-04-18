/*
 * Copyright (C) 2025 The ANTIBUG Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug.bytecode;

import static net.bytebuddy.jar.asm.Opcodes.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.platform.commons.util.ReflectionUtils;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.jar.asm.ClassReader;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

/**
 * Provide functionality to transform bytecode.
 */
public class Agent {

    /** The redefined classes. */
    private static final Set<String> redefines = new HashSet();

    /** The redefined classes. */
    private static final Map<Class, byte[]> codes = new HashMap();

    /** The Instrumentation tool. */
    private static final Instrumentation tool = ByteBuddyAgent.install();

    /**
     * Create dynamic Agent.
     * 
     * @param translator Your bytecode translator.
     */
    public Agent(Class<? extends Translator> translator) {
        this(new TranslatorTransformer(translator));
    }

    /**
     * Create dynamic Agent.
     * 
     * @param agent Your bytecode translator.
     */
    public Agent(ClassFileTransformer agent) {
        tool.addTransformer(agent, true);
    }

    /**
     * Force to transform the target class.
     * 
     * @param target Specify the class to translate.
     */
    public void transform(Class target) {
        try {
            redefines.add(target.getName().replace('.', '/'));
            tool.retransformClasses(target);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    /**
     * Search the transformed code of the specified class. If the target class is not transformed,
     * returns <code>null</code>.
     * 
     * @param target Specify the class to translate.
     * @return Transformed byte code to load by {@link ClassLoader}.
     */
    public static byte[] getTransformedCode(Class target) {
        return codes.get(target);
    }

    /**
     * @version 2012/01/14 13:09:23
     */
    private static final class TranslatorTransformer implements ClassFileTransformer {

        /** The delegator. */
        private final Class<? extends Translator> translator;

        /**
         * @param translator
         */
        private TranslatorTransformer(Class<? extends Translator> translator) {
            this.translator = translator;
        }

        /**
         * @see java.lang.instrument.ClassFileTransformer#transform(java.lang.ClassLoader,
         *      java.lang.String, java.lang.Class, java.security.ProtectionDomain, byte[])
         */
        @Override
        public byte[] transform(ClassLoader loader, String name, Class<?> clazz, ProtectionDomain domain, byte[] bytes) {
            if (!redefines.contains(name)) {
                return bytes;
            }

            try {
                // collect local variables
                LocalVariableManager manager = new LocalVariableManager();
                new ClassReader(bytes).accept(manager, ClassReader.SKIP_FRAMES);

                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                ClassTranslator visitor = new ClassTranslator(writer, name, manager);
                ClassReader reader = new ClassReader(bytes);
                reader.accept(visitor, ClassReader.EXPAND_FRAMES);
                byte[] transformed = writer.toByteArray();

                codes.put(clazz, transformed);

                return transformed;
            } catch (Throwable e) {
                e.printStackTrace();
                throw new Error(e);
            }
        }

        /**
         * @version 2012/01/14 13:16:21
         */
        private class ClassTranslator extends ClassVisitor {

            /** The internal class name. */
            private final String className;

            /** The variable manager. */
            private final LocalVariableManager manager;

            /**
             * @param arg0
             */
            private ClassTranslator(ClassWriter writer, String className, LocalVariableManager manager) {
                super(Opcodes.ASM9, writer);

                this.className = className;
                this.manager = manager;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                try {
                    MethodVisitor visitor = super.visitMethod(access, name, desc, signature, exceptions);
                    LocalVariablesSorter sorter = new LocalVariablesSorter(access, desc, visitor);
                    Translator translator = ReflectionUtils.newInstance(TranslatorTransformer.this.translator);
                    translator.set(sorter, className, name, Type.getMethodType(desc), manager);

                    return translator;
                } catch (Exception e) {
                    throw new Error(e);
                }
            }
        }
    }

    /**
     * @version 2013/08/29 21:19:16
     */
    private static class LocalVariableManager extends ClassVisitor {

        /** The collector manager. */
        private Map<String, Variables> collectors = new HashMap();

        /**
         * @param api
         * @param cv
         */
        private LocalVariableManager(int api, ClassVisitor cv) {
            super(api, cv);
        }

        /**
         * @param api
         */
        private LocalVariableManager() {
            super(Opcodes.ASM9);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            Variables collector = new Variables();

            collectors.put(name + desc, collector);

            return collector;
        }
    }

    /**
     * @version 2013/08/29 21:20:30
     */
    private static class Variables extends MethodVisitor {

        /** The variable map. */
        private Map<Integer, String> names = new HashMap();

        /** The variable map. */
        private Map<Integer, Type> types = new HashMap();

        /**
         * @param api
         */
        private Variables() {
            super(Opcodes.ASM9);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
            names.put(index, name);
            types.put(index, Type.getType(desc));
        }
    }

    /**
     * @version 2012/01/14 13:08:33
     */
    public static abstract class Translator extends MethodVisitor {

        /** The internal class name. */
        protected String className;

        /** The internal class type. */
        protected Type classType;

        /** The internal method name. */
        protected String methodName;

        /** The internal method type. */
        protected Type methodType;

        /** The method identifier. */
        protected int methodIdentifier;

        /** The local variable manager. */
        private Variables variables;

        /**
         * 
         */
        protected Translator() {
            super(Opcodes.ASM9, null);
        }

        /**
         * Lazy set up.
         */
        final void set(LocalVariablesSorter visitor, String className, String methodName, Type methodDescriptor, LocalVariableManager manager) {
            mv = visitor;
            this.className = className;
            this.classType = Type.getObjectType(className);
            this.methodName = methodName;
            this.methodType = methodDescriptor;
            this.methodIdentifier = methodIdentifier(className, methodName, methodDescriptor);
            this.variables = manager.collectors.get(methodName + methodDescriptor.getDescriptor());
        }

        /**
         * Calculate method identifier by its signature.
         * 
         * @param className
         * @param methodName
         * @param methodDescriptor
         * @return
         */
        protected final int methodIdentifier(String className, String methodName, Type methodDescriptor) {
            return (className + "#" + methodName + methodDescriptor).hashCode();
        }

        /**
         * Get local variable name.
         * 
         * @param position
         * @return
         */
        protected final String getLocalName(int position) {
            return variables.names.get(position);
        }

        /**
         * Get local variable type.
         * 
         * @param position
         * @return
         */
        protected final Type getLocalType(int position) {
            return variables.types.get(position);
        }

        /**
         * Creates a new local variable of the given type.
         * 
         * @param type the type of the local variable to be created.
         * @return the identifier of the newly created local variable.
         */
        protected final LocalVariable newLocal(Type type) {
            return new LocalVariable(type, (LocalVariablesSorter) mv);
        }

        /**
         * Create a new insntance and store it into new local variable.
         * 
         * @param api
         * @param instantiator
         * @return
         */
        protected final <S> S instantiate(Class<S> api, Class<? extends S> instantiator) {
            Type type = Type.getType(instantiator);
            mv.visitTypeInsn(NEW, type.getInternalName());
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, type.getInternalName(), "<init>", Type
                    .getConstructorDescriptor(ReflectionUtils.getDeclaredConstructor(instantiator)), false);

            LocalVariable local = newLocal(type);
            local.store();

            return createAPI(local, api);
        }

        /**
         * Load the specified object as possible as we can.
         * 
         * @param object
         */
        protected final void load(Object object) {
            if (object == null) {
                mv.visitInsn(ACONST_NULL);
            } else if (object instanceof String) {
                mv.visitLdcInsn(object);
            } else if (object instanceof Boolean) {
                mv.visitInsn(object == Boolean.TRUE ? ICONST_1 : ICONST_0);
            } else if (object instanceof Integer) {
                Integer integer = (Integer) object;

                switch (integer.intValue()) {
                case 0:
                    mv.visitInsn(ICONST_0);
                    break;

                case 1:
                    mv.visitInsn(ICONST_1);
                    break;

                case 2:
                    mv.visitInsn(ICONST_2);
                    break;

                case 3:
                    mv.visitInsn(ICONST_3);
                    break;

                case 4:
                    mv.visitInsn(ICONST_4);
                    break;
                case 5:
                    mv.visitInsn(ICONST_5);
                    break;
                case -1:
                    mv.visitInsn(ICONST_M1);
                    break;

                default:
                    mv.visitLdcInsn(object);
                }
            } else if (object instanceof LocalVariable) {
                ((LocalVariable) object).load();
            } else if (Proxy.isProxyClass(object.getClass())) {
                InvocationHandler handler = Proxy.getInvocationHandler(object);

                if (handler instanceof InterfaceCaller) {
                    ((InterfaceCaller) handler).invoker.load();
                }
            }
        }

        /**
         * Helper method to write below code. <pre>
         * mv.visitVisitInsn(Opcodes.DUP);
         * 
         * LocalVariable local = newLocal(type);
         * 
         * local.store();
         * </pre>
         * 
         * @param type
         * @return
         */
        protected final LocalVariable copy(Type type) {
            mv.visitInsn(type.getSize() == 1 ? DUP : DUP2);

            LocalVariable local = newLocal(type);
            local.store();

            return local;
        }

        /**
         * <p>
         * Write local variable code.
         * </p>
         * 
         * @param opcode
         * @param index
         * @return
         */
        protected final Bytecode local(int opcode, int index) {
            return new LocalVariable(opcode, index);
        }

        /**
         * Write instruction code.
         * 
         * @param opcode
         * @return
         */
        protected final Bytecode insn(int opcode) {
            return new Instruction(opcode);
        }

        /**
         * Write int value code.
         * 
         * @param opcode
         * @param operand
         * @return
         */
        protected final Bytecode intInsn(int opcode, int operand) {
            return new IntValue(opcode, operand);
        }

        /**
         * Write constant value code.
         * 
         * @param value
         * @return
         */
        protected final Bytecode ldc(Object value) {
            return new Constant(value);
        }

        /**
         * Helper method to write bytecode which wrap the primitive value.
         * 
         * @param type
         */
        protected final void wrap(Type type) {
            Type wrapper = Bytecode.getWrapperType(type);

            if (wrapper != type) {
                mv.visitMethodInsn(INVOKESTATIC, wrapper.getInternalName(), "valueOf", Type.getMethodDescriptor(wrapper, type), false);
            }
        }

        /**
         * Create API.
         * 
         * @param invoker
         * @param api
         * @return
         */
        protected final <S> S createAPI(LocalVariable invoker, Class<S> api) {
            return (S) Proxy.newProxyInstance(api.getClassLoader(), new Class[] {api}, new InterfaceCaller(invoker));
        }

        /**
         * @version 2012/01/18 1:18:40
         */
        private class InterfaceCaller implements InvocationHandler {

            /** The method invoker. */
            private final LocalVariable invoker;

            /** The invocation type. */
            private final String invocation;

            /**
             * @param invoker
             * @param invocation
             */
            private InterfaceCaller(LocalVariable invoker) {
                this.invoker = invoker;
                this.invocation = invoker == null ? className : invoker.type.getInternalName();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (invoker == null) {
                    mv.visitVarInsn(ALOAD, 0); // load this
                } else {
                    invoker.load(); // load insntance
                }

                // build parameter stacks
                Class[] parameters = method.getParameterTypes();

                for (int i = 0; i < parameters.length; i++) {
                    Class parameter = parameters[i];
                    Object value = args[i];

                    if (value instanceof Bytecode) {
                        Bytecode bytecode = (Bytecode) value;
                        bytecode.write(mv, !parameter.isPrimitive());
                    } else if (parameter == int.class || parameter == long.class || parameter == String.class) {
                        mv.visitLdcInsn(value);
                    }
                }
                // call interface method
                mv.visitMethodInsn(INVOKEVIRTUAL, invocation, method.getName(), Type.getMethodDescriptor(method), false);
                return null;
            }
        }
    }
}