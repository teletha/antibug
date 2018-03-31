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

import org.junit.jupiter.api.Test;

/**
 * @version 2018/03/31 23:02:50
 */
public class ExpectThrowTest {

    @Test
    @ExpectThrow(Error.class)
    void error() {
        throw new Error();
    }
}
