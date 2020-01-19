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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.sun.source.util.DocTrees;

public abstract class DocTool<Self extends DocTool> {

    /** Guilty Accessor. */
    public static DocTrees DocUtils;

    /** Guilty Accessor. */
    public static Elements ElementUtils;

    /** Guilty Accessor. */
    public static Types TypeUtils;

    /** The input directories. */
    final List<Path> sources = new ArrayList();

    /** The output directory. */
    Path output = Path.of("docs");

    /**
     * Hide constructor.
     */
    protected DocTool() {
    }

    /**
     * Exact the source directories.
     * 
     * @return
     */
    public final List<Path> sources() {
        return sources;
    }

    /**
     * Exact the output directory.
     * 
     * @return
     */
    public final Path output() {
        if (Files.notExists(output)) {
            try {
                Files.createDirectories(output);
            } catch (IOException e) {
                throw new IllegalStateException("Can't create output directory. [" + output + "]");
            }
        } else if (Files.isDirectory(output) == false) {
            throw new IllegalArgumentException("The output directory is NOT directory. [" + output + "]");
        }
        return output;
    }

    /**
     * Find all package names in the source directory.
     * 
     * @return
     */
    protected final Set<String> findSourcePackages() {
        // collect internal package names
        Set<String> packages = new HashSet();

        for (Path source : sources) {
            try (Stream<Path> paths = Files.walk(source)) {
                paths.filter(path -> Files.isDirectory(path)).forEach(path -> {
                    packages.add(source.relativize(path).toString().replace(File.separatorChar, '.'));
                });
            } catch (Exception e) {
                // though
            }
        }

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