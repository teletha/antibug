/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source.low;

import java.io.Serializable;

/**
 * @version 2014/08/04 7:22:19
 */
public class Cast {

    String single(Object value) {
        return (String) value;
    }

    <T> T generic(Object value) {
        return (T) value;
    }

    Serializable multi(Object value) {
        return (String & Serializable & CharSequence) value;
    }
}
