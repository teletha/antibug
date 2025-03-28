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

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.junit.jupiter.api.Test;

/**
 * @version 2018/03/31 3:13:55
 */
public class AgentTest {

    private static boolean called = false;

    public static final Agent agent = new Agent(new Transformer());

    @Test
    public void agentable() throws Exception {
        // load new class
        new Runnable() {

            @Override
            public void run() {
            }
        }.run();

        assert called;
    }

    /**
     * @version 2012/01/02 11:17:34
     */
    private static final class Transformer implements ClassFileTransformer {

        /**
         * {@inheritDoc}
         */
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)
                throws IllegalClassFormatException {
            called = true;
            return classfileBuffer;
        }
    }
}