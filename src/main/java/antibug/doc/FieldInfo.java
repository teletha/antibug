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

import javax.lang.model.element.VariableElement;

import kiss.XML;

public class FieldInfo extends MemberInfo {

    public final XML type;

    /**
     * @param e
     */
    FieldInfo(VariableElement e) {
        super(e);

        this.type = parseTypeAsXML(e.asType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String id() {
        return name;
    }
}
