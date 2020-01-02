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
import kiss.I;
import kiss.XML;
import kiss.Ⅱ;

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

        site.buildHTML("types/" + info.packageName + "." + info.name + ".html", new BaseHTML() {

            @Override
            protected void contents() {
                $("h2", Styles.heading, text(info.name));
                $(html(info.comment));

                $("h2", Styles.heading, text("Constructor"));
                $("section", Styles.section, () -> {
                    for (ExecutableInfo constructor : info.constructors) {
                        $("h3", Styles.heading, signature(constructor));
                        $(html(constructor.comment));
                        $("dl", Styles.dl, () -> {
                            if (!constructor.paramTags.isEmpty()) {
                                $("dt", text("Parameters"));
                                for (Ⅱ<String, XML> param : constructor.paramTags) {
                                    $("dd", () -> {
                                        $("b", text(param.ⅰ));
                                        $(param.ⅱ);
                                    });
                                }
                            }
                        });
                    }
                });

                $("h2", Styles.heading, text("Methods"));
                $("section", Styles.section, () -> {
                    for (MethodInfo method : info.methods) {
                        $("h3", Styles.heading, signature(method));
                        $(method.comment.v);

                        if (!method.paramTags.isEmpty() || method.returnTag.isPresent()) {
                            $("dl", Styles.dl, () -> {
                                if (!method.paramTags.isEmpty()) {
                                    $("dt", text("Parameters"));
                                    for (Ⅱ<String, XML> param : method.paramTags) {
                                        $("dd", () -> {
                                            $("b", text(param.ⅰ), () -> {
                                            });
                                            $(param.ⅱ);
                                        });
                                    }
                                }

                                method.returnTag.to(tag -> {
                                    $("dt", text("Return"));
                                    $("dd", tag);
                                });
                            });
                        }
                    }
                });
            }
        });
    }

    private XML signature(ExecutableInfo constructor) {
        XML root = I.xml("fragment");
        root.append(constructor.name + "(");
        for (int i = 0, size = constructor.params.size(); i < size; i++) {
            Ⅱ<String, XML> param = constructor.params.get(i);
            root.append(param.ⅱ).append(" ").append(param.ⅰ);

            if (i + 1 != size) {
                root.append(", ");
            }
        }
        root.append(")");

        return root;

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
        site.buildHTML("index.html", new BaseHTML());
    }

    /**
     * 
     */
    class BaseHTML extends HTML {
        {
            $("html", () -> {
                $("head", () -> {
                    $("meta", attr("charset", "UTF-8"));
                    $("title", text(productName));
                    $("base", attr("href", "/"));
                    stylesheet("main.css", Styles.class);
                    stylesheet("https://unpkg.com/element-ui/lib/theme-chalk/index.css");
                    script("https://unpkg.com/vue/dist/vue.js");
                    script("https://unpkg.com/element-ui/lib/index.js");
                });
                $("body", Styles.workbench, () -> {
                    // =============================
                    // Top Navigation
                    // =============================
                    $("header", Styles.header, () -> {
                        $("h1", Styles.productTitle, text(productName));
                    });

                    $("main", Styles.main, () -> {
                        // =============================
                        // Left Side Navigation
                        // =============================
                        $("nav", id("typeNavigation"), Styles.nav, () -> {
                            $("el-scrollbar", id("typeList"), attr(":native", false));
                        });

                        // =============================
                        // Main Contents
                        // =============================
                        $("article", Styles.article, () -> {
                            $("section", () -> {
                                contents();
                            });

                            // =============================
                            // Right Side Navigation
                            // =============================
                            $("aside", text("ASIDE TEXT"));
                        });
                    });

                    script("root.js", data);
                    script("main.js");
                });
            });
        }

        protected void contents() {

        }
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
