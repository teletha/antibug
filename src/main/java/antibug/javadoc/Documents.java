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

import kiss.Manageable;
import kiss.Singleton;
import antibug.javadoc.info.Identifier;
import antibug.javadoc.info.PackageInfo;

/**
 * @version 2014/07/26 21:47:07
 */
@Manageable(lifestyle = Singleton.class)
public class Documents {

    /** The package list. */
    public List<PackageInfo> packages = new ArrayList();

    /**
     * <p>
     * Find {@link PackageInfo} by {@link Identifier}.
     * </p>
     * 
     * @param id An identifier.
     * @return A package.
     */
    public PackageInfo getPackageBy(Identifier id) {
        for (PackageInfo info : packages) {
            if (info.getId() == id) {
                return info;
            }
        }
        return new PackageInfo(id);
    }
}
