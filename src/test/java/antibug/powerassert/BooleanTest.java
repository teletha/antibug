/*
 * Copyright (C) 2021 Nameless Production Committee
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
 * @version 2018/03/31 12:25:18
 */
class BooleanTest {

    @RegisterExtension
    static PowerAssertTester test = new PowerAssertTester();

    @Test
    void constant() {
        boolean value = false;

        test.willCapture("value", value);
        assert value;
    }

    @Test
    void not() {
        boolean value = true;

        test.willUse("!");
        test.willCapture("value", value);
        assert !value;
    }

    @Test
    void array() {
        boolean[] array = {false, false, false};
        boolean[] other = {false, false, false};

        test.willCapture("array", array);
        assert array == other;
    }

    @Test
    void arrayIndex() {
        boolean[] array = {false, false, false};

        test.willCapture("array", array);
        test.willCapture("array[1]", false);
        assert array[1];
    }

    @Test
    void arrayLength() {
        boolean[] array = {false, false, false};

        test.willCapture("array", array);
        test.willCapture("array.length", 3);
        assert array.length == 10;
    }

    @Test
    void arrayNew() {
        test.willUse("new boolean[] {true, false}");
        assert new boolean[] {true, false} == null;
    }

    @Test
    void varargs() {
        test.willCapture("var()", false);
        assert var();
    }

    boolean var(boolean... var) {
        return false;
    }

    @Test
    void method() {
        test.willCapture("test()", false);
        assert test();
    }

    boolean test() {
        return false;
    }

    @Test
    void parameter() {
        test.willCapture("test(false)", false);
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
    void fieldBooleanAccess() {
        test.willCapture("booleanField", false);
        assert booleanField;
    }

    @Test
    void fieldIntAccessWithHiddenName() {
        boolean booleanField = false;

        test.willCapture("this.booleanField", booleanField);
        assert this.booleanField;
    }

    @Test
    void fieldBooleanStaticAccess() {
        test.willCapture("booleanFieldStatic", false);
        assert booleanFieldStatic;
    }
}