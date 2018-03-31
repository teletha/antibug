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
 * @version 2018/03/31 16:30:56
 */
public class CharacterTest {

    @RegisterExtension
    static PowerAssertTester test = new PowerAssertTester();

    @Test
    public void constant() throws Exception {
        char value = 'a';

        test.willUse("'b'");
        test.willCapture("value", value);
        assert 'b' == value;
    }

    @Test
    public void not() throws Exception {
        char value = 'b';

        test.willUse("'b'");
        test.willUse("!=");
        test.willCapture("value", value);
        assert 'b' != value;
    }

    @Test
    public void array() throws Exception {
        char[] array = {0, 1, 2};

        test.willCapture("array", array);
        assert array == null;
    }

    @Test
    public void arrayIndex() throws Exception {
        char[] array = {'a', '1', 'あ'};

        test.willCapture("array", array);
        test.willCapture("array[1]", '1');
        test.willUse("'@'");
        assert array[1] == '@';
    }

    @Test
    public void arrayLength() throws Exception {
        char[] array = {'a', '1', 'あ'};

        test.willCapture("array", array);
        test.willCapture("array.length", 3);
        assert array.length == 10;
    }

    @Test
    public void arrayNew() throws Exception {
        test.willUse("new char[] {'a', '1'} == null");
        assert new char[] {'a', '1'} == null;
    }

    @Test
    public void varargs() throws Exception {
        test.willCapture("var()", false);
        assert var();
    }

    boolean var(char... var) {
        return false;
    }

    @Test
    public void varargsWithHead() throws Exception {
        test.willCapture("head('c')", false);
        assert head('c');
    }

    boolean head(char head, char... var) {
        return false;
    }

    @Test
    public void method() throws Exception {
        test.willCapture("test()", 'r');
        test.willUse("'a'");
        assert test() == 'a';
    }

    char test() {
        return 'r';
    }

    @Test
    public void parameter() throws Exception {
        test.willCapture("test('p')", false);
        assert test('p');
    }

    private boolean test(char value) {
        return false;
    }

    /** The tester. */
    private char charField = 'a';

    /** The tester. */
    private static char charFieldStatic = 'a';

    @Test
    public void fieldCharacterAccess() throws Exception {
        test.willCapture("charField", 'a');
        test.willUse("'b'");
        assert charField == 'b';
    }

    @Test
    public void fieldIntAccessWithHiddenName() throws Exception {
        char charField = 'a';

        test.willCapture("this.charField", charField);
        assert this.charField == 0;
    }

    @Test
    public void fieldCharacterStaticAccess() throws Exception {
        test.willCapture("charFieldStatic", 'a');
        test.willUse("'b'");
        assert charFieldStatic == 'b';
    }
}
