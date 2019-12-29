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

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import kiss.I;
import kiss.XML;
import kiss.Ⅱ;

public class ExecutableInfo extends DocumentInfo {

    public final String name;

    public final List<Ⅱ<String, XML>> params = new ArrayList();

    /**
     * @param e
     */
    ExecutableInfo(ExecutableElement e) {
        super(e);

        this.name = e.getSimpleName().toString();
        for (VariableElement p : e.getParameters()) {
            params.add(I.pair(p.toString(), parseTypeAsXML(p.asType())));
        }
    }
}
