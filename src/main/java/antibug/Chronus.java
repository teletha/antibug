/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug;

import static java.util.concurrent.TimeUnit.*;
import static net.bytebuddy.jar.asm.Opcodes.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import antibug.bytecode.Agent;
import antibug.internal.Awaitable;
import net.bytebuddy.jar.asm.ClassReader;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Type;

/**
 * @version 2018/08/31 21:27:52
 */
public class Chronus {

    /** The re-usable type. */
    private static final Type Executor = Type.getType(Executors.class);

    /** The re-usable type. */
    private static final Type Tool = Type.getType(Awaitable.class);

    /** The internal name. */
    private final Set<String> names = new HashSet();

    /** The bytecode enhancer. */
    private final Agent agent = new Agent(new Transformer());

    /** The base time. */
    private long base;

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
    public void freeze(long millseconds) {
        freezeNano(MILLISECONDS.toNanos(millseconds));
    }

    /**
     * <p>
     * Freeze process.
     * </p>
     * 
     * @param time A nano time to freeze.
     */
    private void freezeNano(long time) {
        try {
            long start = System.nanoTime();
            NANOSECONDS.sleep(time);
            long end = System.nanoTime();

            long remaining = start + time - end;

            if (0 < remaining) {
                freezeNano(remaining);
            }
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    /**
     * <p>
     * Set base time.
     * </p>
     */
    public void mark() {
        base = System.nanoTime();
    }

    /**
     * <p>
     * Freeze process the specified duration from marked time.
     * </p>
     * 
     * @param ms
     * @return If {@link Chronus} freezes process, return true otherwise false.
     */
    public boolean freezeFromMark(long ms) {
        long now = System.nanoTime();
        long wait = base + MILLISECONDS.toNanos(ms) - now;

        if (wait <= 0) {
            return false;
        } else {
            freezeNano(wait);
            return true;
        }
    }

    /**
     * <p>
     * Freeze process the specified duration from marked time.
     * </p>
     * 
     * @param millseconds
     * @return If {@link Chronus} freezes process, return true otherwise false.
     */
    public void freezeFromMark(long start, long end, Runnable assertion) {
        if (freezeFromMark(start)) {
            try {
                assertion.run();
            } catch (AssertionError e) {
                long now = System.nanoTime();

                if (now < base + MILLISECONDS.toNanos(end)) {
                    throw e;
                } else {
                    // ignore error
                }
            }

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
                    throw new Error(e);
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
                super(ASM6, writer);
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
                super(ASM6, visitor);
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
                    case "newFixedThreadPool":
                    case "newScheduledThreadPool":
                    case "newSingleThreadExecutor":
                    case "newSingleThreadScheduledExecutor":
                    case "newWorkStealingPool":
                    case "unconfigurableExecutorService":
                    case "unconfigurableScheduledExecutorService":
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
                mv.visitMethodInsn(INVOKESTATIC, Tool
                        .getInternalName(), "wrap", "(Ljava/util/concurrent/ExecutorService;)Ljava/util/concurrent/ExecutorService;", false);
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
