/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.javadoc.info;

import org.junit.Rule;
import org.junit.Test;

import antibug.javadoc.JavadocParser;

/**
 * @version 2014/07/26 21:55:11
 */
public class MethodInfoTest {

    @Rule
    public static final JavadocParser parser = new JavadocParser();

    /**
     * Text
     */
    @Test
    public void method() {
        MethodInfo info = parser.getMethod();
        assert info.id.memberName.equals("method()");
    }
}
