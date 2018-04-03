/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.powerassert;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * @version 2018/04/04 0:51:58
 */
class WithMessageTest {

    @RegisterExtension
    static PowerAssertTester test = new PowerAssertTester();

    @Test
    void message() {
        String value = "ok";
        test.willCapture("value", "ok");
        test.willMessage("Not Empty");
        assert value.isEmpty() : "Not Empty";
    }

    @Test
    void expression() {
        String value = "ok";
        test.willCapture("value", "ok");
        test.willMessage("ok is not empty");
        assert value.isEmpty() : value + " is not empty";
    }
}
