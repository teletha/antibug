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
 * @version 2014/08/04 9:33:32
 */
public class Statement {

    int For(int value) {
        for (int i = 0; i < 10; i++) {
            value++;
        }
        return value;
    }

    int ForNoInitialNoUpdate(int value) {
        int i = 0;

        for (; i < 10;) {
            value++;
            i++;
        }
        return value;
    }

    int While(int value) {
        while (value <= 10) {
            ++value;
        }
        return value;
    }

    int DoWhile(int value) {
        do {
            value++;
        } while (value < 0);

        return value;
    }

    int If(int value) {
        if (value == 0) {
            value++;
        }
        return value;
    }
}
