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

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.ElementKindVisitor9;
import javax.lang.model.util.ElementScanner9;

/**
 * 
 */
public class AntibugDocument extends Document<AntibugDocument> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void analyze(Element sourceClass) {
        System.out.println(sourceClass);
        sourceClass.accept(new Scanner(), new Object());
    }

    private static class Scanner extends ElementScanner9<Element, Object> {

        /**
         * {@inheritDoc}
         */
        @Override
        public Element visitPackage(PackageElement e, Object p) {
            System.out.println(e);
            return super.visitPackage(e, p);
        }
    }

    /**
     * 
     */
    private static class Visitor extends ElementKindVisitor9<Element, Object> {

        /**
         * {@inheritDoc}
         */
        @Override
        public Element visitPackage(PackageElement e, Object p) {
            System.out.println(e);
            return super.visitPackage(e, p);
        }
    }
}
