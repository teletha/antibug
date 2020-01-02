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
        assert checkReturnType(currentMethod(), "<i class='type'>void</i>");
    }

    @Test
    void returnPrimitive() {
        assert checkReturnType(method("integer"), "<i class='type'>int</i>");
    }

    int integer() {
        return 1;
    }

    @Test
    void returnString() {
        assert checkReturnType(method("string"), "<i class='type' package='java.lang'>String</i>");
    }

    String string() {
        return "";
    }

    @Test
    void returnGenerics() {
        assert checkReturnType(method("generics"), "<i class='type'>T</i>");
    }

    <T> T generics() {
        return null;
    }

    @Test
    void returnBounded() {
        assert checkReturnType(method("bounded"), "<i class='type'>T</i>");
    }

    <T extends Serializable> T bounded() {
        return null;
    }

    @Test
    void returnParameterized() {
        assert checkReturnType(method("parameterized"), "<i class='type' package='java.util'>List</i><i class='parameters'><i class='type' package='java.lang'>String</i></i>");
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
