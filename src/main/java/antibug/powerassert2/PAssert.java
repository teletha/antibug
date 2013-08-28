/*
 * Copyright (C) 2013 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.powerassert2;

/**
 * @version 2013/08/29 8:12:07
 */
public class PAssert {

    public static <T> T $(T value, String expression) {
        return value;
    }

    public static boolean $(boolean value, String expression) {
        return value;
    }
}
