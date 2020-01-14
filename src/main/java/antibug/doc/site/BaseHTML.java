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

/**
 * 
 */
public class BaseHTML extends HTML {

    protected final ClassInfo info;

    protected final Javadoc javadoc;

    /**
     * @param info
     */
    public BaseHTML(Javadoc javadoc, ClassInfo info) {
        this.info = info;
        this.javadoc = javadoc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void declare() {
        $("html", () -> {
            $("head", () -> {
                $("meta", attr("charset", "UTF-8"));
                $("title", text(javadoc.productName() + " API"));
                stylesheet("/main.css");
                stylesheet("https://unpkg.com/element-ui/lib/theme-chalk/index.css");
                script("https://unpkg.com/vue/dist/vue.js");
                script("https://unpkg.com/vue-router/dist/vue-router.js");
                script("https://unpkg.com/element-ui/lib/index.js");
            });
            $("body", Styles.workbench, () -> {
                // =============================
                // Top Navigation
                // =============================
                $("header", Styles.HeaderArea, () -> {
                    $("h1", Styles.HeaderTitle, text(javadoc.productName() + " API"));
                });

                $("main", Styles.MainArea, () -> {
                    // =============================
                    // Left Side Navigation
                    // =============================
                    $("nav", Styles.TypeNavigation, () -> {
                        $("div");
                    });

                    // =============================
                    // Main Contents
                    // =============================
                    $("article", Styles.contents, () -> {
                        $("router-view");
                        main();
                    });

                    // =============================
                    // Right Side Navigation
                    // =============================
                    $("aside", Styles.RNavi, () -> {
                        $("div", Styles.RNaviStickyBlock, () -> {
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