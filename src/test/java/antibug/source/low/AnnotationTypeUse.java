/*
 * Copyright (C) 2015 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source.low;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import antibug.source.low.AnnotationTypeUse.Type;
import antibug.source.low.AnnotationTypeUse.Use;

/**
 * @version 2014/08/03 19:51:43
 */
@Type
@SuppressWarnings("all")
public class AnnotationTypeUse<@Type @Use(1) T> extends antibug.source.low.EnumClass implements @Use(10) Serializable {

    /** field. */
    @Type
    private String name;

    /**
     * Constructor
     */
    @Type
    @Use(1)
    public AnnotationTypeUse(@Type @Use(2) String name) {
        this.name = name;
    }

    /**
     * Method.
     * 
     * @param id parameter
     * @return return value
     * @throws RuntimeException exception
     */
    @Type
    public <@Use(4) @Type G> G getName(@Type Object id) throws @Type @Use(3) RuntimeException {
        @Type
        Object use = new antibug.source.low.AnnotationTypeUse("C.C");
        String[] names = new @Type String[0];

        return (@Type G) id;
    }

    /**
     * @version 2014/08/03 20:43:05
     */
    @Target(ElementType.TYPE_USE)
    @interface Type {
    }

    /**
     * @version 2014/08/03 20:43:05
     */
    @Target(ElementType.TYPE_USE)
    @interface Use {

        /**
         * Return id.
         * 
         * @return An identifier.
         */
        int value();
    }
}
