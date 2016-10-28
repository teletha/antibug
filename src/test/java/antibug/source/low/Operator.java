/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source.low;

/**
 * @version 2014/08/04 9:35:30
 */
public class Operator {

    int arithmetic(int value) {
        value = value + 1;
        value = value - 2;
        value = value * 3;
        value = value / 4;
        value = value % 5;

        value += 1;
        value -= 2;
        value *= 3;
        value /= 4;
        value %= 5;

        return +value + -value;
    }

    int increment(int value) {
        value++;
        value--;
        ++value;
        --value;

        return value;
    }

    int bit(int value) {
        value = value | 1;
        value = value & 2;
        value = value ^ 3;
        value = ~value;
        value = value << 4;
        value = value >> 5;
        value = value >>> 6;

        value |= 1;
        value &= 2;
        value ^= 3;
        value <<= 4;
        value >>= 5;
        value >>>= 6;

        return value;
    }

    boolean logical(boolean value) {
        value = !value;
        value = value || true;
        value = value && false;

        return value;
    }

    int condition(int value) {
        if (value < 0) {
            return 0;
        }

        if (value > 0) {
            return 1;
        }

        if (value <= 0) {
            return 2;
        }

        if (value >= 0) {
            return 3;
        }

        if (value == 0) {
            return 4;
        }

        if (value != 0) {
            return 5;
        }
        return value;
    }

    boolean instanceOf(Object value) {
        return value instanceof String;
    }

    int ternary(int value) {
        return value < 10 ? value + 20 : 30;
    }

    int multiple(int value) {
        int a;
        int b;

        a = b = value;

        return a * b;
    }

    int brace(int value) {
        return (value + 1) * (value - 1);
    }

    String string(String value) {
        return value + " with " + value;
    }
}
