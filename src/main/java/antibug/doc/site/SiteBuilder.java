/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.doc.site;

import java.nio.file.Path;
import java.util.Objects;

import kiss.I;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;
import stylist.StyleDSL;
import stylist.Stylist;

public class SiteBuilder {

    /** GUILTY ACCESSOR */
    static SiteBuilder current;

    /**
     * Configure root directory.
     * 
     * @param pathToRootDirectory
     * @return
     */
    public static final SiteBuilder root(String pathToRootDirectory) {
        return root(Locator.directory(pathToRootDirectory));
    }

    /**
     * Configure root directory.
     * 
     * @param pathToRootDirectory
     * @return
     */
    public static final SiteBuilder root(Path pathToRootDirectory) {
        return root(Locator.directory(pathToRootDirectory));
    }

    /**
     * Configure root directory.
     * 
     * @param rootDirectory
     * @return
     */
    public static final SiteBuilder root(Directory rootDirectory) {
        return new SiteBuilder(rootDirectory);
    }

    /** The root directory. */
    private final Directory root;

    /**
     * 
     * @param rootDirectory
     */
    private SiteBuilder(Directory rootDirectory) {
        this.root = Objects.requireNonNull(rootDirectory);
        current = this;

        // delete all existing files
        root.create().delete("!**@.*");
    }

    /**
     * Build HTML file.
     * 
     * @param path
     * @param html
     */
    public final void buildHTML(String path, HTML html) {
        root.file(path).write(output -> {
            output.append("<!DOCTYPE html>").append(Formattable.EOL);

            for (HTML.ElementNode node : html.root) {
                node.format(output, 0, false);
            }
        });
    }

    /**
     * Build CSS file and return the path of the generated file.
     * 
     * @param styles A style definition class to write.
     * @return A path to the generated file.
     */
    public final String buildCSS(String path, Class<? extends StyleDSL> styles) {
        String formatted = Stylist.pretty().importNormalizeStyle().format(styles);

        File file = root.file(path);
        file.write(output -> output.append(formatted));
        return root.relativize(file).path();
    }

    /**
     * Build JSON file with padding.
     * 
     * @param path
     * @param html
     */
    public final String buildJSONP(String path, Object object) {
        File file = root.file(path);
        file.write(output -> {
            output.append("const " + file.base() + " = ");
            I.write(object, output);
        });
        return root.relativize(file).path();
    }
}
