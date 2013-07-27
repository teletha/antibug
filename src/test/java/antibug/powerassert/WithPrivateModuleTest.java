/*
 * Copyright (C) 2013 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.powerassert;

import org.junit.Rule;
import org.junit.Test;

import antibug.PrivateModule;

/**
 * @version 2012/02/24 11:48:29
 */
public class WithPrivateModuleTest {

    @Rule
    public static final PrivateModule module = new PrivateModule(true, false);

    @Rule
    public static final PowerAssertTester tester = new PowerAssertTester();

    @Test
    public void assertionInPrivateModule() throws Exception {
        WithPrivateModuleClassAcessor acessor = (WithPrivateModuleClassAcessor) module.convert(Clazz.class)
                .newInstance();

        tester.willUse("getClass()");
        tester.willUse("Clazz.class");
        acessor.invoke();
    }

    /**
     * @version 2012/02/24 11:56:14
     */
    public static class Clazz implements WithPrivateModuleClassAcessor {

        /**
         * {@inheritDoc}
         */
        @Override
        public void invoke() {
            assert getClass() == Clazz.class;
        }
    }
}
