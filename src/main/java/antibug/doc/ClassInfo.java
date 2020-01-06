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

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleElementVisitor9;

import kiss.I;
import kiss.Variable;

public class ClassInfo extends ParameterizableInfo implements Comparable<ClassInfo> {

    /** The package name. */
    public String packageName;

    /** The simple class name. */
    public String name;

    /** The kind of type (interface, abstract class, class etc) */
    public String type;

    /** Info repository. */
    protected final List<FieldInfo> fields = new ArrayList();

    /** Info repository. */
    protected final List<ExecutableInfo> constructors = new ArrayList();

    /** Info repository. */
    protected final List<MethodInfo> methods = new ArrayList();

    /**
     * @param root
     */
    ClassInfo(TypeElement root) {
        super(root);
        this.packageName = DocTool.ElementUtils.getPackageOf(root).toString();
        this.name = root.asType().toString().replaceAll("<.+>", "").substring(packageName.length() + 1);
        this.type = detectType(root);

        Scanner scanner = new Scanner();
        for (Element element : root.getEnclosedElements()) {
            element.accept(scanner, this);
        }
    }

    private static String detectType(TypeElement root) {
        switch (root.getKind()) {
        case INTERFACE:
            if (Javadoc.ElementUtils.isFunctionalInterface(root)) {
                return "Functional";
            } else {
                return "Interface";
            }
        case ANNOTATION_TYPE:
            return "Annotation";
        case ENUM:
            return "Enum";
        default: // CLASS
            if (root.getModifiers().contains(Modifier.ABSTRACT)) {
                return "AbstractClass";
            } else if (isThrowable(root.asType())) {
                return "Exception";
            } else {
                return "Class";
            }
        }
    }

    private static boolean isThrowable(TypeMirror type) {
        while (type != null && type.getKind() == TypeKind.DECLARED) {
            DeclaredType dt = (DeclaredType) type;
            TypeElement elem = (TypeElement) dt.asElement();
            Name name = elem.getQualifiedName();
            if ("java.lang.Throwable".contentEquals(name)) {
                return true;
            }
            type = elem.getSuperclass();
        }
        return false;
    }

    /**
     * List up all static fields.
     * 
     * @return A filtered fields.
     */
    public List<FieldInfo> staticFields() {
        return I.signal(fields).take(MemberInfo::isStatic).toList();
    }

    /**
     * List up all non-static fields.
     * 
     * @return A filtered fields.
     */
    public List<FieldInfo> nonStaticFields() {
        return I.signal(fields).skip(MemberInfo::isStatic).toList();
    }

    /**
     * List up all static methods.
     * 
     * @return A filtered methods.
     */
    public List<MethodInfo> staticMethods() {
        return I.signal(methods).take(MemberInfo::isStatic).toList();
    }

    /**
     * List up all non-static methods.
     * 
     * @return A filtered methods.
     */
    public List<MethodInfo> nonStaticMethods() {
        return I.signal(methods).skip(MemberInfo::isStatic).toList();
    }

    /**
     * @param methodName
     * @param paramTypes
     * @return
     */
    public Variable<MethodInfo> findByMethodSignature(String methodName, Class<?>... paramTypes) {
        for (MethodInfo info : methods) {
            if (info.name.equals(methodName)) {
                return Variable.of(info);
            }
        }
        return Variable.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(ClassInfo o) {
        return name.compareTo(o.name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String id() {
        return packageName + "." + name;
    }

    /**
     * 
     */
    private class Scanner extends SimpleElementVisitor9<ClassInfo, ClassInfo> {

        /**
         * {@inheritDoc}
         */
        @Override
        public ClassInfo visitVariable(VariableElement e, ClassInfo p) {
            fields.add(new FieldInfo(e));
            return p;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ClassInfo visitPackage(PackageElement e, ClassInfo p) {
            System.out.println("Package " + e);
            return p;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ClassInfo visitExecutable(ExecutableElement e, ClassInfo p) {
            if (e.getKind() == ElementKind.CONSTRUCTOR) {
                constructors.add(new ExecutableInfo(e));
            } else {
                methods.add(new MethodInfo(e));
            }
            return p;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ClassInfo visitTypeParameter(TypeParameterElement e, ClassInfo p) {
            System.out.println("TypeParameter " + e);
            return p;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ClassInfo visitUnknown(Element e, ClassInfo p) {
            System.out.println("Unknown " + e);
            return p;
        }
    }
}
