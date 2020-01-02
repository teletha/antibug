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
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.SimpleElementVisitor9;

import kiss.Variable;

public class ClassInfo extends ParameterizableInfo implements Comparable<ClassInfo> {

    /** The fully qualifed class name with type parameters. */
    public final String typeName;

    /** The fully qualifed class name. */
    public String packageName;

    /** The simple class name. */
    public String name;

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

        this.typeName = root.asType().toString();
        this.packageName = AntibugDocumentationTool.ElementUtils.getPackageOf(root).toString();
        this.name = typeName.replaceAll("<.+>", "").substring(packageName.length() + 1);

        Scanner scanner = new Scanner();
        for (Element element : root.getEnclosedElements()) {
            element.accept(scanner, this);
        }
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
                constructors.add(new ExecutableInfo(name, e));
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
