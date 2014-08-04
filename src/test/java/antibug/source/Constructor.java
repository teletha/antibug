/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source;

import java.util.ArrayList;

/**
 * @version 2014/08/04 15:51:06
 */
@SuppressWarnings("serial")
public class Constructor extends ArrayList {

    Constructor() {
        this(10);
    }

    Constructor(int size) {
        super(size);
    }
}
