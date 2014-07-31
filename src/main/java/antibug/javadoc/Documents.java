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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import antibug.javadoc.info.ExternalMethodInfo;
import antibug.javadoc.info.ExternalPackageInfo;
import antibug.javadoc.info.ExternalTypeInfo;
import antibug.javadoc.info.Identifier;
import antibug.javadoc.info.MethodInfo;
import antibug.javadoc.info.PackageInfo;
import antibug.javadoc.info.TypeInfo;
import antibug.javadoc.info.annotation.Primitive;

/**
 * @version 2014/07/26 21:47:07
 */
@SuppressWarnings("all")
@Primitive(intValue = 20, booleanValue = false)
public class Documents<T> implements Serializable {

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
            if (info.id.equalsPackage(id)) {
                return info;
            }
        }
        return new ExternalPackageInfo(id);
    }

    /**
     * <p>
     * Find {@link PackageInfo} by {@link Identifier}.
     * </p>
     * 
     * @param id An identifier.
     * @return A package.
     */
    public TypeInfo getTypeBy(Identifier id) {
        PackageInfo packageInfo = getPackageBy(id);

        for (TypeInfo info : packageInfo.types) {
            if (info.id.equalsType(id)) {
                return info;
            }
        }
        return new ExternalTypeInfo(id);
    }

    /**
     * <p>
     * Find {@link PackageInfo} by {@link Identifier}.
     * </p>
     * 
     * @param id An identifier.
     * @return A package.
     */
    public MethodInfo getMethodBy(Identifier id) {
        /*
         * Block comment
         */
        TypeInfo typeInfo = getTypeBy(id);

        /**
         * InDoc
         */
        for (MethodInfo info : typeInfo.methods) {
            if (info.id.equalsMember(id)) {
                return info;
            }
        }

        // API definition
        return new ExternalMethodInfo(id);
    }
}
