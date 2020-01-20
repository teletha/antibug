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

import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public abstract class ModernJavadocProcessor {

    /**
     * Hide constructor.
     */
    protected ModernJavadocProcessor() {
    }

    /**
     * Initialization phase.
     */
    protected abstract void initialize(ModernDocletModel model);

    /**
     * Process a class or interface program element. Provides access to information about the type
     * and its members. Note that an enum type is a kind of class and an annotation type is a kind
     * of interface.
     * 
     * @param root A class or interface program element root.
     */
    protected abstract void process(TypeElement root);

    /**
     * Process a package program element. Provides access to information about the package and its
     * members.
     * 
     * @param root A package program element root.
     */
    protected abstract void process(PackageElement root);

    /**
     * Process a module program element. Provides access to information about the module, its
     * directives, and its members.
     * 
     * @param root A module program element root.
     */
    protected abstract void process(ModuleElement root);

    /**
     * Completion phase.
     */
    protected abstract void complete(ModernDocletModel model);
}