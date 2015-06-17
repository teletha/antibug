/*
 * Copyright (C) 2015 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.javadoc.info;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import antibug.javadoc.JavadocParser;
import antibug.javadoc.info.TypeInfoTest.Inner.InnerNest;

/**
 * @version 2014/07/26 23:33:55
 */
public class TypeInfoTest {

    @Rule
    @ClassRule
    public static final JavadocParser parser = new JavadocParser();

    @Test
    public void name() {
        TypeInfo info = parser.getType();
        assert parser.equals(info, TypeInfoTest.class);
    }

    @Test
    public void innerType() {
        Class target = Inner.class;
        TypeInfo info = parser.getType(target);
        assert parser.equals(info, target);
        assert info.inners.size() == 1;
    }

    @Test
    public void innerNestType() {
        Class target = InnerNest.class;
        TypeInfo info = parser.getType(target);
        assert parser.equals(info, target);
        assert info.inners.size() == 0;
    }

    /**
     * @version 2014/07/26 23:44:24
     */
    public static class Inner {

        public static class InnerNest {
        }
    }
}
