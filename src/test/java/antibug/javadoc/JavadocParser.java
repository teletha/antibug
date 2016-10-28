/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.javadoc;

import static antibug.javadoc.AntibugDoclet.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import com.sun.tools.javadoc.Main;

import antibug.ReusableRule;
import antibug.javadoc.info.Identifier;
import antibug.javadoc.info.MethodInfo;
import antibug.javadoc.info.PackageInfo;
import antibug.javadoc.info.TypeInfo;
import kiss.I;

public class JavadocParser extends ReusableRule {

    /** The empty writer to suppress messages. */
    private static final NoOperationWriter NoOp = new NoOperationWriter();

    /** The current method document. */
    private Method current;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void beforeClass() throws Exception {
        parse(testcase);

        StringBuilder builder = new StringBuilder();
        I.write(doclet.documents, builder);
        System.out.println(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void before(Method method) throws Exception {
        current = method;
    }

    /**
     * <p>
     * Retrieve the current package info.
     * </p>
     * 
     * @return
     */
    public PackageInfo getPackage() {
        return getPackage(testcase.getPackage());
    }

    /**
     * <p>
     * Retrieve the current package info.
     * </p>
     * 
     * @return
     */
    public PackageInfo getPackage(Package pack) {
        Identifier key = Identifier.of(pack.getName());

        return doclet.documents.getPackageBy(key);
    }

    /**
     * <p>
     * Retrieve the current type info.
     * </p>
     * 
     * @return
     */
    public TypeInfo getType() {
        return getType(testcase);
    }

    /**
     * <p>
     * Retrieve the target type info.
     * </p>
     * 
     * @return
     */
    public TypeInfo getType(Class target) {
        Identifier key;
        Package pack = target.getPackage();

        if (pack == null) {
            // primitive or root package?
            key = Identifier.of("", target.getSimpleName(), "");
        } else {
            // other
            key = Identifier.of(pack.getName(), target.getSimpleName(), "");
        }
        return doclet.documents.getTypeBy(key);
    }

    /**
     * <p>
     * Retrieve the current method info.
     * </p>
     * 
     * @return
     */
    public MethodInfo getMethod() {
        return getMethod(current);
    }

    /**
     * <p>
     * Retrieve the target method info.
     * </p>
     * 
     * @return
     */
    public MethodInfo getMethod(Method target) {
        Class clazz = target.getDeclaringClass();
        StringJoiner joiner = new StringJoiner(", ", "(", ")");

        for (Class param : target.getParameterTypes()) {
            joiner.add(param.getName());
        }

        Identifier key = Identifier.of(clazz.getPackage().getName(), clazz.getSimpleName(), target.getName() + joiner);
        return doclet.documents.getMethodBy(key);
    }

    /**
     * <p>
     * Assertion helper.
     * </p>
     * 
     * @param info
     * @param clazz
     * @return
     */
    public boolean equals(PackageInfo info, Class clazz) {
        if (info == null || clazz == null) {
            return false;
        }
        return info.id.packageName.equals(clazz.getPackage().getName());
    }

    /**
     * <p>
     * Assertion helper.
     * </p>
     * 
     * @param info
     * @param class1
     * @return
     */
    public boolean equals(TypeInfo info, Class clazz) {
        if (info == null || clazz == null) {
            return false;
        }
        return info.id.packageName.equals(clazz.getPackage().getName()) && info.id.typeName.equals(clazz.getSimpleName());
    }

    /**
     * <p>
     * Assertion helper.
     * </p>
     * 
     * @param info
     * @param method
     * @return
     */
    public boolean equals(MethodInfo info, Method method) {
        if (info == null || method == null) {
            return false;
        }

        Class clazz = method.getDeclaringClass();
        StringJoiner joiner = new StringJoiner(", ", method.getName() + "(", ")");

        for (Class param : method.getParameterTypes()) {
            joiner.add(param.getName());
        }
        return info.id.packageName.equals(clazz.getPackage().getName()) && info.id.typeName
                .equals(clazz.getSimpleName()) && info.id.memberName.equals(joiner.toString());
    }

    /**
     * <p>
     * Assertion helper.
     * </p>
     * 
     * @param type
     * @param clazz
     * @return
     */
    public boolean equals(Identifier type, Class clazz) {
        return getType(clazz).id.equals(type);
    }

    /**
     * <p>
     * Retrieve the target method info.
     * </p>
     */
    public Method findMethod(String name, Class... parameters) {
        return findMethod(testcase, name, parameters);
    }

    /**
     * <p>
     * Retrieve the target method info.
     * </p>
     */
    public Method findMethod(Class host, String name, Class... parameters) {
        try {
            return host.getDeclaredMethod(name, parameters);
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * <p>
     * Locate test source file.
     * </p>
     * 
     * @param target
     * @return
     */
    private String locateTestSource(Class target) {
        return I.locate("src/test/java").resolve(target.getName().replace('.', '/').concat(".java")).toString();
    }

    /**
     * <p>
     * Parse javadoc of the specified test class.
     * </p>
     * 
     * @param target
     */
    private void parse(Class target) {
        // collect classes
        Set<Class> classes = new HashSet();
        classes.add(target);

        // build options
        List<String> options = new ArrayList();

        for (Class clazz : classes) {
            options.add(locateTestSource(clazz));
        }

        Main.execute("", NoOp, NoOp, NoOp, AntibugDoclet.class.getName(), AntibugDoclet.class.getClassLoader(), options
                .toArray(new String[options.size()]));
    }

    /**
     * @version 2014/07/26 21:29:15
     */
    private static class NoOperationWriter extends PrintWriter {

        /**
         * @param out
         */
        public NoOperationWriter() {
            super(System.out);
            // super(new NoOperationOutputStream());
        }
    }

    /**
     * @version 2014/07/26 21:30:07
     */
    private static class NoOperationOutputStream extends OutputStream {

        /**
         * {@inheritDoc}
         */
        @Override
        public void write(int b) throws IOException {
            // ignore
        }
    }
}
