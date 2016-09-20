/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source.link;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Ignore;
import org.junit.Test;

import antibug.source.Source;
import antibug.source.low.Array;
import antibug.source.low.Lambda;
import antibug.source.low.MethodCall;
import antibug.source.low.Operator;
import antibug.source.low.Statement;
import kiss.I;
import kiss.XML;

/**
 * @version 2014/08/09 10:24:35
 */
public class LinkTest {

    @Test
    @Ignore
    public void samePackage() throws Exception {
        XML xml = parse(Linker.class);
        assert findLink(xml, String.class) == 1;
    }

    @Test
    public void samePackage2() throws Exception {
        parse(MethodCall.class);
        parse(Lambda.class);
        parse(Operator.class);
        parse(Statement.class);
        parse(Array.class);
    }

    private static int findLink(XML xml, Class target) {
        return xml.find("type[href=\"" + target.getName() + "\"").size();
    }

    /**
     * Parse target class.
     * 
     * @param target
     * @return
     */
    private static XML parse(Class target) {
        Path source = I.locate("src/test/java").resolve(target.getName().replace(".", "/") + ".java");

        if (Files.notExists(source)) {
            source = I.locate("src/main/java").resolve(target.getName().replace(".", "/") + ".java");
        }

        return Source.parse(source);
    }
}
