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

import java.util.ArrayList;
import java.util.List;

/**
 * @version 2014/07/26 21:47:55
 */
public class PackageInfo extends IdentifiableInfo {

    /** The fully qualified package name. */
    public String name;

    /** The list of all types in this package. */
    public List<TypeInfo> types = new ArrayList();

    /**
     * <p>
     * JavaBean.
     * </p>
     */
    public PackageInfo() {
        // empty
    }

    /**
     * <p>
     * Create by {@link Identifier}.
     * </p>
     */
    public PackageInfo(Identifier id) {
        this.name = id.packageName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Identifier computeId() {
        return Identifier.of(name, "", "");
    }
}
