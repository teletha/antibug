/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug;

import java.util.Objects;

import org.junit.jupiter.api.Test;

/**
 * @version 2018/04/08 13:24:43
 */
class CodeTest {

    @Test
    void rejectNullArgs() {
        assert Code.rejectNullArgs(this::method);
    }

    void method(String value) {
        Objects.requireNonNull(value);
    }
}
