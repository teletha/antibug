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

import java.util.HashSet;
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
    public MemberInfo(Element e, TypeResolver resolver) {
        super(e, resolver);

        String name = e.getSimpleName().toString();
        if (name.equals("<init>")) {
            name = e.getEnclosingElement().getSimpleName().toString();
        }

        this.name = name;
        this.modifiers = e.getModifiers();
    }

    /**
     * Check whether this member has static modifier or not.
     * 
     * @return Result.
     */
    public final boolean isStatic() {
        return modifiers.contains(Modifier.STATIC);
    }

    /**
     * Check whether this member is deprecated or not.
     * 
     * @return Result.
     */
    public final boolean isDeprecated() {
        return DocTool.ElementUtils.isDeprecated(e);
    }

    /**
     * Check whether this member is overridden or not.
     * 
     * @return Result.
     */
    public final boolean isOverridden() {
        return e.getAnnotation(Override.class) != null;
    }

    /**
     * Build full signature with modifier infomation.
     * 
     * @return
     */
    public final XML createSignature() {
        return createNameWithModifier();
    }

    /**
     * Build name element with modifier infomation.
     * 
     * @param info
     * @return
     */
    public final XML createNameWithModifier() {
        Set<Modifier> visibility = new HashSet();
        Set<Modifier> nonvisibility = new HashSet();
        boolean isPackagePrivate = true;

        for (Modifier modifier : modifiers) {
            switch (modifier) {
            case ABSTRACT:
            case FINAL:
            case VOLATILE:
            case SYNCHRONIZED:
            case TRANSIENT:
                nonvisibility.add(modifier);
                break;

            case PUBLIC:
            case PROTECTED:
            case PRIVATE:
                isPackagePrivate = false;
                // fall-throgh

            default:
                visibility.add(modifier);
                break;
            }
        }

        XML xml = I.xml("i").append(I.xml("a").attr("href", "#" + id()).text(name));
        if (isPackagePrivate) xml.addClass("PACKAGEPRIVATE");
        if (isOverridden()) xml.addClass("OVERRIDE");
        for (Modifier modifier : visibility) {
            xml.addClass(modifier.name());
        }

        if (!nonvisibility.isEmpty()) {
            xml = I.xml("i").append(xml);
            for (Modifier modifier : nonvisibility) {
                xml.addClass(modifier.name());
            }
        }
        return xml;
    }

    /**
     * Compute the member identifier.
     * 
     * @return
     */
    protected abstract String id();
}
