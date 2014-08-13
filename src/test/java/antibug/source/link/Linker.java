/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source.link;

import static java.lang.Math.*;

import java.util.List;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilder;

import kiss.Disposable;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import antibug.source.link.external.ExternalPackage;

/**
 * @version 2014/08/09 10:40:26
 */
public class Linker {

    SamePackage samePackage;

    ExternalPackage externalPackage;

    String javaLang;

    List javaUtil;

    DocumentBuilder javax;

    Element dom;

    InputSource sax;

    Disposable externalModule;

    Consumer<String> generic;

    com.sun.tools.javac.util.List samaName;

    {
        max(10, 1);
        min(10, 1);
    }
}
