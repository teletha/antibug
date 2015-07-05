/*
 * Copyright (C) 2015 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.powerassert;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @version 2015/07/05 10:26:11
 */
public class LambdaTest {

    @Rule
    @ClassRule
    public static final PowerAssertTester tester = new PowerAssertTester();

    @Test
    @Ignore
    public void runnable() throws Exception {
        tester.willCapture("runnable(() -> new Object())", false);
        assert runnable(() -> new Object());
    }

    private boolean runnable(Runnable run) {
        return false;
    }
}
