/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.benchmark;

import java.lang.reflect.Method;

import antibug.ReusableRule;

/**
 * @version 2011/02/08 22:48:23
 */
public class Benchmark extends ReusableRule {

    /**
     * <p>
     * Measure an execution speed of the specified code fragment.
     * </p>
     * 
     * @param measuredCode A code to be measured.
     */
    public void measure(Code measuredCode) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean skip(Method method) {
        System.out.println(isSuite);
        return isSuite;
    }

    /**
     * <p>
     * </p>
     * 
     * @version 2012/01/30 10:56:30
     */
    public static interface Code {

        /**
         * <p>
         * Write micro benchmark code.
         * </p>
         * 
         * @return
         * @throws Throwable
         */
        Object call() throws Throwable;
    }
}
