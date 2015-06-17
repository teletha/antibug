/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.powerassert2;

import static antibug.powerassert2.PowerAssertContext.*;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @version 2012/01/18 13:15:48
 */
@Ignore
public class BooleanTest {

    @Rule
    @ClassRule
    public static final PowerAssertTester tester = new PowerAssertTester();

    @Test
    public void constant() throws Exception {
        boolean value = false;

        tester.willCapture("value", value);
        assert value;
    }

    @Test
    public void constant1() throws Exception {
        boolean value = false;

        assert value;
    }

    @Test
    public void constant12() throws Exception {
        boolean value = false;
        assert log(value, true, "value");
    }

    @Test
    public void constant2() throws Exception {
        boolean value = false;
        boolean value2 = true;

        assert value == value2;
    }

    @Test
    public void constant2A() throws Exception {
        boolean value = false;
        boolean value2 = true;

        assert log(log(value, true, "value") == log(value2, false, "value2"), false, "==");
    }

    @Test
    public void not() throws Exception {
        boolean value = true;

        tester.willUse("!");
        tester.willCapture("value", value);
        assert!value;
    }

    @Test
    public void array() throws Exception {
        boolean[] array = {false, false, false};

        tester.willCapture("array", array);
        assert array == null;
    }

    @Test
    public void arrayIndex() throws Exception {
        boolean[] array = {false, false, false};

        tester.willCapture("array", array);
        tester.willCapture("array[1]", false);
        assert array[1];
    }

    @Test
    public void arrayLength() throws Exception {
        boolean[] array = {false, false, false};

        tester.willCapture("array", array);
        tester.willCapture("array.length", 3);
        assert array.length == 10;
    }

    @Test
    public void arrayNew() throws Exception {
        tester.willUse("new boolean[] {true, false}");
        assert new boolean[] {true, false} == null;
    }

    @Test
    public void varargs() throws Exception {
        tester.willCapture("var()", false);
        assert var();
    }

    boolean var(boolean... var) {
        return false;
    }

    @Test
    public void method() throws Exception {
        tester.willCapture("test()", false);
        assert test();
    }

    boolean test() {
        return false;
    }

    @Test
    public void parameter() throws Exception {
        tester.willCapture("test(false)", false);
        assert test(false);
    }

    private boolean test(boolean value) {
        return false;
    }

    /** The tester. */
    private boolean booleanField = false;

    /** The tester. */
    private static boolean booleanFieldStatic = false;

    @Test
    public void fieldBooleanAccess() throws Exception {
        tester.willCapture("booleanField", false);
        assert booleanField;
    }

    @Test
    public void fieldIntAccessWithHiddenName() throws Exception {
        boolean booleanField = false;

        tester.willCapture("this.booleanField", booleanField);
        assert this.booleanField;
    }

    @Test
    public void fieldBooleanStaticAccess() throws Exception {
        tester.willCapture("booleanFieldStatic", false);
        assert booleanFieldStatic;
    }
}
