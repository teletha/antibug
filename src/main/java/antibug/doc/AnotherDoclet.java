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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.DocumentationTool;
import javax.tools.DocumentationTool.Location;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import kiss.I;

/**
 * <h>DONT USE THIS CLASS</h>
 * <p>
 * It is a Doclet for internal use, but it is public because it cannot be made private due to the
 * specifications of the documentation tool.
 * </p>
 */
public class AnotherDoclet implements Doclet {

    /** The actual builder. */
    private Builder builder;

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
        DocTool.DocUtils = env.getDocTrees();
        DocTool.ElementUtils = env.getElementUtils();
        DocTool.TypeUtils = env.getTypeUtils();

        DocTool tool = I.make(builder.processor);
        tool.output = Path.of(builder.output);
        tool.sources.addAll(builder.sources.stream().map(Path::of).collect(Collectors.toList()));

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
        return Set.of(new SettingOption());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    /**
     * 
     * 
     */
    private class SettingOption implements Option {

        /**
         * {@inheritDoc}
         */
        @Override
        public int getArgumentCount() {
            return 1;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getDescription() {
            return "Setting by JSON format";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public jdk.javadoc.doclet.Doclet.Option.Kind getKind() {
            return Kind.STANDARD;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<String> getNames() {
            return List.of("-options");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getParameters() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean process(String option, List<String> arguments) {
            builder = I.read(arguments.get(0), new Builder());
            return true;
        }
    }

    /**
     * Helper to call {@link AnotherDoclet} programatically.
     */
    public static class Builder {

        /** The product name. */
        public String productName = "ProductName";

        /** The path to output directory. */
        public String output = "docs/api";

        /** The list of source directories. */
        public List<String> sources = new ArrayList();

        /** The actual document processor. */
        public Class<? extends DocTool> processor = Javadoc.class;

        /**
         * Build documents.
         */
        public final void build() {
            synchronized (Builder.class) {
                DocumentationTool tool = ToolProvider.getSystemDocumentationTool();
                Listener listener = new Listener();

                try (StandardJavaFileManager manager = tool
                        .getStandardFileManager(listener, Locale.getDefault(), Charset.defaultCharset())) {
                    manager.setLocationFromPaths(SOURCE_PATH, sources.stream().map(Path::of).collect(Collectors.toList()));
                    manager.setLocationFromPaths(Location.DOCUMENTATION_OUTPUT, List.of(Path.of(output)));

                    Iterable<? extends JavaFileObject> units = manager.list(SOURCE_PATH, "", Set.of(Kind.SOURCE), true);

                    if (tool.getTask(null, manager, listener, AnotherDoclet.class, List.of("-options", I.write(this)), units).call()) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 
     */
    private static class Listener implements DiagnosticListener<JavaFileObject> {

        /**
         * {@inheritDoc}
         */
        @Override
        public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
            System.out.println(diagnostic);
        }
    }
}