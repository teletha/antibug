/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.doc;

import java.io.IOException;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree.Kind;

import kiss.I;
import kiss.XML;

public class TypeResolver {

    /** PackageName-URL pair. */
    static final Map<String, String> ExternalDocumentLocations = new HashMap();

    /**
     * Returns the URL of the document with the specified type name.
     * 
     * @param moduleName Module name. Null or empty string is ignored.
     * @param packageName Package name. Null or empty string is ignored.
     * @param enclosingName Enclosing type name. Null or empty string is ignored.
     * @param typeName Target type's simple name.
     * @return Resoleved URL.
     */
    public static final String resolveDocumentLocation(String moduleName, String packageName, String enclosingName, String typeName) {
        String url = ExternalDocumentLocations.get(packageName);

        if (url != null) {
            StringBuilder builder = new StringBuilder(url);
            if (moduleName != null && moduleName.length() != 0) builder.append(moduleName).append('/');
            if (packageName != null && packageName.length() != 0) builder.append(packageName.replace('.', '/')).append('/');
            if (enclosingName != null && enclosingName.length() != 0) builder.append(enclosingName).append('.');
            builder.append(typeName).append(".html");

            return builder.toString();
        } else {
            StringBuilder builder = new StringBuilder("/types/");
            if (packageName != null && packageName.length() != 0) builder.append(packageName).append('.');
            if (enclosingName != null && enclosingName.length() != 0) builder.append(enclosingName).append('.');
            builder.append(typeName).append(".html");

            return builder.toString();
        }
    }

    /**
     * Collect package names from the specified external documents.
     * 
     * @param urls
     */
    public static final void collectPackage(String... urls) {
        if (urls != null) {
            for (String url : urls) {
                if (url != null && url.startsWith("http") && url.endsWith("/api/")) {
                    try {
                        for (XML a : I.xml(new URL(url + "overview-tree.html")).find(".horizontal a")) {
                            ExternalDocumentLocations.put(a.text(), url);
                        }
                    } catch (MalformedURLException e) {
                        throw I.quiet(e);
                    }
                }
            }
        }
    }

    /** java.lang types */
    private static final Map<String, String> JavaLangTypes = collectJavaLangTypes();

    /**
     * Register all classes under the java.lang package as classes that can be resolved.
     */
    private static Map<String, String> collectJavaLangTypes() {
        Map<String, String> map = new HashMap();

        try {
            ModuleReference module = ModuleFinder.ofSystem().find("java.base").get();

            try (Stream<String> resources = module.open().list()) {
                resources.filter(name -> name.startsWith("java/lang/") && name.indexOf("/", 10) == -1 && name.endsWith(".class"))
                        .map(name -> name.replace('/', '.').substring(0, name.length() - 6))
                        .map(name -> {
                            try {
                                return Class.forName(name);
                            } catch (ClassNotFoundException e) {
                                return null;
                            }
                        })
                        .filter(clazz -> clazz != null && Modifier.isPublic(clazz.getModifiers()))
                        .forEach(clazz -> map.put(clazz.getSimpleName(), clazz.getCanonicalName()));
            }
            return map;
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }

    /** Imported types. */
    private final Map<String, String> importedTypes = new HashMap();

    /**
     * Collect the imported types.
     * 
     * @param clazz
     */
    public void collectImportedTypes(Element clazz) {
        I.signal(DocTool.DocUtils.getPath(clazz))
                .take(tree -> tree.getKind() == Kind.COMPILATION_UNIT)
                .as(CompilationUnitTree.class)
                .flatIterable(CompilationUnitTree::getImports)
                .to(tree -> {
                    if (tree.isStatic()) {

                    } else {
                        String fqcn = tree.getQualifiedIdentifier().toString();
                        importedTypes.put(fqcn.substring(fqcn.lastIndexOf(".") + 1), fqcn);
                    }
                });

        collectMemberTypes(clazz);
    }

    /**
     * Collect the member types.
     * 
     * @param clazz
     */
    private void collectMemberTypes(Element clazz) {
        I.signal(clazz.getEnclosedElements()).take(e -> e.getKind() == ElementKind.CLASS).as(TypeElement.class).to(e -> {
            String fqcn = e.getQualifiedName().toString();
            importedTypes.put(fqcn.substring(fqcn.lastIndexOf(".") + 1), fqcn);

            collectMemberTypes(e);
        });
    }

    /**
     * Compute FQCN from the specified simple name.
     * 
     * @param className
     */
    public String resolveFQCN(String className) {
        String fqcn = importedTypes.get(className);
        if (fqcn == null) fqcn = JavaLangTypes.get(className);

        return fqcn == null ? className : fqcn;
    }
}
