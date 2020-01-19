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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

import antibug.doc.builder.SiteBuilder;
import antibug.doc.site.MainPage;
import kiss.I;
import stylist.StyleDeclarable;
import stylist.Stylist;

public class DocTool<Self extends DocTool> implements DiagnosticListener<JavaFileObject> {

    /** The input directories. */
    private final List<Path> sources = new ArrayList();

    /** The output directory. */
    private Path output = Path.of("docs");

    /**
     * Hide constructor.
     */
    public DocTool() {
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
     * Build documents.
     */
    public final void build() {
        synchronized (DocTool.class) {
            self = this;
            DocumentationTool tool = ToolProvider.getSystemDocumentationTool();

            try (StandardJavaFileManager manager = tool.getStandardFileManager(this, Locale.getDefault(), Charset.defaultCharset())) {
                manager.setLocationFromPaths(SOURCE_PATH, sources);
                manager.setLocationFromPaths(Location.DOCUMENTATION_OUTPUT, List.of(output()));

                Iterable<? extends JavaFileObject> units = manager.list(SOURCE_PATH, "", Set.of(Kind.SOURCE), true);

                if (tool.getTask(null, manager, this, AnotherDoclet.class, List.of(), units).call()) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
        System.out.println(diagnostic);
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

    /** Dirty Access */
    static DocTool self;

    /** The scanned data. */
    public final Data data = new Data();

    /** The site builder. */
    private SiteBuilder site;

    /** Preference */
    private String productName = "Your Product";

    /** PackageName-URL pair. */
    private final Map<String, String> externals = new HashMap();

    /** The internal pacakage names. */
    private final Set<String> internals = new HashSet();

    {
        // built-in external API
        externalDoc("https://docs.oracle.com/en/java/javase/13/docs/api/");
    }

    /**
     * Get the product name.
     * 
     * @return
     */
    public String productName() {
        return productName;
    }

    /**
     * Configure the produc name.
     * 
     * @param productName
     * @return Chainable API.
     */
    public DocTool productName(String productName) {
        if (productName != null && productName.length() != 0) {
            this.productName = productName;
        }
        return this;
    }

    /**
     * Specifies the URL of the resolvable external document.
     * 
     * @param urls A list of document URL．
     * @return Chainable API.
     */
    public DocTool externalDoc(String... urls) {
        if (urls != null) {
            for (String url : urls) {
                if (url != null && url.startsWith("http") && url.endsWith("/api/")) {
                    try {
                        I.signal(new URL(url + "overview-tree.html"))
                                .map(I::xml)
                                .retryWhen(e -> e.delay(200, TimeUnit.MILLISECONDS).take(20))
                                .flatIterable(xml -> xml.find(".horizontal a"))
                                .to(xml -> {
                                    externals.put(xml.text(), url);
                                });
                    } catch (MalformedURLException e) {
                        throw I.quiet(e);
                    }
                }
            }
        }
        return this;
    }

    /**
     * Initialization phase.
     */
    protected void initialize() {
        // build CSS
        I.load(DocTool.class);
        Stylist.pretty().importNormalizeStyle().styles(I.findAs(StyleDeclarable.class)).formatTo(output().resolve("main.css"));

        site = SiteBuilder.root(output()).guard("index.html", "main.js", "main.css");
        internals.addAll(findSourcePackages());
    }

    /**
     * Process a class or interface program element. Provides access to information about the type
     * and its members. Note that an enum type is a kind of class and an annotation type is a kind
     * of interface.
     * 
     * @param root A class or interface program element root.
     */
    protected void process(TypeElement root) {
        data.add(new ClassInfo(root, new TypeResolver(externals, internals, root)));
    }

    /**
     * Process a package program element. Provides access to information about the package and its
     * members.
     * 
     * @param root A package program element root.
     */
    protected void process(PackageElement root) {
    }

    /**
     * Process a module program element. Provides access to information about the module, its
     * directives, and its members.
     * 
     * @param root A module program element root.
     */
    protected void process(ModuleElement root) {
    }

    /**
     * Completion phase.
     */
    protected void complete() {
        // sort data
        data.modules.sort(Comparator.naturalOrder());
        data.packages.sort(Comparator.naturalOrder());
        data.types.sort(Comparator.naturalOrder());

        // after care
        data.connectSubType();

        // build HTML
        site.buildHTML("javadoc.html", new MainPage(this, null));

        for (ClassInfo info : data.types) {
            site.buildHTML("types/" + info.packageName + "." + info.name + ".html", new MainPage(this, info));
        }
    }
}