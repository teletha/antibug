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
import java.util.function.Consumer;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import kiss.Variable;

public class AntibugJavadoc implements Consumer<Element> {

    /** Info repository. */
    public final List<ClassInfo> classes = new ArrayList();

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(Element root) {
        classes.add(new ClassInfo((TypeElement) root));
    }

    /**
     * @param className
     */
    public Variable<ClassInfo> findByClassName(String className) {
        for (ClassInfo info : classes) {
            if (info.fqcn.equals(className)) {
                return Variable.of(info);
            }
        }
        return Variable.empty();
    }
}
