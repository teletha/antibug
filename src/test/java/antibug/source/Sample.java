/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import antibug.javadoc.info.ExternalPackageInfo;
import antibug.javadoc.info.Identifier;
import antibug.javadoc.info.PackageInfo;
import antibug.javadoc.info.annotation.Primitive;
import antibug.javadoc.info.annotation.SourceMarker;
import antibug.javadoc.info.annotation.Typo;

/**
 * @version 2014/07/26 21:47:07
 */
@SuppressWarnings("all")
@Primitive(intValue = 20, booleanValue = false)
public class Sample<T> implements Serializable {

    static {
        Sample.<String> callGeneric("").charAt(0);
    }

    /** The package list. */
    @SourceMarker
    public List<@Typo PackageInfo> packages = new @Typo ArrayList<@Typo PackageInfo>(2);

    /**
     * <p>
     * Find {@link PackageInfo} by {@link Identifier}.
     * </p>
     * 
     * @param id An identifier.
     * @return A package.
     */
    public <T> PackageInfo getPackageBy(Identifier id) throws Exception, RuntimeException {
        for (PackageInfo info : packages) {
            if (info.id.equalsPackage(id)) {
                return info;
            }
        }
        return new ExternalPackageInfo(id);
    }

    private static <T> String callGeneric(T value) {
        return null;
    }
}
