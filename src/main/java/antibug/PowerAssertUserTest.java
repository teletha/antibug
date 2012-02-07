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
 * @version 2012/01/13 22:11:44
 */
public class PowerAssertUserTest {

    /** The tester. */
    private static final String intField = "aaaaa";

    /** The tester. */
    private static int intFieldStatic = 11;

    @Test
    public void fieldIntStaticAccess() throws Exception {
        int[] array = {0, 1, 2};

        assert intFieldStatic == 10 : "test";
    }

    @Test
    public void fieldIntStaticAcces1s() throws Exception {
        String value = "aaaa";

        assert sss(intField).length() == 1;
    }

    @Test
    public void external() {
        String value = "test";

        External.assertInExternal(value);
    }

    public static String sss(String value) {
        return value;
    }

    /**
     * @version 2012/01/22 19:58:35
     */
    private static class External {

        private static void assertInExternal(String value) {
            assert value.length() == 20;
        }
    }
}
