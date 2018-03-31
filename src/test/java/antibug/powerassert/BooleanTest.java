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
 * @version 2018/03/31 12:25:18
 */
public class BooleanTest {

    @RegisterExtension
    static PowerAssertTester test = new PowerAssertTester();

    @Test
    public void constant() throws Exception {
        boolean value = false;

        test.willCapture("value", value);
        assert value;
    }

    @Test
    public void not() throws Exception {
        boolean value = true;

        test.willUse("!");
        test.willCapture("value", value);
        assert !value;
    }

    @Test
    public void array() throws Exception {
        boolean[] array = {false, false, false};

        test.willCapture("array", array);
        assert array == null;
    }

    @Test
    public void arrayIndex() throws Exception {
        boolean[] array = {false, false, false};

        test.willCapture("array", array);
        test.willCapture("array[1]", false);
        assert array[1];
    }

    @Test
    public void arrayLength() throws Exception {
        boolean[] array = {false, false, false};

        test.willCapture("array", array);
        test.willCapture("array.length", 3);
        assert array.length == 10;
    }

    @Test
    public void arrayNew() throws Exception {
        test.willUse("new boolean[] {true, false}");
        assert new boolean[] {true, false} == null;
    }

    @Test
    public void varargs() throws Exception {
        test.willCapture("var()", false);
        assert var();
    }

    boolean var(boolean... var) {
        return false;
    }

    @Test
    public void method() throws Exception {
        test.willCapture("test()", false);
        assert test();
    }

    boolean test() {
        return false;
    }

    @Test
    public void parameter() throws Exception {
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
    public void fieldBooleanAccess() throws Exception {
        test.willCapture("booleanField", false);
        assert booleanField;
    }

    @Test
    public void fieldIntAccessWithHiddenName() throws Exception {
        boolean booleanField = false;

        test.willCapture("this.booleanField", booleanField);
        assert this.booleanField;
    }

    @Test
    public void fieldBooleanStaticAccess() throws Exception {
        test.willCapture("booleanFieldStatic", false);
        assert booleanFieldStatic;
    }
}
