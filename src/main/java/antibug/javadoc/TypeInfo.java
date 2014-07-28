/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.javadoc;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 2014/07/26 23:17:12
 */
public class TypeInfo {

    /** The type name. */
    public String name;

    /** The simple type name. */
    public String simpleName;

    /** The inner class list. */
    public List<TypeInfo> inners = new ArrayList();

}
