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
 * @version 2018/03/31 8:44:55
 */
public class AAATEst {

    @Test
    void ok() {
        System.out.println("OK");
    }

    @Test
    void fail() {
        System.out.println("Start Fail Test");
        int value = 20;
        assert 10 == value;
    }
}
