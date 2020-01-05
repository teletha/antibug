/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.doc;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

import kiss.I;
import kiss.XML;

public abstract class MemberInfo extends DocumentInfo {

    /** The name of this member. */
    public final String name;

    /** The modifier of this member. */
    public final Set<Modifier> modifiers;

    /**
     * @param e
     */
    public MemberInfo(Element e) {
        super(e);

        String name = e.getSimpleName().toString();
        if (name.equals("<init>")) {
            name = e.getEnclosingElement().getSimpleName().toString();
        }

        this.name = name;
        this.modifiers = e.getModifiers();
    }

    /**
     * Build name element with modifier infomation.
     * 
     * @param info
     * @return
     */
    public final XML createNameWithModifier() {
        XML xml = I.xml("i").text(name);

        for (Modifier modifier : modifiers) {
            xml.addClass(modifier.name());
        }

        return xml;
    }
}
