/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.doc;

import java.io.Serializable;
import java.util.List;

import org.junit.jupiter.api.Test;

public class MethodInfoTest extends JavadocTestSupport {

    @Test
    void returnType() {
        assert checkReturnType(currentMethod(), "<type>void</type>");
    }

    @Test
    void returnPrimitive() {
        assert checkReturnType(method("integer"), "<type>int</type>");
    }

    int integer() {
        return 1;
    }

    @Test
    void returnString() {
        assert checkReturnType(method("string"), "<type package='java.lang'>String</type>");
    }

    String string() {
        return "";
    }

    @Test
    void returnGenerics() {
        assert checkReturnType(method("generics"), "<type>T</type>");
    }

    <T> T generics() {
        return null;
    }

    @Test
    void returnBounded() {
        assert checkReturnType(method("bounded"), "<type>T</type>");
    }

    <T extends Serializable> T bounded() {
        return null;
    }

    @Test
    void returnParameterized() {
        assert checkReturnType(method("parameterized"), "<type package='java.util'>List</type><parameters><type package='java.lang'>String</type></parameters>");
    }

    List<String> parameterized() {
        return null;
    }

    /**
     * Shortcut method.
     * 
     * @param info
     * @param expected
     * @return
     */
    private boolean checkReturnType(MethodInfo info, String expected) {
        assert sameXML(info.returnType, expected);
        return true;
    }
}
