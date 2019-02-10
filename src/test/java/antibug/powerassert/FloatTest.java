/*
 * Copyright (C) 2019 Nameless Production Committee
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
 * @version 2018/04/05 9:31:44
 */
class FloatTest {

    @RegisterExtension
    static PowerAssertTester test = new PowerAssertTester();

    @Test
    void constant_0() {
        float value = -1;

        test.willUse("0");
        test.willCapture("value", value);
        assert 0 == value;
    }

    @Test
    void constant_1() {
        float value = -1;

        test.willUse("1");
        test.willCapture("value", value);
        assert 1 == value;
    }

    @Test
    void constant_2() {
        float value = -1;

        test.willUse("2");
        test.willCapture("value", value);
        assert 2 == value;
    }

    @Test
    void constant_3() {
        float value = -1;

        test.willUse("3");
        test.willCapture("value", value);
        assert 3 == value;
    }

    @Test
    void constant_M1() {
        float value = 0;

        test.willUse("-1");
        test.willCapture("value", value);
        assert -1 == value;
    }

    @Test
    void big() {
        float value = 2;

        test.willUse("0.12345678");
        test.willCapture("value", value);
        assert 0.12345678f == value;
    }

    @Test
    void not() {
        float value = 0.3f;

        test.willUse("0.3");
        test.willUse("!=");
        test.willCapture("value", value);
        assert 0.3f != value;
    }

    @Test
    void negative() {
        float value = 0.3f;

        test.willUse("0.3");
        test.willUse("-value");
        test.willCapture("value", value);
        assert 0.3f == -value;
    }

    @Test
    void array() {
        float[] array = {0, 1, 2};
        float[] other = {0, 1, 2};

        test.willCapture("array", array);
        assert array == other;
    }

    @Test
    void arrayIndex() {
        float[] array = {0, 1, 2};

        test.willCapture("array", array);
        test.willCapture("array[1]", 1f);
        assert array[1] == 128;
    }

    @Test
    void arrayLength() {
        float[] array = {0, 1, 2};

        test.willCapture("array", array);
        test.willCapture("array.length", 3);
        assert array.length == 10;
    }

    @Test
    void arrayNew() {
        test.willUse("new float[] {1.0, 2.0}");
        assert new float[] {1, 2} == null;
    }

    @Test
    void varargs() {
        test.willCapture("var()", false);
        assert var();
    }

    boolean var(float... var) {
        return false;
    }

    @Test
    void method() {
        test.willCapture("test()", 1f);
        assert test() == 2f;
    }

    float test() {
        return 1;
    }

    @Test
    void parameter() {
        test.willCapture("test(12.0)", false);
        assert test(12);
    }

    private boolean test(float value) {
        return false;
    }

    /** The tester. */
    private float floatField = 0.123f;

    /** The tester. */
    private static float floatFieldStatic = 0.123f;

    @Test
    void fieldFloatAccess() {
        test.willCapture("floatField", 0.123f);
        assert floatField == 0;
    }

    @Test
    void fieldIntAccessWithHiddenName() {
        float floatField = 0.123f;

        test.willCapture("this.floatField", floatField);
        assert this.floatField == 0;
    }

    @Test
    void fieldFloatStaticAccess() {
        test.willCapture("floatFieldStatic", 0.123f);
        assert floatFieldStatic == 0;
    }
}
