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

import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * @version 2018/03/31 16:32:05
 */
class PowerAssertTest {

    @RegisterExtension
    static PowerAssertTester test = new PowerAssertTester();

    @Test
    void shortConstantAndVariable() {
        short value = 2;

        test.willUse("1");
        test.willCapture("value", (int) value);
        assert (short) 1 == value;
    }

    @Test
    void shortBigConstantAndVariable() {
        short value = 2;

        test.willUse("128");
        test.willCapture("value", (int) value);
        assert (short) 128 == value;
    }

    @Test
    void nullLiteral1() {
        String value = "";

        test.willCapture("value", value);
        assert value == null;
    }

    @Test
    void nullLiteral2() {
        String value = null;

        test.willCapture("value", value);
        assert value != null;
    }

    @Test
    void nullValue() {
        String value = null;

        test.willCapture("value", value);
        assert value == "test";
    }

    @Test
    void nullParameter() {
        test.willCapture("nullMethod(null)", false);
        assert nullMethod(null);
    }

    private boolean nullMethod(Object param) {
        return false;
    }

    @Test
    void classLiteral() {
        Class value = int.class;

        test.willCapture("value", value);
        test.willUse("Integer.class");
        assert Integer.class == value;
    }

    @Test
    void classLiteralWithMethodCall() {
        test.willCapture("Integer.class.getName()", "java.lang.Integer");
        assert Integer.class.getName() == "fail";
    }

    @Test
    void enumLiteral() {
        RetentionPolicy value = RetentionPolicy.CLASS;

        test.willCapture("value", RetentionPolicy.CLASS);
        test.willCapture("RetentionPolicy.RUNTIME", RetentionPolicy.RUNTIME);
        assert RetentionPolicy.RUNTIME == value;
    }

    @Test
    void methodCall() {
        String value = "test";

        test.willCapture("value", value);
        test.willCapture("value.equals(\"a\")", false);
        assert value.equals("a");
    }

    @Test
    void methodCallWithNot() {
        String value = "test";

        test.willCapture("value", value);
        test.willUse("!value.equals(\"test\")");
        assert !value.equals("test");
    }

    @Test
    void privateMethodCall() {
        assert privateMethod();
    }

    private boolean privateMethod() {
        return false;
    }

    @Test
    void methodCalls() {
        String value = "test";

        test.willCapture("value", value);
        test.willCapture("value.substring(2)", "st");
        test.willCapture("value.substring(2).equals(\"xx\")", false);
        assert value.substring(2).equals("xx");
    }

    @Test
    void methodCallInt() {
        List list = new ArrayList();
        list.add("a");
        list.add("b");
        list.add("c");

        test.willCapture("list", list);
        test.willCapture("list.size()", 3);
        assert list.size() == 34;
    }

    @Test
    void methodCallObject() {
        List list = new ArrayList();
        list.add("a");
        list.add("b");
        list.add("c");

        test.willCapture("list", list);
        test.willCapture("list.get(1)", "b");
        assert list.get(1) == "fail";
    }

    @Test
    void methodStaticCall() {
        Object value = "";

        test.willCapture("Integer.valueOf(10)", Integer.valueOf(10));
        test.willCapture("value", "");
        assert Integer.valueOf(10) == value;
    }

    @Test
    void lessThan() {
        int one = 10;
        int other = 20;

        test.willCapture("one", one);
        test.willCapture("other", other);
        test.willUse("<");
        assert other < one;
    }

    @Test
    void lessEqual() {
        int one = 10;
        int other = 20;

        test.willCapture("one", one);
        test.willCapture("other", other);
        test.willUse("<=");
        assert other <= one;
    }

    @Test
    void greaterThan() {
        int one = 10;
        int other = 20;

        test.willCapture("one", one);
        test.willCapture("other", other);
        test.willUse(">");
        assert one > other;
    }

    @Test
    void greaterEqual() {
        int one = 10;
        int other = 20;

        test.willCapture("one", one);
        test.willCapture("other", other);
        test.willUse(">=");
        assert one >= other;
    }

    @Test
    void addition() {
        int one = 10;
        int other = 20;

        test.willCapture("one", one);
        test.willCapture("other", other);
        test.willUse("+");
        test.willUse("==");
        assert one + 1 == other;
    }

    @Test
    void subtraction() {
        int one = 10;
        int other = 20;

        test.willCapture("one", one);
        test.willCapture("other", other);
        test.willUse("-");
        test.willUse("==");
        assert one - 1 == other;
    }

    @Test
    void multiplication() {
        int one = 10;
        int other = 20;

        test.willCapture("one", one);
        test.willCapture("other", other);
        test.willUse("*");
        test.willUse("==");
        assert one * 3 == other;
    }

    @Test
    void division() {
        int one = 10;
        int other = 20;

        test.willCapture("one", one);
        test.willCapture("other", other);
        test.willUse("/");
        test.willUse("==");
        assert one / 2 == other;
    }

    @Test
    void remainder() {
        int one = 10;
        int other = 20;

        test.willCapture("one", one);
        test.willCapture("other", other);
        test.willUse("%");
        test.willUse("==");
        assert one % 2 == other;
    }

    @Test
    void negative() {
        int one = 10;
        int other = 20;

        test.willCapture("one", one);
        test.willCapture("other", other);
        test.willUse("-");
        test.willUse("==");
        assert -one == other;
    }

    @Test
    void leftShift() {
        int one = 10;
        int other = 20;

        test.willCapture("one", one);
        test.willCapture("other", other);
        test.willUse("<<");
        test.willUse("==");
        assert one << 3 == other;
    }

    @Test
    void rightShift() {
        int one = 10;
        int other = 20;

        test.willCapture("one", one);
        test.willCapture("other", other);
        test.willUse(">>");
        test.willUse("==");
        assert one >> 3 == other;
    }

    @Test
    void unrotateRightShift() {
        int one = 10;
        int other = 20;

        test.willCapture("one", one);
        test.willCapture("other", other);
        test.willUse(">>>");
        test.willUse("==");
        assert one >>> 3 == other;
    }

    @Test
    void or() {
        int one = 10;
        int other = 20;

        test.willCapture("one", one);
        test.willCapture("other", other);
        test.willUse("|");
        test.willUse("==");
        assert (one | other) == other;
    }

    @Test
    void xor() {
        int one = 10;
        int other = 20;

        test.willCapture("one", one);
        test.willCapture("other", other);
        test.willUse("^");
        test.willUse("==");
        assert (one ^ other) == other;
    }

    @Test
    void and() {
        int one = 10;
        int other = 20;

        test.willCapture("one", one);
        test.willCapture("other", other);
        test.willUse("&");
        test.willUse("==");
        assert (one & other) == other;
    }

    @Test
    void increment() {
        int one = 10;
        int other = 20;

        test.willCapture("one", one);
        test.willCapture("other", other);
        test.willUse("++");
        test.willUse("==");
        assert one++ == other;
    }

    @Test
    void incrementPre() {
        int one = 10;
        int other = 20;

        test.willCapture("one", one + 1);
        test.willCapture("other", other);
        test.willUse("++one");
        test.willUse("==");
        assert ++one == other;
    }

    @Test
    void decrement() {
        int one = 10;
        int other = 20;

        test.willCapture("one", one);
        test.willCapture("other", other);
        test.willUse("one--");
        test.willUse("==");
        assert one-- == other;
    }

    @Test
    void decrementPre() {
        int one = 10;
        int other = 20;

        test.willCapture("one", one - 1);
        test.willCapture("other", other);
        test.willUse("--");
        test.willUse("==");
        assert --one == other;
    }

    @Test
    void instanceOf() {
        Object value = "test";

        test.willCapture("value", value);
        test.willUse("instanceof");
        assert value instanceof Map;
    }

    @Test
    void instantiate() {
        Object value = "test";

        test.willCapture("value", value);
        test.willUse("new Object()");
        assert value == new Object();
    }

    @Test
    void instantiateWithParameter() {
        Object value = "test";

        test.willCapture("value", value);
        test.willUse("new String(\"fail\")");
        assert value == new String("fail");
    }

    @Test
    void assertTwice() {
        int value = 2;
        assert value != 1; // success

        test.willUse("==");
        test.willCapture("value", value);
        assert value == 3;
    }

    @Test
    void external() {
        String value = "test";

        test.willCapture("value", value);
        test.willCapture("value.length()", 4);
        External.assertInExternal(value);
    }

    /**
     * @version 2012/01/22 19:58:35
     */

    private static class External {

        private static void assertInExternal(String value) {
            assert value.length() == 20;
        }
    }

    @Test
    void lambda() {
        String value = "test";

        test.willCapture("value", value);
        test.willCapture("value.length()", 4);
        validate(() -> {
            assert value.length() == 5;
        });
    }

    private void validate(Runnable run) {
        run.run();
    }

    @Test
    void lambdaWithParam() {
        String value = "test";

        test.willCapture("param", value);
        test.willCapture("param.length()", 4);
        validate(value, param -> {
            assert param.length() == 5;
        });
    }

    private void validate(String value, Consumer<String> run) {
        run.accept(value);
    }
}
