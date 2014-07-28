/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import kiss.I;
import antibug.bytecode.Agent;
import antibug.internal.Awaitable;

/**
 * @version 2014/03/06 12:14:04
 */
public class Chronus extends ReusableRule {

    /** The re-usable type. */
    private static final Type Executor = Type.getType(Executors.class);

    /** The re-usable type. */
    private static final Type Tool = Type.getType(Awaitable.class);

    /** The internal name. */
    private final Set<String> names = new HashSet();

    /** The bytecode enhancer. */
    private final Agent agent = new Agent(new Transformer());

    /**
     * <p>
     * Manipulate time.
     * </p>
     * 
     * @param clazz
     */
    public Chronus(Class... classes) {
        for (Class clazz : classes) {
            names.add(clazz.getName().replace('.', '/'));
            agent.transform(clazz);
        }
    }

    /**
     * <p>
     * Await all asynchronus tasks.
     * </p>
     */
    public void await() {
        Awaitable.await();
    }

    /**
     * <p>
     * Freeze process.
     * </p>
     * 
     * @param millseconds
     */
    public void freeze(int millseconds) {
        try {
            long start = System.currentTimeMillis();
            Thread.sleep(millseconds);
            long end = System.currentTimeMillis();

            if (end - start < millseconds) {
                wait((int) (millseconds - end + start));
            }
        } catch (InterruptedException e) {
            throw I.quiet(e);
        }
    }

    /**
     * @version 2014/03/05 9:51:01
     */
    private class Transformer implements ClassFileTransformer {

        /**
         * {@inheritDoc}
         */
        @Override
        public byte[] transform(ClassLoader loader, String name, Class<?> clazz, ProtectionDomain domain, byte[] bytes) {
            if (!names.contains(name)) {
                return bytes;
            } else {
                try {
                    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                    new ClassReader(bytes).accept(new ClassTranslator(writer), ClassReader.SKIP_DEBUG);

                    return writer.toByteArray();
                } catch (Throwable e) {
                    e.printStackTrace();
                    throw I.quiet(e);
                }
            }
        }

        /**
         * @version 2012/01/14 13:16:21
         */
        private class ClassTranslator extends ClassVisitor {

            /**
             * @param arg0
             */
            private ClassTranslator(ClassWriter writer) {
                super(ASM5, writer);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                return new MethodTranslator(super.visitMethod(access, name, desc, signature, exceptions));
            }
        }

        /**
         * @version 2014/03/06 11:52:18
         */
        private class MethodTranslator extends MethodVisitor {

            /**
             * @param visitor
             */
            protected MethodTranslator(MethodVisitor visitor) {
                super(ASM5, visitor);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean type) {
                mv.visitMethodInsn(opcode, owner, name, desc, type);

                if (opcode == INVOKESPECIAL && name.equals("<init>")) {
                    // For constructor
                    // new ExecutorService() -> Awaitable.wrap(new ExecutorService())
                    if (isAssignable(ExecutorService.class, owner)) {
                        wrap();
                        return;
                    }
                }

                if (opcode == INVOKESTATIC && owner.equals(Executor.getInternalName())) {
                    // For Executors utility methods
                    // Executors.method() -> Awaitable.wrap(Executors.method())
                    switch (name) {
                    case "newCachedThreadPool":
                    case "newSingleThreadPool":
                    case "newFixedThreadPool":
                    case "newWorkStealingPool":
                    case "newSingleThreadScheduledExecutor":
                    case "newScheduledThreadPool":
                    case "unconfigurableExecutorService":
                        wrap();
                        return;
                    }
                }

            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                super.visitFieldInsn(opcode, owner, name, desc);

                switch (opcode) {
                case GETSTATIC:
                case GETFIELD:
                    // For field access
                    // service.submit() -> Awaitable.wrap(service).submit()
                    if (isAssignable(ExecutorService.class, desc)) {
                        wrap();
                    }
                    break;
                }
            }

            /**
             * <p>
             * Call wrapper code.
             * </p>
             */
            private void wrap() {
                mv.visitMethodInsn(INVOKESTATIC, Tool.getInternalName(), "wrap", "(Ljava/util/concurrent/ExecutorService;)Ljava/util/concurrent/ExecutorService;", false);
            }

            /**
             * <p>
             * Chech type.
             * </p>
             * 
             * @param type
             * @param desc
             * @return
             */
            private boolean isAssignable(Class type, String desc) {
                if (desc.charAt(0) == 'L') {
                    desc = desc.substring(1, desc.length() - 1);
                }

                try {
                    Class<?> clazz = Class.forName(desc.replace('/', '.'));

                    return type.isAssignableFrom(clazz);
                } catch (ClassNotFoundException e) {
                    return false;
                }
            }
        }
    }
}