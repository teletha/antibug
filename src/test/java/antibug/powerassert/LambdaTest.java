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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * @version 2018/03/31 16:31:21
 */
public class LambdaTest {

    @RegisterExtension
    static PowerAssertTester test = new PowerAssertTester();

    @Test
    @Disabled
    public void runnable() throws Exception {
        test.willCapture("runnable(() -> new Object())", false);
        assert runnable(() -> new Object());
    }

    private boolean runnable(Runnable run) {
        return false;
    }
}
