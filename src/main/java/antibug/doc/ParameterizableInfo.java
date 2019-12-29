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

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Parameterizable;
import javax.lang.model.type.TypeMirror;

import kiss.I;
import kiss.XML;
import kiss.Ⅱ;

public class ParameterizableInfo extends DocumentInfo {

    public final List<Ⅱ<String, XML>> typeParameters = new ArrayList();

    /**
     * @param e
     */
    public ParameterizableInfo(Parameterizable e) {
        super(e);

        e.getTypeParameters().forEach(type -> {
            List<? extends TypeMirror> bounds = type.getBounds();

            typeParameters.add(I.pair(type.getSimpleName().toString(), parseTypeAsXML(type.asType())));
        });
    }
}
