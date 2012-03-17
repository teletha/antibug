/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug;

import org.junit.Test;

/**
 * @version 2012/03/18 8:24:38
 */
public class Adas {

    @Test
    public void testname() throws Exception {
        int aa = 10;
        assert 10 != aa;
    }
}
