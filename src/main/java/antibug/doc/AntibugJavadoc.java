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
import stylist.value.Color;
import stylist.value.Font;
import stylist.value.Numeric;

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

        site.buildHTML("types/" + info.packageName + "." + info.name + ".html", new HTML() {
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
        data.modules.sort(Comparator.naturalOrder());
        data.packages.sort(Comparator.naturalOrder());
        data.types.sort(Comparator.naturalOrder());

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
                $("body", style.workbench, () -> {
                    // =============================
                    // Top Navigation
                    // =============================
                    $("header", () -> {
                        text("HEADER");
                    });

                    // =============================
                    // Left Side Navigation
                    // =============================
                    $("nav", id("typeNavigation"), style.nav, () -> {
                        $("el-scrollbar", id("typeList"), attr(":native", false));
                    });

                    // =============================
                    // Main Contents
                    // =============================
                    $("main", () -> {
                        text("MAIN");
                    });

                    // =============================
                    // Right Side Navigation
                    // =============================
                    $("aside", () -> {
                        text("ASIDE");
                    });

                    script("https://unpkg.com/vue/dist/vue.js");
                    script("https://unpkg.com/element-ui/lib/index.js");
                    script("root.js", data);
                    script("main.js");
                });
            });
        }
    }

    /**
     * 
     */
    private static interface style extends StyleDSL {

        Color FontColor = Color.rgb(94, 109, 130);

        Font HeadFont = Font.fromGoogle("Oswald");

        Numeric FontSize = Numeric.of(13, px);

        Style type = Style.named("type", () -> {
            cursor.pointer();
        });

        Style nav = () -> {
            display.width(240, px).flex().direction.column();

            $.select(type, () -> {
                display.block();
            });
        };

        Style selector = () -> {
            display.block();
        };

        Style workbench = () -> {
            font.size(FontSize).family("Segoe UI", Font.SansSerif).color(FontColor);
            line.height(1.6);
            display.grid().templateColumns.width(Numeric.of(180, px), Numeric.of(1, fr), Numeric.of(180, px));
        };
    }

    /**
     * Scanned data repository.
     */
    private final class Data {

        /** Type repository. */
        public List<String> modules = new ArrayList();

        /** Type repository. */
        public List<String> packages = new ArrayList();

        /** Type repository. */
        public List<ClassInfo> types = new ArrayList();

        /**
         * Avoid duplication.
         */
        private void add(ClassInfo info) {
            types.add(info);

            if (packages.indexOf(info.packageName) == -1) {
                packages.add(info.packageName);
            }
        }
    }
}
