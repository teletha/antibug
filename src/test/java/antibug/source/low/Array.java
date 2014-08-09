/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source.low;

/**
 * @version 2014/08/04 13:22:18
 */
public class Array {

    static int[] primitives = {1, 2, 3};

    String[] wrappers = new String[] {"a", "b", "c"};

    // @formatter:off
    int[][] nest = {{1, 2}, {3, 4}};
    // @formatter:on

    int[] length = new int[3];

    static {
        // access
        primitives[0] = primitives[1];
    }
}
