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

/**
 * @version 2014/07/31 9:03:54
 */
public class AnnotationValue {

    private static final int STRING = 1 << 1;

    private static final int INT = 1 << 2;

    private static final int LONG = 1 << 3;

    private static final int FLOAT = 1 << 4;

    private static final int DOUBLE = 1 << 5;

    private static final int SHORT = 1 << 6;

    private static final int BYTE = 1 << 7;

    private static final int BOOLEAN = 1 << 8;

    private static final int CHAR = 1 << 9;

    private static final int CLASS = 1 << 10;

    private static final int ARRAY = 1 << 11;

    private static final int ENUM = 1 << 12;

    public int type;

    public Identifier componentType;

    public boolean isArray() {
        return (ARRAY & type) != 0;
    }

    public boolean isEnum() {
        return (ENUM & type) != 0;
    }

    public boolean isClass() {
        return (CLASS & type) != 0;
    }

    public Identifier getComponentType() {
        return componentType;
    }
}
