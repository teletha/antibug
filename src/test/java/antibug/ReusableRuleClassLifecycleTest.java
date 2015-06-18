/*
 * Copyright (C) 2015 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug;

import org.junit.Rule;
import org.junit.Test;

/**
 * @version 2015/06/18 18:05:01
 */
public class ReusableRuleClassLifecycleTest {

    @Rule
    public final ClassLifeCycle rule = new ClassLifeCycle();

    @Test
    public void notInvoked1() throws Exception {
        throw new AssertionError("We must not invoke this test method.");
    }

    @Test
    public void notInvoked2() throws Exception {
        throw new AssertionError("We must not invoke this test method.");
    }

    /**
     * @version 2015/06/18 18:04:53
     */
    private static final class ClassLifeCycle extends ReusableRule {

        /**
         * {@inheritDoc}
         */
        @Override
        protected void beforeClass() throws Exception {
            // error in before class
            throw new BeforeClassError();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void afterClass() {
            burkeError(BeforeClassError.class);
        }
    }

    /**
     * @version 2011/03/20 10:06:58
     */
    private static class BeforeClassError extends Error {

        private static final long serialVersionUID = -8184462531495172018L;
    }
}
