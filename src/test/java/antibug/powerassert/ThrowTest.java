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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * @version 2018/03/31 16:32:28
 */
public class ThrowTest {

    @RegisterExtension
    static PowerAssertTester test = new PowerAssertTester();

    @Test
    public void useAssertWithMessage() {
        int value = 4;

        test.willCapture("value", value);
        assert value == -1 : "this value is " + value;
    }

    @Test
    public void useAssertWithIntMessage() {
        int value = 4;

        test.willCapture("value", value);
        assert value == -1 : value;
    }
}
