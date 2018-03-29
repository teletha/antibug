/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug.powerassert;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @version 2012/02/02 13:17:55
 */
public class WithAnothoerRuleTest {

    @Rule
    @ClassRule
    public static final PowerAssertTester tester = new PowerAssertTester();

    @Test
    public void constant() throws Exception {
        boolean value = false;

        tester.willCapture("value", value);
        assert value;
    }
}
