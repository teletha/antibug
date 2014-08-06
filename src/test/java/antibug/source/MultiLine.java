/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source;

/**
 * @version 2014/08/05 12:27:18
 */
public abstract class MultiLine {

    String[] strings = {"looooooooooooooooooooooooooooooooooooooooooong", "short",
            "looooooooooooooooooooooooooooooong", "short", "looooooooooooooooooooooooooooooong", "short", "log",
            "short", "looooooooooooooooooooooooooooooooooooooooooong"};

    int[][] ints = {
            {123456789, 123456789, 123456789, 123456789, 123456789, 123456789, 123456789, 123456789, 123456789,
                    123456789, 123456789, 123456789, 123456789, 123456789},
            {123456789, 123456789, 123456789, 123456789, 123456789, 123456789, 123456789, 123456789, 123456789,
                    123456789, 123456789, 123456789, 123456789, 123456789}};

    StringBuilder builder = new StringBuilder().append("0000000000000000000000000000")
            .append("111111111111111111111111111111")
            .append("2222222222222222222222222222")
            .append("3333333333333333333333333333");

    abstract void throwLine(int parameter1, int parameter2, int parameter3, int parameter4, int parameter5, int parameter6, int parameter7, int parameter8, int parameter9, int parameter10)
            throws Exception;

    private static void a() {
    }
}
