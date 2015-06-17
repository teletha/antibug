/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AccessControlException;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import kiss.I;
import kiss.model.ClassUtil;

/**
 * @version 2014/07/10 19:19:18
 */
public class CleanRoomTest {

    private static final Path base = I.locateTemporary();

    @Rule
    @ClassRule
    public static final CleanRoom room = new CleanRoom(base);

    @Rule
    @ClassRule
    public static final CleanRoom room2 = new CleanRoom();

    @Test
    public void locateFile() {
        Path file = room.locateFile("empty");

        assert Files.exists(file);
        assert Files.isRegularFile(file);

    }

    @Test
    public void locateArchive() {
        Path file = room.locateFile("jar");
        I.copy(ClassUtil.getArchive(Test.class), file);

        file = room.locateArchive("jar");
        assert Files.exists(file.resolve("org/junit/Test.class"));
        assert Files.isRegularFile(file.resolve("org/junit/Test.class"));
        assert Files.exists(file.resolve("org/junit"));
        assert Files.isDirectory(file.resolve("org/junit"));
        assert Files.notExists(file.resolve("not-exists"));
    }

    @Test
    public void locateDirectoryFromAbsent() {
        Path file = room.locateDirectory("absent");

        assert Files.exists(file);
        assert Files.isDirectory(file);
    }

    @Test
    public void locateDirectoryFromPresent() {
        Path file = room.locateDirectory("dir");

        assert Files.exists(file);
        assert Files.isDirectory(file);
    }

    @Test
    public void locateAbsent() {
        Path file = room.locateAbsent("absent.txt");

        assert Files.notExists(file);
        assert!Files.isRegularFile(file);
        assert!Files.isDirectory(file);
    }

    @Test
    public void locatePresentFile() {
        Path file = room.locateAbsent("present.txt");

        // the specified file doesn't exist yet
        assert!Files.exists(file);

        // create file
        file = room.locateFile("present.txt");
        assert Files.exists(file);

        // the file has already existed
        file = room.locateFile("present.txt");
        assert Files.exists(file);
    }

    @Test
    public void locatedFileCanDelete() throws Exception {
        Path file = room.locateFile("empty");

        assert Files.exists(file);
        assert Files.deleteIfExists(file);
        assert Files.notExists(file);
    }

    @Test(expected = AccessControlException.class)
    public void cantWriteInOriginalDirectory() throws Exception {
        Files.write(base.resolve("file"), "test".getBytes());
    }

    @Test
    public void createFile() throws Exception {
        Path file = room.locateAbsent("create");
        assert Files.exists(file) == false;

        room.with($ -> {
            $.file("create");
        });

        assert Files.exists(file) == true;
        assert Files.isRegularFile(file) == true;
    }

    @Test
    public void createDirectory() throws Exception {
        Path dir = room.locateAbsent("create");
        assert Files.exists(dir) == false;

        room.with($ -> {
            $.dir("create");
        });

        assert Files.exists(dir) == true;
        assert Files.isDirectory(dir) == true;
    }

    @Test
    public void createDirectoryNest() throws Exception {
        Path file = room.locateAbsent("a/b/c");
        assert Files.exists(file) == false;

        room.with($ -> {
            $.dir("a", () -> {
                $.dir("b", () -> {
                    $.file("c");
                });
            });
        });

        assert Files.exists(file) == true;
        assert Files.isRegularFile(file) == true;
    }
}
