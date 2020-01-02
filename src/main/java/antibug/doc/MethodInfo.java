/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.doc;

import javax.lang.model.element.ExecutableElement;

import kiss.XML;

public class MethodInfo extends ExecutableInfo {

    public final XML returnType;

    /**
     * @param e
     */
    public MethodInfo(ExecutableElement e) {
        super(e.getSimpleName().toString(), e);

        this.returnType = parseTypeAsXML(e.getReturnType());
    }
}
