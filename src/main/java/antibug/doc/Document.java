/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug.doc;

import static javax.tools.StandardLocation.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
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
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;

public abstract class Document<Self extends Document> {

    /** The process listener. */
    private DiagnosticListener listener = diagnostic -> {
    };

    /** The input directories. */
    private final List<Path> sources = new ArrayList();

    /** The output directory. */
    private Path output = Path.of("docs");

    /** The utility manager. */
    private DocletEnvironment env;

    /**
     * Set source directory.
     * 
     * @param sourceDirectories A list of paths to source directory.
     * @return Chainable API.
     */
    public final Self sources(String... sourceDirectories) {
        return sources(Arrays.stream(sourceDirectories).map(Path::of).collect(Collectors.toList()));
    }

    /**
     * Set source directory.
     * 
     * @param sourceDirectories A list of paths to source directory.
     * @return Chainable API.
     */
    public final Self sources(Path... sourceDirectories) {
        return sources(List.of(sourceDirectories));
    }

    /**
     * Set source directory.
     * 
     * @param sourceDirectories A list of paths to source directory.
     * @return Chainable API.
     */
    public final Self sources(List<Path> sourceDirectories) {
        if (sourceDirectories != null) {
            for (Path source : sourceDirectories) {
                if (source != null) {
                    this.sources.add(source);
                }
            }
        }
        return (Self) this;
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
     * Set output directory.
     * 
     * @param outputDirectory A path to output directory.
     * @return Chainable API.
     */
    public final Self output(String outputDirectory) {
        return output(Path.of(outputDirectory));
    }

    /**
     * Set output directory.
     * 
     * @param outputDirectory A path to output directory.
     * @return Chainable API.
     */
    public final Self output(Path outputDirectory) {
        if (outputDirectory != null) {
            this.output = outputDirectory;
        }
        return (Self) this;
    }

    /**
     * Start analyze documents.
     * 
     * @param sourceClass
     */
    protected abstract void analyze(Element sourceClass);

    /**
     * Build documents by the specified doclet.
     * 
     * @param builder
     * @throws IOException
     */
    public static void build(Document builder) throws IOException {
        DeadSeaScrolls.builder = Objects.requireNonNull(builder);
        DocumentationTool tool = ToolProvider.getSystemDocumentationTool();

        try (StandardJavaFileManager manager = tool
                .getStandardFileManager(builder.listener, Locale.getDefault(), Charset.defaultCharset())) {
            manager.setLocationFromPaths(SOURCE_PATH, builder.sources);
            manager.setLocationFromPaths(Location.DOCUMENTATION_OUTPUT, List.of(builder.output()));

            Iterable<? extends JavaFileObject> units = manager.list(SOURCE_PATH, "", Set.of(Kind.SOURCE), true);

            // DeadSeaScrolls must be NOT public
            // create public doclet class (because documentation tool accepts only public class)
            Class<? extends DeadSeaScrolls> runtimePublicDocletClass = new ByteBuddy().subclass(DeadSeaScrolls.class)
                    .make()
                    .load(DeadSeaScrolls.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            if (tool.getTask(null, manager, builder.listener, runtimePublicDocletClass, List.of(), units).call()) {
                System.out.println("SUCCESS");
            }
        }
    }

    /**
     * 
     */
    static class DeadSeaScrolls implements Doclet {

        /** The documen builder. */
        private static Document builder;

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
            builder.env = env;

            for (Element element : env.getSpecifiedElements()) {
                builder.analyze(element);
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
