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
import java.util.StringJoiner;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import kiss.I;
import kiss.XML;
import kiss.Ⅱ;

public class ExecutableInfo extends ParameterizableInfo {

    public final List<Ⅱ<String, XML>> params = new ArrayList();

    private final String id;

    /**
     * @param e
     */
    ExecutableInfo(ExecutableElement e, TypeResolver resolver) {
        super(e, resolver);

        StringJoiner joiner = new StringJoiner(",");
        List<? extends VariableElement> params = e.getParameters();
        for (int i = 0; i < params.size(); i++) {
            VariableElement param = params.get(i);
            joiner.add(param.asType().toString());

            XML xml = parseTypeAsXML(param.asType());
            if (e.isVarArgs() && i + 1 == params.size()) {
                xml.attr("array", "var");
            }
            this.params.add(I.pair(param.toString(), xml));
        }
        this.id = name + "(" + joiner + ")";
    }

    /**
     * Build parameter element.
     * 
     * @return
     */
    public final XML createParameter() {
        XML xml = I.xml("span").addClass(styles.SignatureParameterPart.className()[0]);
        xml.append("(");
        for (int i = 0, size = params.size(); i < size; i++) {
            Ⅱ<String, XML> param = params.get(i);
            xml.append(param.ⅱ);
            xml.append(" ");
            xml.append(I.xml("span").addClass("parameterName").text(param.ⅰ));

            if (i + 1 != size) {
                xml.append(", ");
            }
        }
        xml.append(")");

        return xml;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String id() {
        return id;
    }
}
