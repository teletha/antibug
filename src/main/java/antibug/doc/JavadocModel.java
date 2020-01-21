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

import static javax.tools.StandardLocation.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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

import antibug.doc.analyze.ClassInfo;
import antibug.doc.analyze.Data;
import antibug.doc.builder.SiteBuilder;
import antibug.doc.site.MainPage;
import icy.manipulator.Icy;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import kiss.I;
import kiss.Variable;
import psychopath.Directory;
import psychopath.Locator;
import stylist.StyleDeclarable;
import stylist.Stylist;

@Icy
public abstract class JavadocModel {

    /** The scanned data. */
    public final Data data = new Data();

    /** PackageName-URL pair. */
    private final Map<String, String> externals = new HashMap();

    /** The internal pacakage names. */
    private final Set<String> internals = new HashSet();

    /**
     * The list of source directories.
     * 
     * @return
     */
    @Icy.Property
    public abstract List<Directory> sources();

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
    @Icy.Property(nullable = true)
    public abstract Directory output();

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
     * The product name.
     * 
     * @return
     */
    @Icy.Property
    public abstract String product();

    /**
     * The project name.
     * 
     * @return
     */
    @Icy.Property
    public abstract String project();

    /**
     * The product version.
     * 
     * @return
     */
    @Icy.Property
    public abstract String version();

    /**
     * Use JDK as the resolvable external document.
     * 
     * @return
     */
    public final Javadoc useExternalJDKDoc() {
        return useExternalDoc("https://docs.oracle.com/en/java/javase/13/docs/api/");
    }

    /**
     * Specifies the URL of the resolvable external document.
     * 
     * @param urls A list of document URL．
     * @return Chainable API.
     */
    public final Javadoc useExternalDoc(String... urls) {
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
        return (Javadoc) this;
    }

    /**
     * Generate documents.
     */
    public final Javadoc build() {
        synchronized (JavadocModel.class) {
            Internal.model = this;

            DocumentationTool tool = ToolProvider.getSystemDocumentationTool();
            DiagnosticListener<JavaFileObject> listener = o -> {
                System.out.println(o);
            };

            try (StandardJavaFileManager manager = tool.getStandardFileManager(listener, Locale.getDefault(), Charset.defaultCharset())) {
                manager.setLocationFromPaths(SOURCE_PATH, sources().stream().map(Directory::asJavaPath).collect(Collectors.toList()));
                manager.setLocationFromPaths(Location.DOCUMENTATION_OUTPUT, List
                        .of(output() == null ? Path.of("") : output().asJavaPath()));

                Iterable<? extends JavaFileObject> units = manager.list(SOURCE_PATH, "", Set.of(Kind.SOURCE), true);
                if (tool.getTask(null, manager, listener, Internal.class, List.of(), units).call()) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (Javadoc) this;
    }

    /**
     * Build {@link Doclet} to generate documents.
     * 
     * @return
     */
    public final Class<? extends Doclet> buildDocletClass() {
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
    public static class Internal implements Doclet {

        /** The setting model. */
        private static JavadocModel model;

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
            Util.DocUtils = env.getDocTrees();
            Util.ElementUtils = env.getElementUtils();
            Util.TypeUtils = env.getTypeUtils();

            try {
                model.initialize();

                for (Element element : env.getSpecifiedElements()) {
                    switch (element.getKind()) {
                    case MODULE:
                        model.process((ModuleElement) element);
                        break;

                    case PACKAGE:
                        model.process((PackageElement) element);
                        break;

                    default:
                        model.process((TypeElement) element);
                        break;
                    }
                }
            } finally {
                model.complete();
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

    /**
     * Find class by its name.
     * 
     * @param className
     */
    final Variable<ClassInfo> findByClassName(String className) {
        for (ClassInfo info : data.types) {
            if ((info.packageName + "." + info.name).equals(className)) {
                return Variable.of(info);
            }
        }
        return Variable.empty();
    }

    /**
     * Find all package names in the source directory.
     * 
     * @return
     */
    final Set<String> findSourcePackages() {
        // collect internal package names
        Set<String> packages = new HashSet();

        I.signal(sources()).flatMap(Directory::walkDirectoryWithBase).to(sub -> {
            packages.add(sub.ⅰ.relativize(sub.ⅱ).toString().replace(File.separatorChar, '.'));
        });
        return packages;
    }

    /**
     * Initialization phase.
     */
    private void initialize() {
        internals.addAll(findSourcePackages());
    }

    /**
     * Process a class or interface program element. Provides access to information about the type
     * and its members. Note that an enum type is a kind of class and an annotation type is a kind
     * of interface.
     * 
     * @param root A class or interface program element root.
     */
    private void process(TypeElement root) {
        data.add(new ClassInfo(root, new TypeResolver(externals, internals, root)));
    }

    /**
     * Process a package program element. Provides access to information about the package and its
     * members.
     * 
     * @param root A package program element root.
     */
    private void process(PackageElement root) {
    }

    /**
     * Process a module program element. Provides access to information about the module, its
     * directives, and its members.
     * 
     * @param root A module program element root.
     */
    private void process(ModuleElement root) {
    }

    /**
     * Completion phase.
     */
    private void complete() {
        // sort data
        data.modules.sort(Comparator.naturalOrder());
        data.packages.sort(Comparator.naturalOrder());
        data.types.sort(Comparator.naturalOrder());

        // after care
        data.connectSubType();

        if (output() != null) {
            SiteBuilder site = SiteBuilder.root(output()).guard("index.html", "main.js", "main.css");

            // build CSS
            I.load(JavadocModel.class);
            Stylist.pretty()
                    .importNormalizeStyle()
                    .styles(I.findAs(StyleDeclarable.class))
                    .formatTo(output().file("main.css").asJavaPath());

            // build HTML
            site.buildHTML("javadoc.html", new MainPage(this, null));

            for (ClassInfo info : data.types) {
                site.buildHTML("types/" + info.packageName + "." + info.name + ".html", new MainPage(this, info));
            }
        }
    }
}
