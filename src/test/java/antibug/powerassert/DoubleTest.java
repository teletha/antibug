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
 * @version 2018/03/31 16:30:51
 */
public class DoubleTest {

    @RegisterExtension
    static PowerAssertTester test = new PowerAssertTester();

    @Test
    public void constant_0() throws Exception {
        double value = -1;

        test.willUse("0");
        test.willCapture("value", value);
        assert 0 == value;
    }

    @Test
    public void constant_1() throws Exception {
        double value = -1;

        test.willUse("1");
        test.willCapture("value", value);
        assert 1 == value;
    }

    @Test
    public void constant_2() throws Exception {
        double value = -1;

        test.willUse("2");
        test.willCapture("value", value);
        assert 2 == value;
    }

    @Test
    public void constant_3() throws Exception {
        double value = -1;

        test.willUse("3");
        test.willCapture("value", value);
        assert 3 == value;
    }

    @Test
    public void constant_M1() throws Exception {
        double value = 0;

        test.willUse("-1");
        test.willCapture("value", value);
        assert -1 == value;
    }

    @Test
    public void big() throws Exception {
        double value = 2;

        test.willUse("0.1234567898765432");
        test.willCapture("value", value);
        assert 0.1234567898765432d == value;
    }

    @Test
    public void not() throws Exception {
        double value = 0.3;

        test.willUse("0.3");
        test.willUse("!=");
        test.willCapture("value", value);
        assert 0.3 != value;
    }

    @Test
    public void negative() throws Exception {
        double value = 0.3;

        test.willUse("0.3");
        test.willUse("-value");
        test.willCapture("value", value);
        assert 0.3 == -value;
    }

    @Test
    public void array() throws Exception {
        double[] array = {0, 1, 2};
        double[] other = {0, 1, 2};

        test.willCapture("array", array);
        assert array == other;
    }

    @Test
    public void arrayIndex() throws Exception {
        double[] array = {0, 1, 2};

        test.willCapture("array", array);
        test.willCapture("array[1]", 1d);
        assert array[1] == 128;
    }

    @Test
    public void arrayLength() throws Exception {
        double[] array = {0, 1, 2};

        test.willCapture("array", array);
        test.willCapture("array.length", 3);
        assert array.length == 10;
    }

    @Test
    public void arrayNew() throws Exception {
        test.willUse("new double[] {1.0, 2.0}");
        assert new double[] {1, 2} == null;
    }

    @Test
    public void varargs() throws Exception {
        test.willCapture("var()", false);
        assert var();
    }

    boolean var(double... var) {
        return false;
    }

    @Test
    public void method() throws Exception {
        test.willCapture("test()", 1d);
        assert test() == 2;
    }

    double test() {
        return 1;
    }

    @Test
    public void parameter() throws Exception {
        test.willCapture("test(0.123456)", false);
        assert test(0.123456d);
    }

    private boolean test(double value) {
        return false;
    }

    @Test
    public void parameters() throws Exception {
        test.willCapture("test(0.123456, 1.0)", false);
        assert test(0.123456d, 1.0d);
    }

    private boolean test(double first, double second) {
        return false;
    }

    /** The tester. */
    private double doubleField = 32.1011d;

    /** The tester. */
    private static double doubleFieldStatic = 32.1011d;

    @Test
    public void fieldDoubleAccess() throws Exception {
        test.willCapture("doubleField", 32.1011d);
        assert doubleField == 0;
    }

    @Test
    public void fieldIntAccessWithHiddenName() throws Exception {
        double doubleField = 32.1011d;

        test.willCapture("this.doubleField", doubleField);
        assert this.doubleField == 0;
    }

    @Test
    public void fieldDoubleStaticAccess() throws Exception {
        test.willCapture("doubleFieldStatic", 32.1011d);
        assert doubleFieldStatic == 0;
    }
}
