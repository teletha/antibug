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

import java.util.HashMap;
import java.util.Map;

/**
 * @version 2014/07/29 13:58:36
 */
public class Identifier implements Comparable<Identifier> {

    /** The cache storage. */
    private static final Map<String, Identifier> cache = new HashMap();

    /** The module name. */
    public final String packageName;

    /** The type name. */
    public final String typeName;

    /** The member name. */
    public final String memberName;

    /**
     * @param packageName
     * @param typeName
     * @param memberName
     */
    private Identifier(String packageName, String typeName, String memberName) {
        this.packageName = packageName;
        this.typeName = typeName;
        this.memberName = memberName;
    }

    /**
     * <p>
     * Convert to package identifier.
     * </p>
     * 
     * @return An identifier.
     */
    public Identifier asPackage() {
        return of(packageName, "", "");
    }

    /**
     * <p>
     * Convert to type identifier.
     * </p>
     * 
     * @return An identifier.
     */
    public Identifier asType() {
        return of(packageName, typeName, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Identifier o) {
        return toString().compareTo(o.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Identifier && toString().equals(obj.toString());
    }

    /**
     * <p>
     * Test equality of package.
     * </p>
     * 
     * @param id A target {@link Identifier} to test.
     * @return A result.
     */
    public boolean equalsPackage(Identifier id) {
        return packageName.equals(id.packageName);
    }

    /**
     * <p>
     * Test equality of type.
     * </p>
     * 
     * @param id A target {@link Identifier} to test.
     * @return A result.
     */
    public boolean equalsType(Identifier id) {
        return typeName.equals(id.typeName) && equalsPackage(id);
    }

    /**
     * <p>
     * Test equality of type.
     * </p>
     * 
     * @param id A target {@link Identifier} to test.
     * @return A result.
     */
    public boolean equalsMember(Identifier id) {
        return memberName.equals(id.memberName) && equalsType(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(packageName);

        if (typeName.length() != 0) {
            builder.append('+').append(typeName);

            if (memberName.length() != 0) {
                builder.append('+').append(memberName);
            }
        }
        return builder.toString();
    }

    /**
     * @return
     */
    public Object toTypeString() {
        return asType().toString();
    }

    /**
     * @return
     */
    public Object toPackageString() {
        return asPackage().toString();
    }

    /**
     * <p>
     * Build {@link Identifier} by name.
     * </p>
     * 
     * @param id
     * @return
     */
    public static final Identifier of(String id) {
        if (id == null) {
            id = "";
        }

        String[] parts = id.split("\\+");

        switch (parts.length) {
        case 0:
            return of("", "", "");

        case 1:
            return of(parts[0], "", "");

        case 2:
            return of(parts[0], parts[1], "");

        default:
            return of(parts[0], parts[1], parts[2]);
        }
    }

    /**
     * <p>
     * Build {@link Identifier} by names.
     * </p>
     * 
     * @param packageName
     * @param typeName
     * @param memberName
     * @return
     */
    public static final Identifier of(String packageName, String typeName, String memberName) {
        typeName = typeName == null ? "" : typeName;
        memberName = typeName.length() == 0 || memberName == null ? "" : memberName;
        String key = packageName + " " + typeName + " " + memberName;

        Identifier value = cache.get(key);

        if (value == null) {
            value = new Identifier(packageName, typeName, memberName);
            cache.put(key, value);
        }
        return value;
    }
}
