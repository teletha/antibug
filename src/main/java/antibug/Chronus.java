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

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import kiss.I;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import antibug.bytecode.Agent;

/**
 * @version 2014/03/05 14:25:42
 */
public class Chronus implements TestRule {

    /** reuse */
    private static final String Scheduler = "java/util/concurrent/ScheduledThreadPoolExecutor";

    /** reuse */
    private static final String Executor = "java/util/concurrent/ThreadPoolExecutor";

    /** reuse */
    private static final String Executors = "java/util/concurrent/Executors";

    /** reuse. */
    private static final Type ChronoScheduler = Type.getType(ChronoTrigger.class);

    /** reuse. */
    private static final Type Wrapper = Type.getType(Awaitable.class);

    /** The internal name. */
    private final Set<String> fqcn = new HashSet();

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
            fqcn.add(clazz.getName().replace('.', '/'));
            agent.transform(clazz);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                base.evaluate();
            }
        };
    }

    /**
     * <p>
     * Await all asynchronus tasks.
     * </p>
     */
    public void await() {
        ChronoTrigger.await();
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
            if (!fqcn.contains(name)) {
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
         * @version 2014/03/05 9:55:37
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
                if (owner.equals(Executors)) {
                    switch (name) {
                    case "newCachedThreadPool":
                        owner = Wrapper.getInternalName();
                        break;
                    }
                }

                if (owner.equals(Scheduler)) {
                    owner = ChronoScheduler.getInternalName();
                }

                if (owner.equals(Executor)) {
                    owner = Wrapper.getInternalName();
                }
                mv.visitMethodInsn(opcode, owner, name, desc, type);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void visitTypeInsn(int opcode, String owner) {
                if (owner.equals(Scheduler)) {
                    owner = ChronoScheduler.getInternalName();
                }

                if (owner.equals(Executor)) {
                    owner = Wrapper.getInternalName();
                }
                mv.visitTypeInsn(opcode, owner);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                super.visitFieldInsn(opcode, owner, name, desc);

                if (opcode == GETSTATIC) {
                    if (isAssignable(ExecutorService.class, desc)) {
                        mv.visitMethodInsn(INVOKESTATIC, Wrapper.getInternalName(), "wrap", "(Ljava/util/concurrent/ExecutorService;)Ljava/util/concurrent/ExecutorService;", false);
                    }
                }
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
                if (desc.charAt(0) != 'L') {
                    return false;
                }

                try {
                    Class<?> clazz = Class.forName(desc.substring(1, desc.length() - 1).replace('/', '.'));

                    return type.isAssignableFrom(clazz);
                } catch (ClassNotFoundException e) {
                    return false;
                }
            }
        }
    }
}
