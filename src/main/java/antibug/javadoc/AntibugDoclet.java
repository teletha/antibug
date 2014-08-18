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

import java.util.HashMap;
import java.util.Map;

import kiss.I;
import kiss.Manageable;
import kiss.Singleton;
import kiss.XML;
import antibug.javadoc.info.AnnotationInfo;
import antibug.javadoc.info.Documents;
import antibug.javadoc.info.Identifier;
import antibug.javadoc.info.MarkupValue;
import antibug.javadoc.info.MethodInfo;
import antibug.javadoc.info.PackageInfo;
import antibug.javadoc.info.ParamInfo;
import antibug.javadoc.info.TypeInfo;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Type;

/**
 * @version 2014/07/26 22:34:28
 */
@Manageable(lifestyle = Singleton.class)
public class AntibugDoclet extends Doclet {

    /** The singleton-like. */
    static AntibugDoclet doclet;

    static {
        I.load(Identifier.class, true);
    }

    /** All documents. */
    protected final Documents documents = new Documents();

    /** Avoid linear search. */
    private Map<ClassDoc, TypeInfo> types = new HashMap();

    /** Avoid linear search. */
    private Map<PackageDoc, PackageInfo> packages = new HashMap();

    /**
     * <p>
     * Start entry point.
     * </p>
     */
    public static boolean start(RootDoc root) {
        doclet = new AntibugDoclet();
        doclet.build(root);

        return true;
    }

    /**
     * <p>
     * Build custom documents.
     * </p>
     * 
     * @param root
     */
    protected void build(RootDoc root) {
        for (ClassDoc classDoc : root.classes()) {
            TypeInfo typeInfo = findTypeInfoBy(classDoc);
        }
    }

    /**
     * <p>
     * Find package info by {@link ClassDoc}.
     * </p>
     * 
     * @param doc
     * @return
     */
    private PackageInfo findPackageBy(PackageDoc doc) {
        // check cache
        PackageInfo info = packages.get(doc);

        if (info == null) {
            // build package info
            info = new PackageInfo();
            info.id = Identifier.of(doc.name(), "", "");

            // associate with documents
            documents.packages.add(info);

            // cache
            packages.put(doc, info);
        }

        // API definition
        return info;
    }

    /**
     * <p>
     * Find type info by {@link ClassDoc}.
     * </p>
     * 
     * @param doc
     * @return
     */
    private TypeInfo findTypeInfoBy(ClassDoc doc) {
        // check cache
        TypeInfo info = types.get(doc);

        if (info == null) {
            // build type info
            info = new TypeInfo();
            info.id = Identifier.of(doc.containingPackage().name(), doc.simpleTypeName(), "");

            if (doc.containingClass() != null) {
                TypeInfo parent = findTypeInfoBy(doc.containingClass());
                parent.inners.add(info.id);
            }

            for (MethodDoc methodDoc : doc.methods()) {
                info.methods.add(findMethodInfo(methodDoc, info.id));
            }

            // associate with package
            PackageInfo packageInfo = findPackageBy(doc.containingPackage());
            packageInfo.types.add(info);

            // cache
            types.put(doc, info);
        }

        // API definition
        return info;
    }

    /**
     * <p>
     * Find method info by {@link MethodDoc}.
     * </p>
     * 
     * @param methodDoc
     * @return
     */
    private MethodInfo findMethodInfo(MethodDoc doc, Identifier id) {
        MethodInfo info = new MethodInfo();
        info.id = Identifier.of(id.packageName, id.typeName, doc.name() + doc.signature());

        for (Parameter parameter : doc.parameters()) {
            ParamInfo param = new ParamInfo();
            param.name = parameter.name();
            param.type = id(parameter.type());

            for (AnnotationDesc desc : parameter.annotations()) {
                AnnotationInfo anno = new AnnotationInfo();
                anno.type = id(desc.annotationType());

                for (ElementValuePair pair : desc.elementValues()) {
                    anno.keys.add(id(pair.element()));
                    anno.values.add(analyze(pair.value()));
                }
                param.annotation.add(anno);
            }

            info.params.add(param);
        }

        // API definition
        return info;
    }

    /**
     * @param value
     * @return
     */
    private MarkupValue analyze(AnnotationValue value) {
        XML xml = I.xml("root");
        // Returns the value. The type of the returned object is one of the following:
        //
        // a wrapper class for a primitive type
        // String
        // Type (representing a class literal)
        // FieldDoc (representing an enum constant)
        // AnnotationDesc
        // AnnotationValue[]
        Object object = value.value();

        return null;
    }

    /**
     * @param value
     * @return
     */
    private void analyze(XML xml, Object object) {
        // Returns the value. The type of the returned object is one of the following:
        //
        // a wrapper class for a primitive type
        // String
        // Type (representing a class literal)
        // FieldDoc (representing an enum constant)
        // AnnotationDesc
        // AnnotationValue[]
        if (object instanceof String) {
        }
    }

    /**
     * @param element
     * @return
     */
    private Identifier id(MethodDoc method) {
        return findMethodInfo(method, findTypeInfoBy(method.containingClass()).id).id;
    }

    /**
     * <p>
     * Find {@link Identifier} info by {@link Type}.
     * </p>
     * 
     * @param methodDoc
     * @return
     */
    private Identifier id(Type type) {
        if (type.isPrimitive()) {
            return Identifier.of("", type.simpleTypeName(), "");
        } else {
            return Identifier.of(type.asClassDoc().containingPackage().name(), type.simpleTypeName(), "");
        }
    }
}
