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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

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

    /** PackageName-URL pair. */
    private final Map<String, String> externals = new HashMap();

    /** The internal pacakage names. */
    private final Set<String> internals = new HashSet();

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
                            externals.put(a.text(), url);
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
        internals.addAll(findSourcePackages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void process(TypeElement root) {
        TypeResolver resolver = new TypeResolver(externals, internals, root);
        ClassInfo info = new ClassInfo(root, resolver);
        data.add(info);

        site.buildHTML("types/" + info.packageName + "." + info.name + ".html", new BaseHTML() {

            /**
             * {@inheritDoc}
             */
            @Override
            protected void main() {
                $("h2", () -> {
                    $(info.createModifier(), info.createName());
                });
                $(html(info.comment));

                I.signal(info.constructors).effectOnce(() -> {
                }).to(c -> {
                    $("section", styles.MainSection, () -> {
                        $("h2", id(c.id()), styles.MainTitle, c.createModifier(), c.createName(), c.createParameter());
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
                        $("h2", id(m.id()), styles.MainTitle, () -> {
                            $(m.createModifier(), m.createReturnType(), m.createName(), m.createParameter());
                        });

                        int types = m.numberOfTypeVariables();
                        int params = m.numberOfParameters();
                        int returns = m.returnVoid() ? 0 : 1;
                        int exceptions = m.numberOfExceptions();

                        if (0 < types + params + returns + exceptions) {
                            $("section", styles.MainSignature, () -> {
                                $("table", styles.SignatureTable, () -> {
                                    IntStream.range(0, types).forEach(i -> {
                                        $("tr", styles.SignatureTypeVariable, () -> {
                                            $("td", m.createTypeVariable(i));
                                            $("td", m.createTypeVariableComment(i));
                                        });
                                    });

                                    IntStream.range(0, params).forEach(i -> {
                                        $("tr", styles.SignatureParameter, () -> {
                                            $("td", m.createParameter(i), text(" "), m.createParameterName(i));
                                            $("td", m.createParameterComment(i));
                                        });
                                    });

                                    if (0 < returns) {
                                        $("tr", styles.SignatureReturn, () -> {
                                            $("td", m.createReturnType());
                                            $("td", m.createReturnComment());
                                        });
                                    }

                                    IntStream.range(0, exceptions).forEach(i -> {
                                        $("tr", styles.SignatureException, () -> {
                                            $("td", m.createException(i));
                                            $("td", m.createExceptionComment(i));
                                        });
                                    });
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
                            $(m.createModifier());
                            $(m.createName());

                            if (m instanceof ExecutableInfo) {
                                ExecutableInfo e = (ExecutableInfo) m;
                                $(e.createParameter());
                            }

                            if (m instanceof MethodInfo) {
                                $(((MethodInfo) m).createReturnType());
                            }

                            if (m instanceof FieldInfo) {
                                $(((FieldInfo) m).createType());
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
