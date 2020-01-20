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

import static javax.tools.StandardLocation.SOURCE_PATH;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.DiagnosticListener;
import javax.tools.DocumentationTool;
import javax.tools.DocumentationTool.Location;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import icy.manipulator.Icy;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import kiss.I;
import psychopath.Directory;
import psychopath.Locator;

@Icy
interface ModernDocletModel {

    /**
     * The list of source directories.
     * 
     * @return
     */
    @Icy.Property
    List<Directory> sources();

    /**
     * The list of source directories.
     * 
     * @return
     */
    @Icy.Overload("sources")
    private List<Directory> sources(String... paths) {
        return I.signal(paths).map(Locator::directory).toList();
    }

    /**
     * The list of source directories.
     * 
     * @return
     */
    @Icy.Overload("sources")
    private List<Directory> sources(Path... paths) {
        return I.signal(paths).map(Locator::directory).toList();
    }

    /**
     * Specify the directory where the product is output.
     * 
     * @return
     */
    @Icy.Property
    Directory output();

    /**
     * Specify the directory where the product is output.
     * 
     * @return
     */
    @Icy.Overload("output")
    private Directory output(String path) {
        return Locator.directory(path);
    }

    /**
     * Specify the directory where the product is output.
     * 
     * @return
     */
    @Icy.Overload("output")
    private Directory output(Path path) {
        return Locator.directory(path);
    }

    /**
     * Specify the class that performs javadoc processing. The default is {@link Javadoc}.
     * 
     * @return
     */
    @Icy.Property
    default Class<? extends ModernJavadocProcessor> processor() {
        return Javadoc.class;
    }

    /**
     * Generate documents.
     */
    default void build() {
        synchronized (Internal.class) {
            Internal.model = this;

            DocumentationTool tool = ToolProvider.getSystemDocumentationTool();
            DiagnosticListener<JavaFileObject> listener = o -> {
                System.out.println(o);
            };

            try (StandardJavaFileManager manager = tool.getStandardFileManager(listener, Locale.getDefault(), Charset.defaultCharset())) {
                manager.setLocationFromPaths(SOURCE_PATH, sources().stream().map(Directory::asJavaPath).collect(Collectors.toList()));
                manager.setLocationFromPaths(Location.DOCUMENTATION_OUTPUT, List.of(output().asJavaPath()));

                Iterable<? extends JavaFileObject> units = manager.list(SOURCE_PATH, "", Set.of(Kind.SOURCE), true);

                if (tool.getTask(null, manager, listener, Internal.class, List.of(), units).call()) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Build {@link Doclet} to generate documents.
     * 
     * @return
     */
    default Class<? extends Doclet> buildDocletClass() {
        Internal.model = this;
        return Internal.class;
    }

    /**
     * <h>DONT USE THIS CLASS</h>
     * <p>
     * It is a Doclet for internal use, but it is public because it cannot be made private due to
     * the specifications of the documentation tool.
     * </p>
     */
    class Internal implements Doclet {

        /** The setting. */
        private static ModernDocletModel model;

        /**
         * {@inheritDoc}
         */
        @Override
        public final void init(Locale locale, Reporter reporter) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final boolean run(DocletEnvironment env) {
            ModernJavadocProcessor.DocUtils = env.getDocTrees();
            ModernJavadocProcessor.ElementUtils = env.getElementUtils();
            ModernJavadocProcessor.TypeUtils = env.getTypeUtils();

            ModernJavadocProcessor tool = I.make(model.processor());
            tool.model = model;

            try {
                tool.initialize();

                for (Element element : env.getSpecifiedElements()) {
                    switch (element.getKind()) {
                    case MODULE:
                        tool.process((ModuleElement) element);
                        break;

                    case PACKAGE:
                        tool.process((PackageElement) element);
                        break;

                    default:
                        tool.process((TypeElement) element);
                        break;
                    }
                }
            } finally {
                tool.complete();
            }
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final String getName() {
            return getClass().getSimpleName();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final Set<? extends Option> getSupportedOptions() {
            return Set.of();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final SourceVersion getSupportedSourceVersion() {
            return SourceVersion.latest();
        }
    }
}
