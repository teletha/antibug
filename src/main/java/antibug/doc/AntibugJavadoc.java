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

    /** Preference */
    private String productName = "Your Product";

    /**
     * Configure the produc name.
     * 
     * @param productName
     * @return
     */
    public AntibugJavadoc productName(String productName) {
        if (productName != null && productName.length() != 0) {
            this.productName = productName;
        }
        return this;
    }

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
                    $("header", style.header, () -> {
                        $("h1", style.productTitle, letter(productName));
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

        // color palette - https://coolors.co/e63946-f1faee-a8dadc-457b9d-1d3557
        Color BackColor = Color.rgb(241, 250, 238);

        Color SecondColor = Color.rgb(168, 218, 220);

        Color AccentColor = Color.rgb(69, 123, 157);

        Color DarkColor = Color.rgb(29, 53, 87);

        Color FontColor = Color.rgb(94, 109, 130);

        Color ParagraphColor = Color.rgb(40, 165, 245);

        Color ListColor = Color.rgb(250, 210, 50);

        Color SignatureColor = Color.rgb(221, 81, 76);

        Color CodeColor = Color.rgb(94, 185, 94);

        Font HeadFont = Font.fromGoogle("Oswald");

        Numeric FontSize = Numeric.of(13, px);

        Numeric HeaderHeight = Numeric.of(80, px);

        Numeric MaxWidth = Numeric.of(1200, px);

        Style workbench = () -> {
            font.size(FontSize).family("Segoe UI", Font.SansSerif).color(FontColor);
            line.height(1.6);
            display.maxWidth(MaxWidth);
            margin.auto();
        };

        Style header = () -> {
            background.color(Color.White);
            position.sticky().top(0, rem);
            display.maxWidth(MaxWidth).height(HeaderHeight).zIndex(10);
            margin.auto();
            border.bottom.color(ParagraphColor).width(1, px).solid();
        };

        Style productTitle = () -> {
            font.size(1.5, rem).family(HeadFont).weight.normal().color(ParagraphColor);
        };

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
