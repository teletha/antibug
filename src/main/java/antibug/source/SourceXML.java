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
     * Join the given items.
     * </p>
     */
    SourceXML join(List<? extends Tree> trees) {
        return join(null, trees, null);
    }

    /**
     * <p>
     * Join the given items.
     * </p>
     * 
     * @param prefix
     * @param arguments
     * @param suffix
     */
    SourceXML join(String prefix, List<? extends Tree> trees, String suffix) {
        return join(prefix, trees, ",", suffix);
    }

    /**
     * <p>
     * Join the given items.
     * </p>
     * 
     * @param prefix
     * @param trees
     * @param separator
     * @param suffix
     * @return
     */
    SourceXML join(String prefix, List<? extends Tree> trees, String separator, String suffix) {
        SourceXML current = this;
        int size = trees.size();

        if (size != 0) {
            // prefix
            if (prefix != null) {
                if (prefix.equals(" ")) {
                    current.space();
                } else {
                    current.text(prefix);
                }
            }

            // join
            for (int i = 0; i < size; i++) {
                current = trees.get(i).accept(visitor, current);

                if (i < size - 1) {
                    if (separator != null && separator.length() != 0) {
                        current.text(separator);
                    }
                    current.space();
                }
            }

            // suffix
            if (suffix != null) {
                if (suffix.equals(" ")) {
                    current.space();
                } else {
                    current.text(suffix);
                }
            }
        }
        return current;
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
     * Write member declaration.
     * </p>
     * 
     * @return Chainable API.
     */
    SourceXML memberDeclare(String value) {
        xml.append(I.xml("member").text(value));
    
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
     * Write type parameters.
     * </p>
     * 
     * @param typeArguments
     * @param b
     */
    SourceXML typeParams(List<? extends Tree> list, boolean withSpace) {
        SourceXML current = this;

        if (!list.isEmpty()) {
            current = child("typeParam").join("<", list, ">");

            if (withSpace) {
                current.space();
            }
        }

        return current;
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
     * @param members
     */
    SourceXML visit(List<? extends Tree> trees) {
        SourceXML context = this;

        for (Tree tree : trees) {
            context = tree.accept(visitor, context);
        }

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return xml.toString();
    }
}
