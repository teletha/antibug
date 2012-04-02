/*
 * Copyright (C) 2012 Nameless Production Committee
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

import antibug.CleanRoom;

/**
 * @version 2012/02/02 13:17:55
 */
public class WithAnothoerRuleTest {

    @Rule
    public static final CleanRoom ROOM = new CleanRoom();

    @Rule
    public static final PowerAssertTester tester = new PowerAssertTester();

    @Test
    public void constant() throws Exception {
        boolean value = false;

        tester.willCapture("value", value);
        assert value;
    }
}
