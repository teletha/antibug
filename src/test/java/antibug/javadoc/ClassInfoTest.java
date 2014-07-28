/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.javadoc;

import org.junit.Rule;
import org.junit.Test;

import antibug.javadoc.ClassInfoTest.Inner.InnerNest;

/**
 * @version 2014/07/26 23:33:55
 */
public class ClassInfoTest {

    @Rule
    public static final JavadocParser parser = new JavadocParser();

    @Test
    public void name() {
        TypeInfo info = parser.getType();
        assert info.name.equals(ClassInfoTest.class.getName());
    }

    @Test
    public void innerType() {
        Class target = Inner.class;
        TypeInfo info = parser.getType(target);
        assert info.name.equals(target.getName());
        assert info.simpleName.equals(target.getSimpleName());
        assert info.inners.size() == 1;
    }

    @Test
    public void innerNestType() {
        Class target = InnerNest.class;
        TypeInfo info = parser.getType(target);
        assert info.name.equals(target.getName());
        assert info.simpleName.equals(target.getSimpleName());
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
