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

import java.util.function.Consumer;

import kiss.I;
import kiss.Tree;
import kiss.Variable;
import kiss.XML;
import stylist.Style;
import stylist.StyleDSL;

/**
 * Domain Specific Language for HTML.
 */
public abstract class HTML extends Tree<String, XML> {

    /**
     * 
     */
    public HTML() {
        super((name, id, context) -> {
            return I.xml("<" + name + "/>");
        }, null, (follower, current) -> {
            if (follower instanceof Style) {
                Style style = (Style) follower;
                for (String className : style.names()) {
                    current.addClass(className);
                }
            } else {
                System.out.println(follower + "  " + current);
                follower.accept(current);
            }
        });
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
    protected final Consumer<XML> attr(Object name, Object value) {
        return parent -> {
            if (name != null) {
                String n = String.valueOf(name);

                if (!n.isEmpty()) {
                    parent.attr(n, String.valueOf(value));
                }
            }
        };
    }

    /**
     * Declare html.
     * 
     * @param html
     * @return
     */
    protected final Consumer<XML> html(Variable<XML> html) {
        return parent -> {

        };
    }

    /**
     * <p>
     * accept text node.
     * </p>
     * 
     * @param text A text.
     */
    protected final Consumer<XML> text(Object text) {
        return parent -> {
            parent.append(parent.to().getOwnerDocument().createTextNode(String.valueOf(text)));
        };
    }

    /**
     * Shorthand method to declare class attribute.
     * 
     * @param className
     * @return
     */
    protected final Consumer<XML> id(String id) {
        return parent -> {
            parent.attr("id", id);
        };
    }

    /**
     * Shorthand method to declare class attribute.
     * 
     * @param className
     * @return
     */
    protected final Consumer<XML> clazz(String className) {
        return parent -> {
            parent.addClass(className);
        };
    }

    /**
     * Shorthand method to write stylesheet link tag.
     * 
     * @param uri URI to css.
     */
    protected final void stylesheet(String uri) {
        $("link", attr("rel", "stylesheet"), attr("href", uri));
    }

    /**
     * Build CSS file and return the path of the generated file.
     * 
     * @param styles A style definition class to write.
     * @return A path to the generated file.
     */
    protected final void stylesheet(String path, Class<? extends StyleDSL> styles) {
        $("link", attr("rel", "stylesheet"), attr("href", SiteBuilder.current.buildCSS(path, styles)));
    }

    /**
     * Shorthand method to write script tag.
     * 
     * @param uri URI to script.
     */
    protected final void script(String uri) {
        $("script", attr("src", uri));
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
}
