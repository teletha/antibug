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
 * @version 2018/03/31 16:31:05
 */
public class IntTest {

    @RegisterExtension
    static PowerAssertTester test = new PowerAssertTester();

    @Test
    public void constant_0() throws Exception {
        int value = -1;

        test.willUse("0");
        test.willCapture("value", value);
        assert 0 == value;
    }

    @Test
    public void constant_1() throws Exception {
        int value = -1;

        test.willUse("1");
        test.willCapture("value", value);
        assert 1 == value;
    }

    @Test
    public void constant_2() throws Exception {
        int value = -1;

        test.willUse("2");
        test.willCapture("value", value);
        assert 2 == value;
    }

    @Test
    public void constant_3() throws Exception {
        int value = -1;

        test.willUse("3");
        test.willCapture("value", value);
        assert 3 == value;
    }

    @Test
    public void constant_M1() throws Exception {
        int value = 0;

        test.willUse("-1");
        test.willCapture("value", value);
        assert -1 == value;
    }

    @Test
    public void big() throws Exception {
        int value = 2;

        test.willUse("123456789");
        test.willCapture("value", value);
        assert 123456789 == value;
    }

    @Test
    public void not() throws Exception {
        int value = 10;

        test.willUse("10");
        test.willUse("!=");
        test.willCapture("value", value);
        assert 10 != value;
    }

    @Test
    public void negative() throws Exception {
        int value = 10;

        test.willUse("10");
        test.willUse("-value");
        test.willCapture("value", value);
        assert 10 == -value;
    }

    @Test
    public void array() throws Exception {
        int[] array = {0, 1, 2};

        test.willCapture("array", array);
        assert array == null;
    }

    @Test
    public void arrayIndex() throws Exception {
        int[] array = {0, 1, 2};

        test.willCapture("array", array);
        test.willCapture("array[1]", 1);
        assert array[1] == 128;
    }

    @Test
    public void arrayLength() throws Exception {
        int[] array = {0, 1, 2};

        test.willCapture("array", array);
        test.willCapture("array.length", 3);
        assert array.length == 10;
    }

    @Test
    public void arrayNew() throws Exception {
        test.willUse("new int[] {1, 2}");
        assert new int[] {1, 2} == null;
    }

    @Test
    public void varargs() throws Exception {
        test.willCapture("var()", false);
        assert var();
    }

    boolean var(int... var) {
        return false;
    }

    @Test
    public void varargsWithHead() throws Exception {
        test.willCapture("head(1)", false);
        assert head(1);
    }

    boolean head(int head, int... var) {
        return false;
    }

    @Test
    public void method() throws Exception {
        test.willCapture("test()", 1);
        assert test() == 2;
    }

    int test() {
        return 1;
    }

    @Test
    public void parameter() throws Exception {
        test.willCapture("test(12)", false);
        assert test(12);
    }

    private boolean test(int value) {
        return false;
    }

    /** The tester. */
    private int intField = 11;

    /** The tester. */
    private static int intFieldStatic = 11;

    @Test
    public void fieldIntAccess() throws Exception {
        test.willCapture("intField", 11);
        assert intField == 0;
    }

    @Test
    public void fieldIntAccessWithHiddenName() throws Exception {
        int intField = 11;

        test.willCapture("this.intField", intField);
        assert this.intField == 0;
    }

    @Test
    public void fieldIntStaticAccess() throws Exception {
        test.willCapture("intFieldStatic", 11);
        assert intFieldStatic == 0;
    }
}
