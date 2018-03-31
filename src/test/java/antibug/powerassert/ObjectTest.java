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
    public void constant() throws Exception {
        Object value = new Object();

        test.willCapture("value", value);
        assert null == value;
    }

    @Test
    public void not() throws Exception {
        Object value = null;

        test.willUse("!=");
        test.willCapture("value", value);
        assert null != value;
    }

    @Test
    public void array() throws Exception {
        Object[] array = {"0", "1", "2"};

        test.willCapture("array", array);
        assert array == null;
    }

    @Test
    public void arrayIndex() throws Exception {
        Object[] array = {"0", "1", "2"};

        test.willCapture("array", array);
        test.willCapture("array[1]", "1");
        assert array[1] == "128";
    }

    @Test
    public void arrayLength() throws Exception {
        Object[] array = {"0", "1", "2"};

        test.willCapture("array", array);
        test.willCapture("array.length", 3);
        assert array.length == 10;
    }

    @Test
    public void arrayNew() throws Exception {
        test.willUse("new Object[] {\"1\", \"2\"}");
        assert new Object[] {"1", "2"} == null;
    }

    @Test
    public void varargs() throws Exception {
        test.willCapture("var()", false);
        assert var();
    }

    boolean var(Object... var) {
        return false;
    }

    @Test
    public void varargsWithHead() throws Exception {
        test.willCapture("head(\"1\")", false);
        assert head("1");
    }

    boolean head(String head, Object... var) {
        return false;
    }

    @Test
    public void method() throws Exception {
        test.willCapture("test()", "1");
        assert test() == "2";
    }

    Object test() {
        return "1";
    }

    @Test
    public void parameter() throws Exception {
        test.willCapture("test(\"12\")", false);
        assert test("12");
    }

    private boolean test(Object value) {
        return false;
    }

    @Test
    public void parameterWithVarArg() throws Exception {
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
    public void fieldObjectAccess() throws Exception {
        test.willCapture("ObjectField", "11");
        assert ObjectField == "";
    }

    @Test
    public void fieldObjectStaticAccess() throws Exception {
        test.willCapture("ObjectFieldStatic", "11");
        assert ObjectFieldStatic == "";
    }
}
