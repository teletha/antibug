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
import antibug.doc.style.Styles;
import kiss.I;
import kiss.XML;
import kiss.Ⅱ;

public class Javadoc extends DocTool<Javadoc> {

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
    public Javadoc productName(String productName) {
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

            /**
             * {@inheritDoc}
             */
            @Override
            protected void main() {
                $("h2", () -> {
                    $(info.createNameWithModifier());
                });
                $(html(info.comment));

                $("h2", styles.heading, text("Constructor"));
                $("section", () -> {
                    for (ExecutableInfo constructor : info.constructors) {
                        $("h3", id(constructor.id()), styles.heading, text(constructor.name), constructor.createParameter());
                        $(html(constructor.comment));
                        $("dl", () -> {
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

                $("h2", styles.heading, text("Methods"));
                $("section", () -> {
                    for (MethodInfo method : info.methods) {
                        $("h3", id(method.id()), styles.heading, text(method.name), method.createParameter());
                        $(method.comment.v);

                        if (!method.paramTags.isEmpty() || method.returnTag.isPresent()) {
                            $("dl", () -> {
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

            /**
             * {@inheritDoc}
             */
            @Override
            protected void aside() {
                members("Constructors", info.constructors);
                members("Static Fields", info.staticFields());
                members("Fields", info.nonStaticFields());
                members("Static Methods", info.staticMethods());
                members("Methods", info.nonStaticMethods());
            }

            private void members(String title, List<? extends MemberInfo> members) {
                if (members.size() != 0) {
                    $("h5", styles.RNaviTitle, text(title));
                    $("ul", foŕ(members, m -> {
                        $("li", () -> {
                            $(m.createNameWithModifier());

                            if (m instanceof ExecutableInfo) {
                                ExecutableInfo e = (ExecutableInfo) m;
                                $(e.createParameter());
                            }

                            if (m instanceof MethodInfo) {
                                $("span", styles.RNaviReturnType, ((MethodInfo) m).returnType);
                            }

                            if (m instanceof FieldInfo) {
                                $("span", styles.RNaviReturnType, ((FieldInfo) m).type);
                            }
                        });
                    }));
                }
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
        site.buildHTML("index.html", new BaseHTML());
    }

    /**
     * 
     */
    class BaseHTML extends HTML {

        protected final Styles styles = I.make(Styles.class);

        {
            $("html", () -> {
                $("head", () -> {
                    $("meta", attr("charset", "UTF-8"));
                    $("title", text(productName));
                    stylesheet("main.css", styles);
                    stylesheet("https://unpkg.com/element-ui/lib/theme-chalk/index.css");
                    script("https://unpkg.com/vue/dist/vue.js");
                    script("https://unpkg.com/element-ui/lib/index.js");
                });
                $("body", styles.workbench, () -> {
                    // =============================
                    // Top Navigation
                    // =============================
                    $("header", styles.HeaderArea, () -> {
                        $("h1", styles.productTitle, text(productName));
                    });

                    $("main", styles.MainArea, () -> {
                        // =============================
                        // Left Side Navigation
                        // =============================
                        $("nav", id("typeNavigation"), styles.TypeNavigation);

                        // =============================
                        // Main Contents
                        // =============================
                        $("article", styles.contents, () -> {
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

                    script("root.js", data);
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
