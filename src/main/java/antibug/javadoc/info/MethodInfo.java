/*
 * Copyright (C) 2016 Nameless Production Committee
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
 * @version 2014/07/26 22:32:46
 */
public class MethodInfo extends IdentifiableInfo {

    /** The parameters. */
    public List<ParamInfo> params = new ArrayList();

    /** The return type id. */
    public Identifier returnType;
}
