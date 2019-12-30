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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

import antibug.doc.site.HTML;
import antibug.doc.site.SiteBuilder;
import stylist.Style;
import stylist.StyleDSL;

public class AntibugJavadoc extends AntibugDocumentationTool<AntibugJavadoc> {

    /** The scanned data. */
    private final Data data = new Data();

    /** The site builder. */
    private SiteBuilder site;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        site = SiteBuilder.root(output()).guard("index.html", "main.js");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void process(TypeElement root) {
        ClassInfo info = new ClassInfo(root);
        data.add(info);

        site.buildHTML("types/" + info.fqcn + ".html", new HTML() {
            {
                $("html", () -> {
                    $("body", () -> {

                    });
                });
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void process(PackageElement root) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void process(ModuleElement root) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void complete() {
        // sort data
        data.packages.sort(Comparator.naturalOrder());

        // build HTML
        site.buildHTML("index.html", new IndexHTML());
    }

    /**
     * 
     */
    private final class IndexHTML extends HTML {
        {
            $("html", () -> {
                $("head", () -> {
                    $("meta", attr("charset", "UTF-8"));
                    stylesheet("https://unpkg.com/element-ui/lib/theme-chalk/index.css");
                    stylesheet("main.css", style.class);
                    stylesheet("javadoc.css", BuiltinStyles.class);
                });
                $("body", () -> {
                    $("nav", () -> {
                        $("ul", id("packageList"), style.packageList, () -> {
                            $("li", attr("v-for", "package in packages"), () -> {
                                text("{{package}}");
                            });
                        });
                        $("ul", id("typeList"), () -> {
                            $("li", attr("v-for", "type in types"), () -> {
                                text("{{type}}");
                            });
                        });
                    });
                    $("main", attr("id", "app"), () -> {
                        $("h1", () -> {
                            $("em", () -> {
                                text("{{message}}");
                            });
                        });
                    });
                    script("https://unpkg.com/vue/dist/vue.js");
                    script("https://unpkg.com/element-ui/lib/index.js");
                    script("data.js", data);
                    script("main.js");
                });
            });
        }
    }

    /**
     * 
     */
    private static interface style extends StyleDSL {

        Style packageList = () -> {
            display.height(200, px);
        };
    }

    /**
     * Scanned data repository.
     */
    private final class Data {

        /** Type repository. */
        public List<String> packages = new ArrayList();

        /** Type repository. */
        public List<String> types = new ArrayList();

        /**
         * Avoid duplication.
         */
        private void add(ClassInfo info) {
            types.add(info.fqcn);

            if (packages.indexOf(info.packageName) == -1) {
                packages.add(info.packageName);
            }
        }
    }
}
