/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.doc.site;

import antibug.doc.ClassInfo;
import antibug.doc.Javadoc;
import antibug.doc.builder.HTML;
import antibug.doc.style.Styles;
import kiss.I;

/**
 * 
 */
public class BaseHTML extends HTML {

    protected final Styles styles = I.make(Styles.class);

    protected final ClassInfo info;

    /**
     * @param info
     */
    public BaseHTML(Javadoc javadoc, ClassInfo info) {
        this.info = info;

        $("html", () -> {
            $("head", () -> {
                $("meta", attr("charset", "UTF-8"));
                $("title", text(javadoc.productName() + " API"));
                stylesheet("main.css", styles);
                stylesheet("https://unpkg.com/element-ui/lib/theme-chalk/index.css");
                script("https://unpkg.com/vue/dist/vue.js");
                script("https://unpkg.com/vue-router/dist/vue-router.js");
                script("https://unpkg.com/element-ui/lib/index.js");
            });
            $("body", styles.workbench, () -> {
                // =============================
                // Top Navigation
                // =============================
                $("header", styles.HeaderArea, () -> {
                    $("h1", styles.HeaderTitle, text(javadoc.productName() + " API"));
                });

                $("main", styles.MainArea, () -> {
                    // =============================
                    // Left Side Navigation
                    // =============================
                    $("nav", id("typeNavigation"), styles.TypeNavigation, () -> {
                        $("div");
                    });

                    // =============================
                    // Main Contents
                    // =============================
                    $("article", styles.contents, () -> {
                        $("router-view");
                        main();
                    });

                    // =============================
                    // Right Side Navigation
                    // =============================
                    $("aside", styles.RNavi, () -> {
                        $("div", styles.RNaviStickyBlock, () -> {
                            aside();
                        });
                    });
                });

                script("root.js", javadoc.data);
                script("main.js");
            });
        });
    }

    /**
     * Write your main contents.
     */
    protected void main() {
    }

    /**
     * Write your aside contents.
     */
    protected void aside() {
    }
}