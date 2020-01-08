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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /** PackageName-URL pair. */
    private static final Map<String, String> externalDocumentLocations = new HashMap();

    /**
     * Returns the URL of the document with the specified type name.
     * 
     * @param moduleName Module name. Null or empty string is ignored.
     * @param packageName Package name. Null or empty string is ignored.
     * @param enclosingName Enclosing type name. Null or empty string is ignored.
     * @param typeName Target type's simple name.
     * @return Resoleved URL.
     */
    public static final String resolveDocumentLocation(String moduleName, String packageName, String enclosingName, String typeName) {
        String url = externalDocumentLocations.get(packageName);

        if (url != null) {
            StringBuilder builder = new StringBuilder(url);
            if (moduleName != null && moduleName.length() != 0) builder.append(moduleName).append('/');
            if (packageName != null && packageName.length() != 0) builder.append(packageName.replace('.', '/')).append('/');
            if (enclosingName != null && enclosingName.length() != 0) builder.append(enclosingName).append('.');
            builder.append(typeName).append(".html");

            return builder.toString();
        } else {
            StringBuilder builder = new StringBuilder("/types/");
            if (packageName != null && packageName.length() != 0) builder.append(packageName).append('.');
            if (enclosingName != null && enclosingName.length() != 0) builder.append(enclosingName).append('.');
            builder.append(typeName).append(".html");

            return builder.toString();
        }
    }
    // https://docs.oracle.com/en/java/javase/13/docs/api/java/util/concurrent/ScheduledExecutorService.html
    // https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/util/concurrent/ScheduledExecutorService.html

    /** The scanned data. */
    private final Data data = new Data();

    /** The site builder. */
    private SiteBuilder site;

    /** Preference */
    private String productName = "Your Product";

    {
        // built-in external API
        externalDoc("https://docs.oracle.com/en/java/javase/13/docs/api/");
    }

    /**
     * Configure the produc name.
     * 
     * @param productName
     * @return Chainable API.
     */
    public Javadoc productName(String productName) {
        if (productName != null && productName.length() != 0) {
            this.productName = productName;
        }
        return this;
    }

    /**
     * Specifies the URL of the resolvable external document.
     * 
     * @param urls A list of document URL．
     * @return Chainable API.
     */
    public Javadoc externalDoc(String... urls) {
        if (urls != null) {
            for (String url : urls) {
                if (url != null && url.startsWith("http") && url.endsWith("/api/")) {
                    try {
                        for (XML a : I.xml(new URL(url + "overview-tree.html")).find(".horizontal a")) {
                            externalDocumentLocations.put(a.text(), url);
                        }
                    } catch (MalformedURLException e) {
                        throw I.quiet(e);
                    }
                }
            }
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
        TypeResolver resolver = new TypeResolver();
        resolver.register(root);

        ClassInfo info = new ClassInfo(root, resolver);
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

                I.signal(info.constructors).effectOnce(() -> {
                }).to(c -> {
                    $("section", styles.MainSection, () -> {
                        $("h2", id(c.id()), styles.MainTitle, c.createNameWithModifier(), c.createParameter());
                        $(html(c.comment));
                        $("dl", () -> {
                            if (!c.paramTags.isEmpty()) {
                                $("dt", text("Parameters"));
                                for (Ⅱ<String, XML> param : c.paramTags) {
                                    $("dd", () -> {
                                        $("b", text(param.ⅰ));
                                        $(param.ⅱ);
                                    });
                                }
                            }
                        });
                    });
                });

                I.signal(info.methods).effectOnce(() -> {
                }).to(m -> {
                    $("section", styles.MainSection, () -> {
                        $("h2", id(m.id()), styles.MainTitle, m.createNameWithModifier(), m.createParameter(), m.returnType);
                        $(m.comment.v);

                        if (!m.paramTags.isEmpty() || m.returnTag.isPresent()) {
                            $("dl", () -> {
                                if (!m.paramTags.isEmpty()) {
                                    $("dt", text("Parameters"));
                                    for (Ⅱ<String, XML> param : m.paramTags) {
                                        $("dd", () -> {
                                            $("b", text(param.ⅰ), () -> {
                                            });
                                            $(param.ⅱ);
                                        });
                                    }
                                }

                                m.returnTag.to(tag -> {
                                    $("dt", text("Return"));
                                    $("dd", tag);
                                });
                            });
                        }
                    });
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
        site.buildHTML("javadoc.html", new BaseHTML());
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
                    $("title", text(productName + " API"));
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
                        $("h1", styles.HeaderTitle, text(productName + " API"));
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
