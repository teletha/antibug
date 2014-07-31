/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.javasource;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import kiss.I;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;

/**
 * @version 2014/07/31 10:41:40
 */
public class SourceParser {

    public static final void main(final String[] args) throws Exception {
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

        Path dir = I.locate("src/main/java/antibug/javadoc");
        List<File> files = new ArrayList();

        for (Path path : I.walk(dir, "Documents.java")) {
            files.add(path.toFile());
        }

        StandardJavaFileManager fileManager = javac.getStandardFileManager(null, null, null);
        CompilationTask task = javac.getTask(null, fileManager, null, null, null, fileManager.getJavaFileObjectsFromFiles(files));

        JavacTask javacTask = (JavacTask) task;
        Trees trees = Trees.instance(task);

        for (CompilationUnitTree unit : javacTask.parse()) {
            LineWriter writer = new LineWriter();
            SourceMapper mapper = new SourceMapper(unit, trees.getSourcePositions());

            // start analyzing
            new Analyzer(writer, mapper).printExpr((JCTree) unit);

            int number = 1;

            for (StringBuilder line : writer) {
                System.out.println(number++ + "   " + line);
            }
        }
    }
}
