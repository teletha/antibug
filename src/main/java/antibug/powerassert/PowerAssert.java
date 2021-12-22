/*
 * Copyright (C) 2021 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug.powerassert;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

import org.junit.jupiter.engine.execution.JupiterEngineExecutionContext;

import antibug.bytecode.Agent;

public class PowerAssert {

    /** For test. */
    static Consumer<PowerAssertionError> errorCapture;

    /** The recode for the translated classes. */
    private static final Set<String> translated = new ConcurrentSkipListSet();

    /** The actual translator. */
    private static final Agent agent = new Agent(PowerAssertTranslator.class);

    /**
     * @param error
     * @param testExecutor
     * @param errorExecutor
     */
    public static void capture(JupiterEngineExecutionContext context, Throwable error, Runnable testExecutor, Consumer<Throwable> errorExecutor) {
        if (error instanceof PowerAssertionError) {
            PowerAssertionError e = (PowerAssertionError) error;

            if (errorCapture != null) {
                errorCapture.accept(e); // for self test
            } else {
                errorExecutor.accept(error); // rethrow for unit test
            }
        } else {
            try {
                Throwable cause = error;

                while (cause != null) {
                    if (cause instanceof AssertionError) {
                        // should we print this error message in detail?
                        // if (description.getAnnotation(PowerAssertOff.class) == null &&
                        // !description.getTestClass()
                        // .isAnnotationPresent(PowerAssertOff.class)) {

                        Class clazz = Class.forName(cause.getStackTrace()[0].getClassName());

                        synchronized (PowerAssert.class) {
                            // translate assertion code only once
                            if (translated.add(clazz.getName())) {
                                agent.transform(clazz);
                                testExecutor.run();
                                return;
                            }
                        }
                    }
                    cause = cause.getCause();
                }
                errorExecutor.accept(error);
            } catch (ClassNotFoundException e) {
                errorExecutor.accept(e);
            }
        }
    }
}