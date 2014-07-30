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

/**
 * @version 2014/07/30 15:46:05
 */
public class AnnotationInfo {

    /** The annotation type. */
    public Identifier type;

    /** The key list. */
    public List<Identifier> keys = new ArrayList();

    /** The value list. */
    public List<Object> values = new ArrayList();
}
