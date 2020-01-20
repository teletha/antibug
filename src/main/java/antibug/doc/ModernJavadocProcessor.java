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

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.sun.source.util.DocTrees;

import kiss.I;
import kiss.Managed;
import kiss.Singleton;
import psychopath.Directory;

@Managed(Singleton.class)
public abstract class ModernJavadocProcessor {

    /** Guilty Accessor. */
    public static DocTrees DocUtils;

    /** Guilty Accessor. */
    public static Elements ElementUtils;

    /** Guilty Accessor. */
    public static Types TypeUtils;

    /** The doclet option. */
    public ModernDocletModel model;

    /**
     * Hide constructor.
     */
    protected ModernJavadocProcessor() {
    }

    /**
     * Find all package names in the source directory.
     * 
     * @return
     */
    protected final Set<String> findSourcePackages() {
        // collect internal package names
        Set<String> packages = new HashSet();

        I.signal(model.sources()).flatMap(Directory::walkDirectoryWithBase).to(sub -> {
            packages.add(sub.ⅰ.relativize(sub.ⅱ).toString().replace(File.separatorChar, '.'));
        });

        return packages;
    }

    /**
     * Initialization phase.
     */
    protected abstract void initialize();

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
    protected abstract void complete();
}