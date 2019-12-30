/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug.doc.site;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import kiss.I;
import kiss.Tree;
import stylist.StyleDSL;

/**
 * Domain Specific Language for HTML.
 */
public abstract class HTML extends Tree<String, HTML.ElementNode> {

    /**
     * 
     */
    public HTML() {
        super(HTML.ElementNode::new, null);
    }

    /**
     * <p>
     * accept node attribute with name.
     * </p>
     * 
     * @param name An attribute name.
     */
    protected final Consumer attr(Object name) {
        return attr(name, null);
    }

    /**
     * <p>
     * accept node attribute with name.
     * </p>
     * 
     * @param name An attribute name.
     */
    protected final Consumer<HTML.ElementNode> attr(Object name, String value) {
        return parent -> {
            if (name != null) {
                String n = String.valueOf(name);

                if (!n.isEmpty()) {
                    parent.attrs.add(new AttributeNode(n, value));
                }
            }
        };
    }

    /**
     * <p>
     * accept text node.
     * </p>
     * 
     * @param text A text.
     */
    protected final void text(Object text) {
        $(new TextNode(String.valueOf(text)));
    }

    /**
     * Shorthand method to declare class attribute.
     * 
     * @param className
     * @return
     */
    protected final Consumer<HTML.ElementNode> id(String id) {
        return n -> n.attrs.add(new AttributeNode("id", id));
    }

    /**
     * Shorthand method to declare class attribute.
     * 
     * @param className
     * @return
     */
    protected final Consumer<HTML.ElementNode> clazz(String className) {
        return n -> n.attrs.add(new AttributeNode("class", className));
    }

    /**
     * Build CSS file and return the path of the generated file.
     * 
     * @param styles A style definition class to write.
     * @return A path to the generated file.
     */
    protected final void linkStylesheet(String path, Class<? extends StyleDSL> styles) {
        $("link", attr("rel", "stylesheet"), attr("href", SiteBuilder.current.buildCSS(path, styles)));
    }

    /**
     * Build script tag.
     * 
     * @param path A style definition class to write.
     */
    protected final void script(String path) {
        $("script", attr("src", path));
    }

    /**
     * Build JSONP file and return the path of the generated file.
     * 
     * @param styles A style definition class to write.
     * @return A path to the generated file.
     */
    protected final void script(String path, Object model) {
        $("script", attr("src", SiteBuilder.current.buildJSONP(path, model)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (HTML.ElementNode node : root) {
            try {
                node.format(builder, 0, false);
            } catch (IOException e) {
                throw I.quiet(e);
            }
        }
        return builder.toString();
    }

    /**
     * 
     */
    static class ElementNode implements Consumer<HTML.ElementNode>, Formattable {

        private static final Set<String> characterType = Set
                .of("title", "dd", "dt", "figcaption", "figure", "li", "p", "a", "abbr", "b", "bdi", "bdo", "cite", "code", "data", "dfn", "em", "i", "kbd", "mark", "q", "rb", "rp", "rt", "rtc", "s", "samp", "strong", "sub", "sup", "time", "u", "var", "del", "ins");

        protected String name;

        private List<HTML.AttributeNode> attrs = new ArrayList();

        private List<Formattable> children = new ArrayList();

        /**
         * @param name
         */
        private ElementNode(String name, int id, Object context) {
            this.name = name;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(HTML.ElementNode context) {
            context.children.add(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean format(Appendable output, int depth, boolean prevBlock) throws IOException {
            if (name.isEmpty()) {
                for (Formattable child : children) {
                    prevBlock = child.format(output, depth, prevBlock);
                }
                return prevBlock;
            }

            boolean isBlock = !characterType.contains(name);

            if (prevBlock && isBlock) {
                output.append(EOL).append(indent(depth));
            }
            output.append("<").append(name);

            for (HTML.AttributeNode attr : attrs) {
                attr.format(output, depth, false);
            }

            if (children.isEmpty() && !name.equals("script")) {
                output.append("/>");
            } else {
                output.append(">");

                for (Formattable child : children) {
                    prevBlock = child.format(output, depth + 1, isBlock);
                }

                if (prevBlock) {
                    output.append(EOL).append(indent(depth));
                }
                output.append("</").append(name).append(">");
            }
            return isBlock;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            try {
                StringBuilder builder = new StringBuilder();
                format(builder, 0, false);
                return builder.toString();
            } catch (IOException e) {
                throw I.quiet(e);
            }
        }
    }

    /**
     * 
     */
    private static class TextNode implements Consumer<HTML.ElementNode>, Formattable {

        private final String text;

        /**
         * @param text
         */
        private TextNode(Object text) {
            this.text = String.valueOf(text);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(HTML.ElementNode context) {
            context.children.add(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean format(Appendable output, int depth, boolean prevBlock) throws IOException {
            output.append(text);
            return false;
        }
    }

    /**
     * 
     */
    private static class AttributeNode implements Formattable {

        private String name;

        private String value;

        /**
         * @param name
         * @param value
         */
        private AttributeNode(String name, String value) {
            this.name = name;
            this.value = value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean format(Appendable output, int depth, boolean prevBlock) throws IOException {
            output.append(" ").append(name);

            if (value != null) {
                output.append("='").append(value).append("'");
            }
            return false;
        }
    }
}
