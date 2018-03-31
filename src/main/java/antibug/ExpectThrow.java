/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @version 2018/03/31 22:55:19
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@Test
@ExtendWith(ExpectThrowExtension.class)
public @interface ExpectThrow {
    /**
     * Specify the error.
     * 
     * @return
     */
    Class<? extends Throwable> value();
}
