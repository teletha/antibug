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

public class ExecutableInfo extends ParameterizableInfo {

    /** The parameter name manager. */
    private final List<String> names = new ArrayList();

    /** The parameter signature manager. */
    private final List<XML> signatures = new ArrayList();

    /** The parameter comment manager. */
    private final List<XML> comments = new ArrayList();

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
            names.add(param.toString());
            signatures.add(xml);
        }
        this.id = name + "(" + joiner + ")";
    }

    /**
     * Compute the number of parameters.
     */
    public final int numberOfParameters() {
        return names.size();
    }

    /**
     * Build parameter element.
     * 
     * @return
     */
    public final XML createParameter() {
        XML xml = I.xml("span").addClass(styles.SignatureParameterPart.className()[0]);
        xml.append("(");
        for (int i = 0, size = names.size(); i < size; i++) {
            xml.append(createParameter(i));
            xml.append(" ");
            xml.append(I.xml("span").addClass("parameterName").text(names.get(i)));

            if (i + 1 != size) {
                xml.append(", ");
            }
        }
        xml.append(")");

        return xml;
    }

    /**
     * Build parameter element.
     * 
     * @return
     */
    public final XML createParameter(int index) {
        return signatures.get(index).clone();
    }

    /**
     * Build parameter element.
     * 
     * @return
     */
    public final XML createParameterName(int index) {
        return I.xml("<i/>").text(names.get(index));
    }

    /**
     * Build parameter element.
     * 
     * @return
     */
    public final XML createParameterComment(int index) {
        return comments.get(index).clone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String id() {
        return id;
    }
}
