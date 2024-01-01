/*
 * Copyright (C) 2024 The ANTIBUG Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug.powerassert;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * @version 2018/04/05 9:28:37
 */
class LongTest {

    @RegisterExtension
    static PowerAssertTester test = new PowerAssertTester();

    @Test
    void constant_0() {
        long value = -1;

        test.willUse("0");
        test.willCapture("value", value);
        assert 0 == value;
    }

    @Test
    void constant_1() {
        long value = -1;

        test.willUse("1");
        test.willCapture("value", value);
        assert 1 == value;
    }

    @Test
    void constant_2() {
        long value = -1;

        test.willUse("2");
        test.willCapture("value", value);
        assert 2 == value;
    }

    @Test
    void constant_3() {
        long value = -1;

        test.willUse("3");
        test.willCapture("value", value);
        assert 3 == value;
    }

    @Test
    void constant_M1() {
        long value = 0;

        test.willUse("-1");
        test.willCapture("value", value);
        assert -1 == value;
    }

    @Test
    void big() {
        long value = 2;

        test.willUse("1234567890123");
        test.willCapture("value", value);
        assert 1234567890123L == value;
    }

    @Test
    void not() {
        long value = 10;

        test.willUse("10");
        test.willUse("!=");
        test.willCapture("value", value);
        assert 10 != value;
    }

    @Test
    void negative() {
        long value = 10;

        test.willUse("10");
        test.willUse("-value");
        test.willCapture("value", value);
        assert 10 == -value;
    }

    @Test
    void array() {
        long[] array = {0, 1, 2};
        long[] other = {0, 1, 2};

        test.willCapture("array", array);
        assert array == other;
    }

    @Test
    void arrayIndex() {
        long[] array = {0, 1, 2};

        test.willCapture("array", array);
        test.willCapture("array[1]", 1L);
        assert array[1] == 128;
    }

    @Test
    void arrayLength() {
        long[] array = {0, 1, 2};

        test.willCapture("array", array);
        test.willCapture("array.length", 3);
        assert array.length == 10;
    }

    @Test
    void arrayNew() {
        test.willUse("new long[] {1, 2}");
        assert new long[] {1, 2} == null;
    }

    @Test
    void varargs() {
        test.willCapture("var()", false);
        assert var();
    }

    boolean var(long... var) {
        return false;
    }

    @Test
    void method() {
        test.willCapture("test()", 1L);
        assert test() == 2;
    }

    long test() {
        return 1;
    }

    @Test
    void parameter() {
        test.willCapture("test(12)", false);
        assert test(12);
    }

    private boolean test(long value) {
        return false;
    }

    /** The tester. */
    private long longField = 11;

    /** The tester. */
    private static long longFieldStatic = 11;

    @Test
    void fieldLongAccess() {
        test.willCapture("longField", 11L);
        assert longField == 0;
    }

    @Test
    void fieldIntAccessWithHiddenName() {
        long longField = 11;

        test.willCapture("this.longField", longField);
        assert this.longField == 0;
    }

    @Test
    void fieldLongStaticAccess() {
        test.willCapture("longFieldStatic", 11L);
        assert longFieldStatic == 0;
    }
}