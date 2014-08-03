/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source;

import java.util.List;
import java.util.function.BiConsumer;

import kiss.I;
import kiss.XML;

import com.sun.source.tree.Tree;

/**
 * <p>
 * XML manipulation helper.
 * </p>
 * 
 * @version 2014/07/31 21:44:17
 */
class SourceXML {

    /** The current xml. */
    private final XML xml;

    /** The visitor. */
    private final SourceTreeVisitor visitor;

    /**
     * <p>
     * Create new wrapper.
     * </p>
     * 
     * @param xml
     */
    SourceXML(XML xml, SourceTreeVisitor visitor) {
        this.xml = xml;
        this.visitor = visitor;
    }

    /**
     * <p>
     * Create child element.
     * </p>
     * 
     * @param name A element name.
     * @return A child element.
     */
    SourceXML child(String name) {
        return new SourceXML(xml.child(name), visitor);
    }

    /**
     * <p>
     * Create child elements.
     * </p>
     */
    <T> SourceXML children(String name, String prefix, String suffix, List<T> list, BiConsumer<T, SourceXML> item) {
        int size = list.size();

        if (size != 0) {
            SourceXML container = child(name);

            container.text(prefix);

            for (int i = 0; i < size; i++) {
                item.accept(list.get(i), container);

                if (i < size - 1) {
                    container.text(",").space();
                }
            }
            container.text(suffix);
        }
        return this;
    }

    /**
     * <p>
     * Create child elements.
     * </p>
     */
    SourceXML join(List<? extends Tree> list) {
        int size = list.size();

        if (size != 0) {
            for (int i = 0; i < size; i++) {
                list.get(i).accept(visitor, this);

                if (i < size - 1) {
                    text(",").space();
                }
            }
        }
        return this;
    }

    /**
     * <p>
     * Write reserved word.
     * </p>
     * 
     * @param word
     * @return Chainable API.
     */
    SourceXML reserved(String word) {
        xml.append(I.xml("reserved").text(word));

        return this;
    }

    /**
     * <p>
     * Write single space.
     * </p>
     * 
     * @return Chainable API.
     */
    SourceXML space() {
        xml.append(" ");

        return this;
    }

    /**
     * <p>
     * Write text.
     * </p>
     * 
     * @param tree
     * @return Chainable API.
     */
    SourceXML text(Object text) {
        String value = text.toString();

        if (value.length() != 0) {
            xml.append(text.toString());
        }
        return this;
    }

    /**
     * <p>
     * Write semicolon.
     * </p>
     * 
     * @return Chainable API.
     */
    SourceXML semiColon() {
        xml.append(";");

        return this;
    }

    /**
     * <p>
     * Write line break.
     * </p>
     */
    void line() {
    }

    /**
     * <p>
     * Write attribute.
     * </p>
     * 
     * @param name An attribute name.
     * @param value An attribute value.
     * @return Chainable API.
     */
    SourceXML attr(String name, Object value) {
        xml.attr(name, value);
        return this;
    }

    /**
     * <p>
     * Write string literal.
     * </p>
     * 
     * @return Chainable API.
     */
    SourceXML string(String value) {
        xml.append(I.xml("string").text("\"" + value + "\""));

        return this;
    }

    /**
     * <p>
     * Write number literal.
     * </p>
     * 
     * @return Chainable API.
     */
    SourceXML number(String value) {
        xml.append(I.xml("number").text(value));

        return this;
    }

    /**
     * <p>
     * Write variable.
     * </p>
     * 
     * @return Chainable API.
     */
    SourceXML variable(String name) {
        xml.append(I.xml("var").text(name));

        return this;
    }

    /**
     * <p>
     * Write type.
     * </p>
     * 
     * @return Chainable API.
     */
    SourceXML memberAccess(String name) {
        xml.append(I.xml("member").text(name));

        return this;
    }

    /**
     * <p>
     * Write type.
     * </p>
     * 
     * @return Chainable API.
     */
    SourceXML type(String name) {
        xml.append(I.xml("type").text(name));

        return this;
    }

    /**
     * <p>
     * Write member declaration.
     * </p>
     * 
     * @return Chainable API.
     */
    SourceXML declaraMember(String value) {
        xml.append(I.xml("member").text(value));

        return this;
    }

    /**
     * 
     */
    void shrink() {
        String text = xml.text();

        for (int i = 0; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i))) {
                return;
            }
        }
        xml.text("");
    }

    /**
     * @param expression
     */
    SourceXML visit(Tree tree) {
        return tree.accept(visitor, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return xml.toString();
    }
}
