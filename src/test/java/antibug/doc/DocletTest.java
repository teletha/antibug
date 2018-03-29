/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.doc;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Locale;

import javax.tools.DocumentationTool;
import javax.tools.DocumentationTool.DocumentationTask;
import javax.tools.DocumentationTool.Location;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.Test;

import filer.Filer;
import kiss.I;

/**
 * @version 2018/03/30 2:31:46
 */
public class DocletTest {

    @Test
    public void test() {

    }

    public static void main(String[] args) throws Exception {
        Path output = Filer.locate(".doc");
        Filer.createDirectory(output);

        DocumentationTool doc = ToolProvider.getSystemDocumentationTool();
        StandardJavaFileManager manager = doc.getStandardFileManager(null, Locale.getDefault(), StandardCharsets.UTF_8);
        manager.setLocation(Location.DOCUMENTATION_OUTPUT, I.list(output.toFile()));

        Iterable<? extends JavaFileObject> sources = manager.getJavaFileObjects(new File("src/main/java/antibug/AntiBug.java"));

        DocumentationTask task = doc.getTask(null, manager, null, Example.class, null, sources);
        Boolean result = task.call();
        System.out.println(result);
    }
}
