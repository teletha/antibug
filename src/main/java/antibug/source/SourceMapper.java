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

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.Tree;
import com.sun.source.util.SourcePositions;

/**
 * @version 2014/07/31 14:36:06
 */
public class SourceMapper {

    /** The compiled source. */
    private final CompilationUnitTree unit;

    /** The source position manager. */
    private final SourcePositions positions;

    /** The line manager. */
    private final LineMap map;

    /** The actual source reader. */
    private final BufferedReader reader;

    /** The actual source line. */
    private int currentLine = 1;

    /**
     * @param positions
     * @param map
     * @throws IOException
     */
    SourceMapper(CompilationUnitTree unit, SourcePositions positions) throws IOException {
        this.unit = unit;
        this.positions = positions;
        this.map = unit.getLineMap();
        this.reader = new BufferedReader(unit.getSourceFile().openReader(true));
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
            while (++currentLine < lineNumber) {
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
}
