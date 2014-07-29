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

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import kiss.Manageable;
import kiss.Singleton;
import antibug.javadoc.info.PackageInfo;
import antibug.javadoc.info.TypeInfo;

/**
 * @version 2014/07/26 21:47:07
 */
@Manageable(lifestyle = Singleton.class)
public class Documents {

    /** The package list. */
    public TreeMap<Identifier, PackageInfo> packages = new TreeMap();

    /** The type list. */
    public List<TypeInfo> types = new ArrayList();
}
