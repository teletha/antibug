/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.util;

import java.nio.file.Files;

import org.junit.Test;

/**
 * @version 2012/02/18 10:05:02
 */
public class NoteTest {

    @Test
    public void readAndWrite() throws Exception {
        Note note = new Note("memo");

        assert new String(Files.readAllBytes(note)).equals("memo");
        Files.write(note, "write".getBytes());
        assert new String(Files.readAllBytes(note)).equals("write");
    }

    @Test
    public void string() throws Exception {
        assert new Note("memo").toString().equals("memo");
    }
}
