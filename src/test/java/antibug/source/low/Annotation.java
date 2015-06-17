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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @version 2014/08/02 12:59:29
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Annotation {

    /**
     * <p>
     * Default value is "no".
     * </p>
     * 
     * @return A current value.
     */
    String value() default "no";
}
