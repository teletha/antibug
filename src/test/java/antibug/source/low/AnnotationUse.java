/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source.low;

import java.lang.annotation.RetentionPolicy;

import org.junit.Test;

/**
 * @version 2014/08/05 10:17:32
 */
public class AnnotationUse {

    /**
     * @version 2014/08/05 10:22:16
     */
    @interface Anno {

        String string() default "def";

        int[] arrayInt() default {0, 0, 0, 0};

        RetentionPolicy enumType() default RetentionPolicy.RUNTIME;

        Class classType() default System.class;

        Test anno() default @Test(expected = IllegalAccessError.class, timeout = 10L);
    }

    /**
     * @version 2014/08/05 10:38:13
     */
    @interface AnnoUse {

        Anno single();

        Anno[] array();
    }

    /**
     * @version 2014/08/05 10:22:19
     */
    @Anno(string = "value", arrayInt = {1, 2, 3}, enumType = RetentionPolicy.CLASS, classType = Class.class, anno = @Test(timeout = 1000L))
    @AnnoUse(single = @Anno(string = "single"), array = {@Anno(string = "array1"), @Anno(string = "array2")})
    private class User {
    }
}
