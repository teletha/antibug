/*
 * Copyright (C) 2015 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source.low;

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

    void ForEach(int[] values) {
        for (int value : values) {
            System.out.println(value);
        }
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

    int IfElse(int value) {
        if (value == 0) {
            value++;
        } else {
            value--;
        }
        return value;
    }

    int Break(int value) {
        root: while (value < 10) {
            if (value % 2 == 0) {
                break;
            }

            if (value % 3 == 0) {
                break root;
            }
            value++;
        }
        return value;
    }

    int Continue(int value) {
        root: for (; value < 10; value++) {
            if (value % 2 == 0) {
                continue;
            }

            if (value % 3 == 0) {
                continue root;
            }
            return 0;
        }
        return value;
    }

    int Switch(int value) {
        switch (value) {
        case 1:
            value++;
            break;

        case 2:
            value--;

        case 3:
            return 4;

        default:
            return 5;
        }
        return 6;
    }

    void Synchronized(String value) {
        synchronized (value) {
            System.out.println(value);
        }
    }
}
