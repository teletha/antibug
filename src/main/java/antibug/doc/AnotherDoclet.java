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

import java.util.Locale;
import java.util.Set;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.sun.source.util.DocTrees;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

/**
 * <h>DONT USE THIS CLASS</h>
 * <p>
 * It is a Doclet for internal use, but it is public because it cannot be made private due to the
 * specifications of the documentation tool.
 * </p>
 */
public class AnotherDoclet implements Doclet {

    /** Guilty Accessor. */
    public static Types TypeUtils;

    /** Guilty Accessor. */
    public static Elements ElementUtils;

    /** Guilty Accessor. */
    public static DocTrees DocUtils;

    DocTrees trees;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void init(Locale locale, Reporter reporter) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean run(DocletEnvironment env) {
        AnotherDoclet.DocUtils = env.getDocTrees();
        AnotherDoclet.ElementUtils = env.getElementUtils();
        AnotherDoclet.TypeUtils = env.getTypeUtils();

        try {
            DocTool.self.initialize();

            for (Element element : env.getSpecifiedElements()) {
                switch (element.getKind()) {
                case MODULE:
                    DocTool.self.process((ModuleElement) element);
                    break;

                case PACKAGE:
                    DocTool.self.process((PackageElement) element);
                    break;

                default:
                    DocTool.self.process((TypeElement) element);
                    break;
                }
            }
        } finally {
            DocTool.self.complete();
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getName() {
        return getClass().getSimpleName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Set<? extends Option> getSupportedOptions() {
        return Set.of();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
}