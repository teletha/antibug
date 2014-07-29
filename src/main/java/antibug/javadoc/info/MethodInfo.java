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
import java.util.StringJoiner;

/**
 * @version 2014/07/26 22:32:46
 */
public class MethodInfo extends IdentifiableInfo {

    /** The method name. */
    public String name;

    /** The method signature. */
    public String signature;

    /** The declaring class id. */
    public Identifier declaring;

    /** The parameters. */
    public List<ParamInfo> params = new ArrayList();

    /** The return type id. */
    public Identifier returnType;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Identifier computeId() {
        StringJoiner joiner = new StringJoiner(",", name.concat("("), ")");

        for (ParamInfo info : params) {
            joiner.add(info.type.toString());
        }
        return Identifier.of(declaring.packageName, declaring.typeName, joiner.toString());
    }
}
