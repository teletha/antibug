/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug;

import java.util.Objects;

import org.junit.jupiter.api.Test;

/**
 * @version 2018/04/01 21:05:08
 */
public class NullCheckerTest {

    @Test
    void ok() {
        assert Code.rejectNullArgs(this::some);
    }

    void some(String value, String sec) {
        Objects.requireNonNull(value);
    }

    void someInt(int value) {
    }
}
