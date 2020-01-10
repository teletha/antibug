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

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

public class ElementUtil {

    /**
     * Find the top-level {@link TypeElement} (not member class).
     * 
     * @param e
     * @return
     */
    public static TypeElement getTopLevelTypeElement(Element e) {
        Element parent = e.getEnclosingElement();

        while (parent != null && parent.getKind() != ElementKind.PACKAGE) {
            e = parent;
            parent = e.getEnclosingElement();
        }
        return (TypeElement) e;
    }
}
