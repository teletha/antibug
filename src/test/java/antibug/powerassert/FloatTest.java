/*
 * Copyright (C) 2018 Nameless Production Committee
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
 * @version 2018/03/31 16:30:46
 */
public class FloatTest {

    @RegisterExtension
    static PowerAssertTester test = new PowerAssertTester();

    @Test
    public void constant_0() throws Exception {
        float value = -1;

        test.willUse("0");
        test.willCapture("value", value);
        assert 0 == value;
    }

    @Test
    public void constant_1() throws Exception {
        float value = -1;

        test.willUse("1");
        test.willCapture("value", value);
        assert 1 == value;
    }

    @Test
    public void constant_2() throws Exception {
        float value = -1;

        test.willUse("2");
        test.willCapture("value", value);
        assert 2 == value;
    }

    @Test
    public void constant_3() throws Exception {
        float value = -1;

        test.willUse("3");
        test.willCapture("value", value);
        assert 3 == value;
    }

    @Test
    public void constant_M1() throws Exception {
        float value = 0;

        test.willUse("-1");
        test.willCapture("value", value);
        assert -1 == value;
    }

    @Test
    public void big() throws Exception {
        float value = 2;

        test.willUse("0.12345678");
        test.willCapture("value", value);
        assert 0.12345678f == value;
    }

    @Test
    public void not() throws Exception {
        float value = 0.3f;

        test.willUse("0.3");
        test.willUse("!=");
        test.willCapture("value", value);
        assert 0.3f != value;
    }

    @Test
    public void negative() throws Exception {
        float value = 0.3f;

        test.willUse("0.3");
        test.willUse("-value");
        test.willCapture("value", value);
        assert 0.3f == -value;
    }

    @Test
    public void array() throws Exception {
        float[] array = {0, 1, 2};

        test.willCapture("array", array);
        assert array == null;
    }

    @Test
    public void arrayIndex() throws Exception {
        float[] array = {0, 1, 2};

        test.willCapture("array", array);
        test.willCapture("array[1]", 1f);
        assert array[1] == 128;
    }

    @Test
    public void arrayLength() throws Exception {
        float[] array = {0, 1, 2};

        test.willCapture("array", array);
        test.willCapture("array.length", 3);
        assert array.length == 10;
    }

    @Test
    public void arrayNew() throws Exception {
        test.willUse("new float[] {1.0, 2.0}");
        assert new float[] {1, 2} == null;
    }

    @Test
    public void varargs() throws Exception {
        test.willCapture("var()", false);
        assert var();
    }

    boolean var(float... var) {
        return false;
    }

    @Test
    public void method() throws Exception {
        test.willCapture("test()", 1f);
        assert test() == 2f;
    }

    float test() {
        return 1;
    }

    @Test
    public void parameter() throws Exception {
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
    public void fieldFloatAccess() throws Exception {
        test.willCapture("floatField", 0.123f);
        assert floatField == 0;
    }

    @Test
    public void fieldIntAccessWithHiddenName() throws Exception {
        float floatField = 0.123f;

        test.willCapture("this.floatField", floatField);
        assert this.floatField == 0;
    }

    @Test
    public void fieldFloatStaticAccess() throws Exception {
        test.willCapture("floatFieldStatic", 0.123f);
        assert floatFieldStatic == 0;
    }
}
