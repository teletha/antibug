/*
 * Copyright (C) 2015 Nameless Production Committee
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
 * @version 2014/07/29 18:49:10
 */
public class ParamInfo {

    /** The parameter type. */
    public Identifier type;

    /** The parameter name. */
    public String name;

    /** The parameter annotation list. */
    public List<AnnotationInfo> annotation = new ArrayList();
}
