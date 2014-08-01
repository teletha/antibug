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
import java.util.function.Consumer;

import kiss.I;
import kiss.XML;

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

    /**
     * <p>
     * Create new wrapper.
     * </p>
     * 
     * @param xml
     */
    SourceXML(XML xml) {
        this.xml = xml;
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
        return new SourceXML(xml.child(name));
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
    <T> SourceXML join(List<T> list, Consumer<T> item) {
        return join(null, null, list, item);
    }

    /**
     * <p>
     * Create child elements.
     * </p>
     */
    <T> SourceXML join(String prefix, String suffix, List<T> list, Consumer<T> item) {
        int size = list.size();

        if (size != 0) {
            if (prefix != null) text(prefix);

            for (int i = 0; i < size; i++) {
                item.accept(list.get(i));

                if (i < size - 1) {
                    text(",").space();
                }
            }
            if (suffix != null) text(suffix);
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
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return xml.toString();
    }
}
