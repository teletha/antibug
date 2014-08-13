/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import js.util.HashMap;
import kiss.I;
import kiss.XML;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.Tree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;

/**
 * @version 2014/08/07 10:24:20
 */
public class Source {

    /** The root xml. */
    private final XML root;

    /** The compiled source. */
    private final CompilationUnitTree unit;

    /** The source position manager. */
    private final SourcePositions positions;

    /** The line mapping manager. */
    private final LineMap map;

    /** The actual source reader. */
    private final BufferedReader reader;

    /** The source visitor. */
    private final SourceTreeVisitor visitor;

    /** The actual source line number. */
    private int actualLine = 1;

    /** The logical AST line number. */
    private int logicalLine = 1;

    /** The latest line. */
    SourceXML latestLine;

    /** The current AST indent size. */
    private int indentSize = 0;

    /** The indent pattern. */
    private String indent = "    ";

    /**
     * @param positions
     * @param map
     * @throws IOException
     */
    Source(XML xml, CompilationUnitTree unit, SourcePositions positions) throws IOException {
        this.root = xml;
        this.unit = unit;
        this.positions = positions;
        this.map = unit.getLineMap();
        this.reader = new BufferedReader(unit.getSourceFile().openReader(true));
        this.visitor = new SourceTreeVisitor(this);
    }

    /**
     * <p>
     * Create new line.
     * </p>
     */
    SourceXML startNewLine() {
        SourceXML newLine = new SourceXML(logicalLine, root.child("line").attr("n", logicalLine++), visitor, this);

        newLine.text(indent());

        return this.latestLine = newLine;
    }

    /**
     * <p>
     * Build indent pattern.
     * </p>
     * 
     * @return
     */
    private String indent() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < indentSize; i++) {
            builder.append(indent);
        }
        return builder.toString();
    }

    /**
     * <p>
     * Trace line.
     * </p>
     * 
     * @param current
     * @param context TODO
     */
    SourceXML traceLine(Tree current, SourceXML context) {
        int actualLine = getLine(current);

        if (actualLine < logicalLine) {
            return context;
        }

        while (logicalLine < actualLine) {
            startNewLine().text(readLineFrom(logicalLine) + "☆");
        }
        return startNewLine();
    }

    /**
     * <p>
     * Compute line number of the given token.
     * </p>
     * 
     * @param tree
     * @return
     */
    int getLine(Tree tree) {
        return (int) map.getLineNumber(positions.getStartPosition(unit, tree));
    }

    /**
     * <p>
     * Compute line number of the given token.
     * </p>
     * 
     * @param tree
     * @return
     */
    int getEndLine(Tree tree) {
        return (int) map.getLineNumber(positions.getEndPosition(unit, tree));
    }

    /**
     * @param i
     * @return
     */
    private String readLineFrom(int lineNumber) {
        try {
            while (++actualLine < lineNumber) {
                reader.readLine();
            }
            String line = reader.readLine();

            line = removeHeadWhitespace(line);

            if (line.startsWith("*")) {
                line = " ".concat(line);
            }
            return line;
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }

    /**
     * @param line
     * @return
     */
    private String removeHeadWhitespace(String line) {
        int index = 0;

        while (index < line.length() && Character.isWhitespace(line.charAt(index))) {
            index++;
        }

        return line.substring(index);
    }

    /**
     * <p>
     * Increase indent size.
     * </p>
     */
    void increaseIndent() {
        increase(1);
    }

    /**
     * <p>
     * Increase indent size.
     * </p>
     */
    void increase(int size) {
        this.indentSize += size;
    }

    /**
     * <p>
     * Decrease indent size.
     * </p>
     */
    void decreaseIndent() {
        decrease(1);
    }

    /**
     * <p>
     * Decrease indent size.
     * </p>
     */
    void decrease(int size) {
        this.indentSize -= size;
    }

    /**
     * @param exceptions
     */
    public SourceXML lineFor(List<? extends Tree> trees) {
        return lineFor(trees.get(0));
    }

    private Deque<Tree> wrappers = new ArrayDeque();

    /**
     * @param exceptions
     */
    public SourceXML lineFor(Tree tree) {
        int start = latestLine.line;
        int end = getEndLine(tree);
        boolean shouldWrap = start != end;

        if (shouldWrap) {
            wrappers.addLast(tree);
            indentSize += 2;
            return startNewLine();
        } else {
            return latestLine;
        }
    }

    /**
     * @param expression
     * @param context
     * @return
     */
    public SourceXML lineFor(Tree tree, SourceXML context) {
        int start = context.line;
        int end = getEndLine(tree);
        boolean shouldWrap = start != end;

        if (shouldWrap) {
            wrappers.addLast(tree);
            indentSize += 2;
            return startNewLine();
        } else {
            return context;
        }
    }

    /**
     * <p>
     * Create new line.
     * </p>
     */
    private SourceXML startNewLine(int additionalIndentSize) {
        SourceXML newLine = new SourceXML(logicalLine, root.child("line").attr("n", logicalLine++), visitor, this);

        newLine.text(indent(additionalIndentSize));

        return this.latestLine = newLine;
    }

    /**
     * <p>
     * Build indent pattern.
     * </p>
     * 
     * @return
     */
    private String indent(int additionalIndentSize) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < indentSize + additionalIndentSize; i++) {
            builder.append(indent);
        }
        return builder.toString();
    }

    /**
     * @param tree
     */
    public void unwrap(Tree tree) {
        if (wrappers.peekLast() == tree) {
            wrappers.pollLast();
            indentSize -= 2;
        }
    }

    /** The normal import. */
    private final Map<String, Class> imports = new HashMap();

    /**
     * <p>
     * Import type.
     * </p>
     * 
     * @param tree
     */
    public void importType(ImportTree tree) {
        try {
            String fqcn = tree.getQualifiedIdentifier().toString();

            if (tree.isStatic()) {
                // static import
            } else {
                // normal import
                if (fqcn.endsWith(".*")) {
                    // wildcard
                } else {
                    // fqcn
                    Class clazz = Class.forName(fqcn);
                    imports.put(clazz.getSimpleName(), clazz);
                }
            }
        } catch (ClassNotFoundException e) {
            throw I.quiet(e);
        }
    }

    /**
     * @param name
     */
    public void resolveType(String name) {

    }

    /**
     * @param name
     */
    public void resolveType(Tree tree) {
        String access = tree.toString();

        if (access.equals("this")) {
            // ignore
            System.out.println(access + " ->  keyword");
        } else if (imports.containsKey(access)) {
            // normal import
            System.out.println(access + " ->  import " + imports.get(access));
        } else {
            System.out.println(access);
        }
    }

    /** The compiler interface. */
    private static final JavaCompiler compiler;

    /** The file manager. */
    private static final StandardJavaFileManager manager;

    static {
        compiler = ToolProvider.getSystemJavaCompiler();
        manager = compiler.getStandardFileManager(null, null, null);
    }

    /**
     * <p>
     * Parse the given source and build {@link XML} representation.
     * </p>
     * 
     * @param path A source file.
     * @return A {@link XML}.
     */
    public static XML parse(Path path) {
        try {
            JavacTask task = (JavacTask) compiler.getTask(null, manager, null, null, null, manager.getJavaFileObjects(path.toFile()));
            Trees trees = Trees.instance(task);

            XML xml = I.xml("source");

            for (CompilationUnitTree unit : task.parse()) {
                Source lines = new Source(xml, unit, trees.getSourcePositions());

                // start analyzing
                unit.accept(lines.visitor, null);
            }
            return xml;
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }
}
