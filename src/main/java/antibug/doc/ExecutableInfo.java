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

        List<? extends VariableElement> params = e.getParameters();
        for (int i = 0; i < params.size(); i++) {
            VariableElement param = params.get(i);
            XML xml = parseTypeAsXML(param.asType());
            if (e.isVarArgs() && i + 1 == params.size()) {
                xml.attr("array", "var");
            }
            this.params.add(I.pair(param.toString(), xml));
        }
    }
}
