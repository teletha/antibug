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

/**
 * @version 2014/07/26 23:07:56
 */
public class PackageInfoTest {

    @Rule
    @ClassRule
    public static final JavadocParser parser = new JavadocParser();

    @Test
    public void name() {
        PackageInfo info = parser.getPackage();
        assert parser.equals(info, PackageInfoTest.class);
    }
}
