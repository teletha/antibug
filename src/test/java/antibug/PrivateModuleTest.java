/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug;

import java.nio.file.Files;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import antibug.relative.module.RelativeModule;

/**
 * @version 2013/07/27 10:06:26
 */
public class PrivateModuleTest {

    @Rule
    @ClassRule
    public static final PrivateModule module = new PrivateModule(true, false);

    @Rule
    @ClassRule
    public static final PrivateModule moduleJar = new PrivateModule(true, true);

    @Rule
    @ClassRule
    public static final PrivateModule renameRelative1 = new PrivateModule("relative/module", true, false);

    @Rule
    @ClassRule
    public static final PrivateModule renameRelative2 = new PrivateModule("relative/module/", true, false);

    @Test
    public void path() throws Exception {
        assert Files.isDirectory(module.path);
        assert Files.isRegularFile(moduleJar.path);
    }

    @Test
    public void convert() throws Exception {
        assert module.convert(Clazz.class) != null;
        assert moduleJar.convert(Clazz.class) != null;
        assert Clazz.class != module.convert(Clazz.class);
        assert Clazz.class != moduleJar.convert(Clazz.class);
    }

    @Test
    public void convertArray() throws Exception {
        assert module.convert(Clazz[].class) != null;
        assert moduleJar.convert(Clazz[].class) != null;
        assert Clazz[].class != module.convert(Clazz[].class);
        assert Clazz[].class != moduleJar.convert(Clazz[].class);
    }

    @Test
    public void relativePackage() throws Exception {
        Class converted = renameRelative1.convert(RelativeModule.class);
        assert converted.getName().equals(RelativeModule.class.getSimpleName());

        converted = renameRelative2.convert(RelativeModule.class);
        assert converted.getName().equals(RelativeModule.class.getSimpleName());
    }

    /**
     * @version 2010/11/07 22:53:30
     */
    private static class Clazz {
    }
}
