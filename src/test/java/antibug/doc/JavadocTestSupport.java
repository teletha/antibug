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

import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import kiss.I;
import kiss.Variable;
import kiss.XML;

class JavadocTestSupport {

    private static final JavadocBuilder builder = new JavadocBuilder();

    static {
        AntibugDoclet.Builder.sources("src/test/java").analyzer(builder).build();
    }

    protected final MethodInfo currentMethod() {
        StackFrame frame = StackWalker.getInstance(Set.of(Option.RETAIN_CLASS_REFERENCE), 2)
                .walk(s -> s.skip(1).limit(1).findFirst().get());

        return builder.findByClassName(frame.getClassName())
                .exact()
                .findByMethodSignature(frame.getMethodName(), frame.getMethodType().parameterArray())
                .exact();
    }

    /**
     * Test xml equality.
     * 
     * @param actual
     * @param expected
     * @return
     */
    protected final boolean sameXML(Variable<XML> actual, String expected) {
        assert normalize(actual.exact()).equals(expected);

        return true;
    }

    /**
     * Serialize trimed xml.
     * 
     * @param xml
     * @return
     */
    private String normalize(XML xml) {
        return I.xml(trimWhitespace(xml.to())).toString();
    }

    /**
     * Trim all white space on each {@link Node}.
     * 
     * @param node
     * @return
     */
    private Node trimWhitespace(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                child.setTextContent(child.getTextContent().trim());
            }
            trimWhitespace(child);
        }
        return node;
    }
}
