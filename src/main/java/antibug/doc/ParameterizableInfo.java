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

public abstract class ParameterizableInfo extends MemberInfo {

    protected final List<Ⅱ<String, XML>> typeParameters = new ArrayList();

    /**
     * @param e
     */
    public ParameterizableInfo(Parameterizable e, TypeResolver resolver) {
        super(e, resolver);

        e.getTypeParameters().forEach(type -> {
            XML param = parseTypeAsXML(type.asType());
            List<? extends TypeMirror> bounds = type.getBounds();
            int size = bounds.size();
            if (size != 0) {
                if (size != 1 || !bounds.get(0).toString().equals("java.lang.Object")) {
                    XML extend = I.xml("<i/>").addClass("extends");
                    for (int i = 0; i < size; i++) {
                        if (i != 0) {
                            extend.append(" & ");
                        }
                        extend.append(parseTypeAsXML(bounds.get(i)));
                    }
                    param.after(extend);
                }
            }
            typeParameters.add(I.pair(type.getSimpleName().toString(), param.parent().children()));

        });

    }

    /**
     * Build paramter type element.
     * 
     * @return
     */
    public XML createPrameterType() {
        if (typeParameters.isEmpty()) {
            return null;
        }

        XML root = I.xml("i").addClass("parameters");
        for (Ⅱ<String, XML> type : typeParameters) {
            root.append(type.ⅱ.clone());
        }
        return root;
    }
}
