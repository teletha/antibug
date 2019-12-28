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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
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

/**
 * 
 */
public class AntibugDoclet implements Doclet {

    /** Singleton builder. */
    public final static Builder Builder = new Builder();

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
        for (Element element : env.getSpecifiedElements()) {
            Builder.analyzer.accept(element);
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

    /**
     * 
     * 
     */
    public static class Builder {

        /** The process listener. */
        private DiagnosticListener listener = diagnostic -> {
        };

        /** The input directories. */
        private final List<Path> sources = new ArrayList();

        /** The output directory. */
        private Path output = Path.of("docs");

        /** The document analyzer. */
        private Consumer<Element> analyzer = new JavadocBuilder();

        /**
         * Hide constructor.
         */
        private Builder() {
        }

        /**
         * Set source directory.
         * 
         * @param sourceDirectories A list of paths to source directory.
         * @return Chainable API.
         */
        public final Builder sources(String... sourceDirectories) {
            return sources(Arrays.stream(sourceDirectories).map(Path::of).collect(Collectors.toList()));
        }

        /**
         * Set source directory.
         * 
         * @param sourceDirectories A list of paths to source directory.
         * @return Chainable API.
         */
        public final Builder sources(Path... sourceDirectories) {
            return sources(List.of(sourceDirectories));
        }

        /**
         * Set source directory.
         * 
         * @param sourceDirectories A list of paths to source directory.
         * @return Chainable API.
         */
        public final Builder sources(List<Path> sourceDirectories) {
            if (sourceDirectories != null) {
                for (Path source : sourceDirectories) {
                    if (source != null) {
                        this.sources.add(source);
                    }
                }
            }
            return this;
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
        public final Builder output(String outputDirectory) {
            return output(Path.of(outputDirectory));
        }

        /**
         * Set output directory.
         * 
         * @param outputDirectory A path to output directory.
         * @return Chainable API.
         */
        public final Builder output(Path outputDirectory) {
            if (outputDirectory != null) {
                this.output = outputDirectory;
            }
            return this;
        }

        /**
         * Set document analyzer.
         * 
         * @param analyzer
         * @return
         */
        public final Builder analyzer(Consumer<Element> analyzer) {
            if (analyzer != null) {
                this.analyzer = analyzer;
            }
            return this;
        }

        /**
         * Build documents by the specified doclet.
         * 
         * @throws IOException
         */
        public void build() {
            DocumentationTool tool = ToolProvider.getSystemDocumentationTool();

            try (StandardJavaFileManager manager = tool.getStandardFileManager(listener, Locale.getDefault(), Charset.defaultCharset())) {
                manager.setLocationFromPaths(SOURCE_PATH, sources);
                manager.setLocationFromPaths(Location.DOCUMENTATION_OUTPUT, List.of(output()));

                Iterable<? extends JavaFileObject> units = manager.list(SOURCE_PATH, "", Set.of(Kind.SOURCE), true);

                if (tool.getTask(null, manager, listener, AntibugDoclet.class, List.of(), units).call()) {
                    System.out.println("SUCCESS");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}