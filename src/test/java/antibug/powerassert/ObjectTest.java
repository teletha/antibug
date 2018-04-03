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
 * @version 2018/03/31 16:31:52
 */
public class ObjectTest {

    @RegisterExtension
    static PowerAssertTester test = new PowerAssertTester();

    @Test
    public void constant() {
        Object value = new Object();

        test.willCapture("value", value);
        assert null == value;
    }

    @Test
    public void not() {
        Object value = null;

        test.willUse("!=");
        test.willCapture("value", value);
        assert null != value;
    }

    @Test
    public void array() {
        Object[] array = {"0", "1", "2"};

        test.willCapture("array", array);
        assert array == null;
    }

    @Test
    public void arrayIndex() {
        Object[] array = {"0", "1", "2"};

        test.willCapture("array", array);
        test.willCapture("array[1]", "1");
        assert array[1] == "128";
    }

    @Test
    public void arrayLength() {
        Object[] array = {"0", "1", "2"};

        test.willCapture("array", array);
        test.willCapture("array.length", 3);
        assert array.length == 10;
    }

    @Test
    public void arrayNew() {
        test.willUse("new Object[] {\"1\", \"2\"}");
        assert new Object[] {"1", "2"} == null;
    }

    @Test
    public void varargZero() {
        test.willCapture("var()", false);
        assert var();
    }

    @Test
    public void varargs() {
        String value = "B";

        test.willCapture("value", value);
        test.willCapture("var(\"A\", value)", false);
        assert var("A", value);
    }

    boolean var(Object... var) {
        return false;
    }

    @Test
    public void varargsWithHead() {
        test.willCapture("head(\"1\")", false);
        assert head("1");
    }

    boolean head(String head, Object... var) {
        return false;
    }

    @Test
    public void method() {
        test.willCapture("test()", "1");
        assert test() == "2";
    }

    Object test() {
        return "1";
    }

    @Test
    public void methodChain() {
        String base = "X";

        test.willCapture("base", "X");
        test.willCapture("base.toLowerCase()", "x");
        test.willCapture("base.toLowerCase().contains(\"X\")", false);
        assert base.toLowerCase().contains("X");
    }

    @Test
    public void parameter() {
        test.willCapture("test(\"12\")", false);
        assert test("12");
    }

    private boolean test(Object value) {
        return false;
    }

    @Test
    public void parameterWithVarArg() {
        test.willCapture("var2()", false);
        assert var2();
    }

    private boolean var2(String... vars) {
        return false;
    }

    /** The tester. */
    private Object ObjectField = "11";

    /** The tester. */
    private static Object ObjectFieldStatic = "11";

    @Test
    public void fieldObjectAccess() {
        test.willCapture("ObjectField", "11");
        assert ObjectField == "";
    }

    @Test
    public void fieldObjectStaticAccess() {
        test.willCapture("ObjectFieldStatic", "11");
        assert ObjectFieldStatic == "";
    }
}
