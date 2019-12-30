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

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

import antibug.doc.site.HTML;
import antibug.doc.site.SiteBuilder;
import kiss.I;

public class AntibugJavadoc extends AntibugDocumentationTool<AntibugJavadoc> {

    /** The scanned data. */
    private final Data data = new Data();

    /** The site builder. */
    private SiteBuilder site;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        site = SiteBuilder.root(output());

        try {
            Path output = output().toAbsolutePath();

            if (Files.notExists(output)) {
                Files.createDirectories(output);
            }

            if (!Files.isDirectory(output)) {
                throw new IllegalStateException("The specified output location is not a directory. Specify a directory. [" + output + "]");
            }

            // delete all existing files
            Files.walkFileTree(output, new DeleteAllFilesAndDirectories());
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }

    /**
     * Helper to locate file.
     * 
     * @param name
     * @return
     */
    private Path locate(String name) {
        return output().resolve(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void process(TypeElement root) {
        ClassInfo info = new ClassInfo(root);
        data.add(info);

        site.buildHTML("types/" + info.fqcn + ".html", new HTML() {
            {
                $("html", () -> {
                    $("body", () -> {

                    });
                });
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void process(PackageElement root) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void process(ModuleElement root) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void complete() {
        // sort data
        data.packages.sort(Comparator.naturalOrder());

        // build HTML

        site.buildHTML("index.html", new HTML() {
            {
                $("html", () -> {
                    $("head", () -> {
                        linkStylesheet("main.css", AntibugJavadocStyles.class);
                        linkStylesheet("javadoc.css", BuiltinStyles.class);
                    });
                    $("body", () -> {
                        $("nav", () -> {
                            $("div", id("packageList"), () -> {
                                $("span", attr("v-for", "package in packages"), () -> {
                                    text("{{package}}");
                                });
                            });
                            $("div", id("typeList"), () -> {
                                $("span", attr("v-for", "type in types"), () -> {
                                    text("{{type}}");
                                });
                            });
                        });
                        $("main", attr("id", "app"), () -> {
                            $("h1", () -> {
                                $("em", () -> {
                                    text("{{message}}");
                                });
                            });
                        });
                        script("https://cdn.jsdelivr.net/npm/vue/dist/vue.js");
                        script("data.js", data);
                        script("main@js");
                    });
                });
            }
        });
    }

    /**
     * Scanned data repository.
     */
    private final class Data {

        /** Type repository. */
        public List<String> packages = new ArrayList();

        /** Type repository. */
        public List<String> types = new ArrayList();

        /**
         * Avoid duplication.
         */
        private void add(ClassInfo info) {
            types.add(info.fqcn);

            if (packages.indexOf(info.packageName) == -1) {
                packages.add(info.packageName);
            }
        }
    }

    /**
     * 
     */
    private final class DeleteAllFilesAndDirectories implements FileVisitor<Path> {
        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (!file.getFileName().toString().contains("@")) {
                Files.delete(file);
            }
            return FileVisitResult.CONTINUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            try {
                Files.delete(dir);
            } catch (FileSystemException e) {
                // ignore
            }
            return FileVisitResult.CONTINUE;
        }
    }
}
