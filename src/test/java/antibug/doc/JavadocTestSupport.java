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
import java.util.Objects;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
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
        return sameXML(actual.exact(), expected);
    }

    /**
     * Test xml equality.
     * 
     * @param actual
     * @param expected
     * @return
     */
    protected final boolean sameXML(XML actual, String expected) {
        return sameXML(actual, I.xml(expected));
    }

    /**
     * Test xml equality.
     * 
     * @param actual
     * @param expected
     * @return
     */
    protected final boolean sameXML(XML actual, XML expected) {
        return sameXML(actual, actual.to(), expected, expected.to());
    }

    /**
     * Test xml equality.
     * 
     * @param actual
     * @param expected
     * @return
     */
    private boolean sameXML(XML actualXML, Node actual, XML expectedXML, Node expected) {
        // base
        assert actual != null;
        assert expected != null;
        assert Objects.equals(actual.getLocalName(), expected.getLocalName());
        assert actual.getTextContent().trim().equals(expected.getTextContent().trim());

        // attributes
        NamedNodeMap actualAttrs = actual.getAttributes();
        NamedNodeMap expectedAttrs = expected.getAttributes();
        if (actualAttrs != null && expectedAttrs != null) {
            assert actualAttrs.getLength() == expectedAttrs.getLength();

            for (int i = 0; i < actualAttrs.getLength(); i++) {
                Attr attr = (Attr) actualAttrs.item(i);
                Attr pair = (Attr) expectedAttrs.getNamedItem(attr.getName());
                assert pair != null : "Expected element has no attribute [" + attr.getName() + "] in " + expectedXML;
                assert attr.getName().equals(pair.getName());
                assert attr.getValue().equals(pair.getValue());
            }
            for (int i = 0; i < expectedAttrs.getLength(); i++) {
                Attr attr = (Attr) expectedAttrs.item(i);
                Attr pair = (Attr) actualAttrs.getNamedItem(attr.getName());
                assert pair != null : "Actual element has no attribute [" + attr.getName() + "] in " + actualXML;
                assert attr.getName().equals(pair.getName());
                assert attr.getValue().equals(pair.getValue());
            }
        }

        // children
        NodeList actualChildren = actual.getChildNodes();
        NodeList expectedChildren = expected.getChildNodes();
        assert actualChildren.getLength() == expectedChildren.getLength();
        for (int i = 0; i < actualChildren.getLength(); i++) {
            assert sameXML(actualXML, actualChildren.item(i), expectedXML, expectedChildren.item(i));
        }
        return true;
    }
}
