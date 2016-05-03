/*
 * Copyright (C) 2015 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.javadoc;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import kiss.I;

/**
 * @version 2014/08/16 9:03:13
 */
public class Location {

    /** The jdk location. */
    public static final String JDKDocLocation = "http://docs.oracle.com/javase/8/docs/api/";

    /** The list of doc root locations. */
    private static final List<String> roots = new ArrayList();

    // initialization
    static {
        installDocRoot(JDKDocLocation);
    }

    /** The type name. */
    private final String[] packages;

    /** The type name. */
    private final String type;

    /** The type name. */
    private final String specifier;

    /** The document location. */
    private final String documentRoot;

    /** The document type. */
    private final boolean externalDoc;

    /**
     * Construct document location info.
     * 
     * @param type
     * @param specifier
     * @param documentRoot
     */
    private Location(Class type, String specifier, String documentRoot) {
        this.packages = parsePackage(type);
        this.type = type.getSimpleName();
        this.specifier = specifier;
        this.documentRoot = documentRoot;

        Path archive = I.locate(type);
        this.externalDoc = archive == null || Files.isRegularFile(archive);
    }

    /**
     * Compute javadoc location.
     * 
     * @return
     */
    public String getJavadocLocation() {
        if (externalDoc) {
            StringBuilder builder = new StringBuilder(documentRoot);

            for (String name : packages) {
                builder.append(name).append('/');
            }
            builder.append(type).append(".html");

            if (specifier.length() != 0) {
                builder.append('#').append(specifier);
            }

            return builder.toString();
        } else {
            StringBuilder builder = new StringBuilder();

            return builder.toString();
        }
    }

    /**
     * <p>
     * Install document root location.
     * </p>
     * 
     * @param uri
     */
    public static void installDocRoot(String uri) {
        if (uri != null && (uri.startsWith("http://") || uri.startsWith("https://"))) {
            if (!uri.endsWith("/")) {
                uri = uri.concat("/");
            }
            roots.add(uri);
        }
    }

    /**
     * Locate by string representation.
     * 
     * @param value
     * @return
     */
    public static Location of(String value) {
        String documentRoot = null;
        String path;

        // detect root
        for (String root : roots) {
            if (value.startsWith(root)) {
                documentRoot = root;
                path = value.substring(root.length() + 1);
                break;
            }
        }

        return new Location(null, null, documentRoot);
    }

    /**
     * Locate by class.
     * 
     * @param clazz
     * @return
     */
    public static Location of(Class clazz) {
        return new Location(clazz, "", JDKDocLocation);
    }

    /**
     * Locate by constructor.
     * 
     * @param clazz
     * @param parameterTypes
     * @return
     */
    public static Location ofExecutable(Class clazz, Class... parameterTypes) {
        try {
            return new Location(clazz, parseExecutable(clazz.getDeclaredConstructor(parameterTypes)), JDKDocLocation);
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * Locate by method.
     * 
     * @param clazz
     * @param methodName
     * @param parameterTypes
     * @return
     */
    public static Location ofExecutable(Class clazz, String methodName, Class... parameterTypes) {
        try {
            return new Location(clazz, parseExecutable(clazz.getDeclaredMethod(methodName, parameterTypes)), JDKDocLocation);
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Location ofField(Class clazz, String fieldName) {
        try {
            return new Location(clazz, clazz.getDeclaredField(fieldName).getName(), JDKDocLocation);
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * Parse package.
     * 
     * @param type A target class.
     * @return
     */
    private static String[] parsePackage(Class type) {
        return type.getPackage().getName().split("\\.");
    }

    /**
     * Parse method.
     * 
     * @param methodName
     * @param parameterTypes
     * @return
     */
    private static String parseExecutable(Executable exe) {
        boolean varArgs = exe.isVarArgs();
        StringBuilder builder = new StringBuilder();

        // compute simple name.
        String name = exe.getName();
        int index = name.lastIndexOf('.');

        if (index != -1) {
            name = name.substring(index + 1);
        }
        builder.append(name).append('-');

        // compute parameters
        Parameter[] parameters = exe.getParameters();

        for (int i = 0, length = parameters.length; i < length; i++) {
            String param = parameters[i].getParameterizedType().getTypeName();

            // remove wildcard parameter
            param = param.replaceAll("<.*>", "");

            // resolve array parameter
            param = param.replaceAll("\\[\\]", varArgs && i == length - 1 ? "..." : ":A");

            builder.append(param);

            // write separator
            if (i != length - 1) builder.append('-');
        }

        // API definition
        return builder.append('-').toString();
    }
}
