/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.javadoc.info.annotation;

/**
 * @version 2014/07/31 9:48:51
 */
public @interface Primitive {

    int intValue();

    long longValue() default 10L;

    float floatValue() default 3.14F;

    double doubleValue() default 1.618D;

    boolean booleanValue();
}