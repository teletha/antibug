/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.javasource;

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
     * Encode xml text.
     * </p>
     * 
     * @param text
     * @return
     */
    private String encode(String text) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            switch (c) {
            case '<':
                builder.append("&lt;");
                break;

            case '>':
                builder.append("&gt;");
                break;

            default:
                builder.append(c);
                break;
            }
        }
        return builder.toString();
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
    SourceXML type(String name) {
        xml.append(I.xml("type").text(name));

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return xml.toString();
    }
}
