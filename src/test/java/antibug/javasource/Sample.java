/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.javasource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import antibug.javadoc.info.ExternalPackageInfo;
import antibug.javadoc.info.Identifier;
import antibug.javadoc.info.PackageInfo;
import antibug.javadoc.info.annotation.Primitive;
import antibug.javadoc.info.annotation.SourceMarker;
import antibug.javadoc.info.annotation.Typo; // COMMENT

/**
 * @version 2014/07/26 21:47:07
 */
@SuppressWarnings("all")
@Primitive(intValue = 20, booleanValue = false)
public class Sample<T> implements Serializable {

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

    static {
        System.out.println("DOC");
    }

    // /**
    // * <p>
    // * Find {@link PackageInfo} by {@link Identifier}.
    // * </p>
    // *
    // * @param id An identifier.
    // * @return A package.
    // */
    // public TypeInfo getTypeBy(Identifier id) {
    // PackageInfo packageInfo = getPackageBy(id);
    //
    // for (TypeInfo info : packageInfo.types) {
    // if (info.id.equalsType(id)) {
    // return info;
    // }
    // }
    // return new ExternalTypeInfo(id);
    // }
    //
    // /**
    // * <p>
    // * Find {@link PackageInfo} by {@link Identifier}.
    // * </p>
    // *
    // * @param id An identifier.
    // * @return A package.
    // */
    // public MethodInfo getMethodBy(Identifier id) {
    // /*
    // * Block comment
    // */
    // TypeInfo typeInfo = getTypeBy(id);
    //
    // /**
    // * InDoc
    // */
    // for (MethodInfo info : typeInfo.methods) {
    // if (info.id.equalsMember(id)) {
    // return info;
    // }
    // }
    //
    // // API definition
    // return new ExternalMethodInfo(id);
    // }
}
