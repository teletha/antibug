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

import org.junit.jupiter.api.Test;

public class ParameterizableInfoTest extends JavadocTestSupport {

    @Test
    public <A> void single() {
        assert checkTypeParmeterName(currentMethod(), "A");
        assert checkTypeParameter(currentMethod(), "<i class='type'>A</i>");
    }

    @Test
    public <A, B> void multi() {
        assert checkTypeParmeterName(currentMethod(), "A", "B");
        assert checkTypeParameter(currentMethod(), "<i class='type'>A</i>", "<i class='type'>B</i>");
    }

    @Test
    public <A extends Comparable> void bounded() {
        assert checkTypeParmeterName(currentMethod(), "A");
        assert checkTypeParameter(currentMethod(), "<i class='type'>A</i><extends><i class='type' package='java.lang'>Comaprable</i></extends>");
    }

    @Test
    public <A extends Comparable<A>> void boundedVariable() {
        assert checkTypeParmeterName(currentMethod(), "A");
        assert checkTypeParameter(currentMethod(), "<i class='type'>A</i><extends><i class='type' package='java.lang'>Comaprable</i><i class='parameters'><i class='type'>A</i></i></extends>");
    }

    @Test
    public <A extends Comparable & Serializable> void intersection() {
        assert checkTypeParmeterName(currentMethod(), "A");
        assert checkTypeParameter(currentMethod(), "<i class='type'>A</i><extends><i class='type' package='java.lang'>Comaprable</i><i class='type' package='java.io'>Serializable</i></extends>");
    }

    /**
     * Shortcut method.
     * 
     * @param info
     * @param expected
     * @return
     */
    private boolean checkTypeParmeterName(ParameterizableInfo info, String... expected) {
        for (int i = 0; i < expected.length; i++) {
            assert info.typeParameters.get(i).ⅰ.equals(expected[i]);
        }
        return true;
    }

    /**
     * Shortcut method.
     * 
     * @param info
     * @param expected
     * @return
     */
    private boolean checkTypeParameter(ParameterizableInfo info, String... expected) {
        for (int i = 0; i < expected.length; i++) {
            assert sameXML(info.typeParameters.get(i).ⅱ, expected[i]);
        }
        return true;
    }
}
