/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.javadoc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kiss.I;
import antibug.ReusableRule;
import antibug.javadoc.info.MethodInfo;
import antibug.javadoc.info.PackageInfo;
import antibug.javadoc.info.TypeInfo;

import com.sun.tools.javadoc.Main;

/**
 * @version 2014/07/26 21:56:00
 */
public class JavadocParser extends ReusableRule {

    /** The empty writer to suppress messages. */
    private static final NoOperationWriter NoOp = new NoOperationWriter();

    /** The root document. */
    private static AntibugDoclet doclet = I.make(AntibugDoclet.class);

    /** The current method document. */
    private Method current;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void beforeClass() throws Exception {
        parse(testcase);
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
     * Retrieve all documents.
     * </p>
     * 
     * @return
     */
    public Documents info() {
        return doclet.documents;
    }

    /**
     * <p>
     * Retrieve the current package info.
     * </p>
     * 
     * @return
     */
    public PackageInfo getPackage() {
        Identifier key = new Identifier(testcase.getPackage().getName());
        PackageInfo info = doclet.documents.packages.get(key);

        if (info == null) {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }
        return info;
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
        for (TypeInfo info : doclet.documents.types) {
            if (info.name.equals(target.getName())) {
                return info;
            }
        }
        // If this exception will be thrown, it is bug of this program. So we must rethrow the
        // wrapped error in here.
        throw new Error();
    }

    /**
     * <p>
     * Retrieve the current method info.
     * </p>
     * 
     * @return
     */
    public MethodInfo getMethod() {
        TypeInfo type = getType();

        for (MethodInfo info : type.methods) {
            if (info.name.equals(current.getName())) {
                System.out.println(info.signature + "   " + current.toString());

            }
        }
        return null;
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

        Main.execute("", NoOp, NoOp, NoOp, AntibugDoclet.class.getName(), AntibugDoclet.class.getClassLoader(), options.toArray(new String[options.size()]));
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
