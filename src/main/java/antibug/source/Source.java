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

import kiss.I;
import kiss.XML;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;

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
    String readLineFrom(int lineNumber) {
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
    void increase() {
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
    void decrease() {
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
     * 
     */
    public void parse() {
        unit.accept(visitor, null);
    }
}
