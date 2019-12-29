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
    <A> void single() {
        assert checkTypePrameter(currentMethod(), "<type>A</type>");
    }

    @Test
    <A, B> void multi() {
        assert checkTypePrameter(currentMethod(), "<type>A</type>", "<type>B</type>");
    }

    @Test
    <A extends Comparable> void bounded() {
        assert checkTypePrameter(currentMethod(), "<type>A</type><extends><type package='java.lang'>Comaprable</type></extends>");
    }

    @Test
    <A extends Comparable<A>> void boundedVariable() {
        assert checkTypePrameter(currentMethod(), "<type>A</type><extends><type package='java.lang'>Comaprable</type><parameters><type>A</type></parameters></extends>");
    }

    @Test
    <A extends Comparable & Serializable> void intersected() {
        assert checkTypePrameter(currentMethod(), "<type>A</type><extends><type package='java.lang'>Comaprable</type><type package='java.io'>Serializable</type></extends>");
    }

    /**
     * Shortcut method.
     * 
     * @param info
     * @param expected
     * @return
     */
    private boolean checkTypePrameter(ParameterizableInfo info, String... expected) {
        for (int i = 0; i < expected.length; i++) {
            assert sameXML(info.typeParameters.get(i).ⅱ, expected[i]);
        }
        return true;
    }
}
