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

import java.util.Deque;
import java.util.LinkedList;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.TypeElement;

import kiss.I;

/**
 * Completed resolved type.
 */
public class ResolvedType {

    public String typeName = "";

    public String enclosingName = "";

    public String packageName = "";

    public String moduleName = "";

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ResolvedType [typeName=" + typeName + ", enclosingName=" + enclosingName + ", packageName=" + packageName + ", moduleName=" + moduleName + "]";
    }

    public static final ResolvedType resolve(String fqcn) {
        TypeElement type = Javadoc.ElementUtils.getTypeElement(fqcn);

        if (type == null) {
            return resolve(type);
        } else {
            return resolve(type);
        }
    }

    /**
     * Resolve from element.
     * 
     * @return Resoleved type.
     */
    public static final ResolvedType resolve(TypeElement e) {
        ResolvedType resolved = new ResolvedType();
        resolved.typeName = e.getSimpleName().toString();

        // enclosing
        Deque<String> enclosings = new LinkedList();
        Element enclosing = e.getEnclosingElement();
        while (enclosing.getKind() != ElementKind.PACKAGE) {
            enclosings.addFirst(((TypeElement) enclosing).getSimpleName().toString());
            enclosing = enclosing.getEnclosingElement();
        }
        resolved.enclosingName = I.join(".", enclosings);

        // pacakage
        resolved.packageName = enclosing.toString();

        // module
        enclosing = enclosing.getEnclosingElement();

        if (enclosing instanceof ModuleElement) {
            ModuleElement module = (ModuleElement) enclosing;
            resolved.moduleName = module.getQualifiedName().toString();
        } else {
            resolved.moduleName = "";
        }

        return resolved;
    }
}