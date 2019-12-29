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
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementScanner9;

import kiss.Variable;

public class ClassInfo extends DocumentInfo {

    /** The fully qualifed class name. */
    public final String fqcn;

    /** Info repository. */
    public final List<FieldInfo> fields = new ArrayList();

    /** Info repository. */
    public final List<ExecutableInfo> constructors = new ArrayList();

    /** Info repository. */
    public final List<MethodInfo> methods = new ArrayList();

    /**
     * @param root
     */
    ClassInfo(Element root) {
        super(root);

        this.fqcn = root.asType().toString();

        root.accept(new Scanner(), this);
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
     * 
     */
    private class Scanner extends ElementScanner9<ClassInfo, ClassInfo> {

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
