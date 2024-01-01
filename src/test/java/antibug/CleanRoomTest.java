/*
 * Copyright (C) 2024 The ANTIBUG Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * @version 2018/03/31 3:13:24
 */
public class CleanRoomTest {

    @RegisterExtension
    CleanRoom room = new CleanRoom();

    @Test
    public void locateFile() {
        Path file = room.locateFile("empty");

        assert Files.exists(file);
        assert Files.isRegularFile(file);
    }

    @Test
    public void locateFileWithTimeStamp() throws Exception {
        Instant now = Instant.now();
        Path file = room.locateFile("empty", now, "Contents");

        assert Files.exists(file);
        assert Files.isRegularFile(file);
        assert Files.getLastModifiedTime(file).toMillis() == now.toEpochMilli();
    }

    @Test
    public void locateArchive() throws Exception {
        Path file = room.locateArchive("test.zip", $ -> {
            $.file("file");
            $.dir("empty");
            $.dir("dir", () -> {
                $.file("child");
            });
        });

        assert Files.exists(file.resolve("file"));
        assert Files.isRegularFile(file.resolve("file"));
        assert Files.exists(file.resolve("empty"));
        assert Files.isDirectory(file.resolve("empty"));
        assert Files.exists(file.resolve("dir"));
        assert Files.isDirectory(file.resolve("dir"));
        assert Files.exists(file.resolve("dir/child"));
        assert Files.isRegularFile(file.resolve("dir/child"));
        assert Files.notExists(file.resolve("not-exists"));
        assert Files.walk(file).count() == 5;
    }

    @Test
    public void locateDirectoryFromAbsent() {
        Path file = room.locateDirectory("absent");

        assert Files.exists(file);
        assert Files.isDirectory(file);
    }

    @Test
    public void locateDirectoryFromPresent() {
        Path path = room.locateDirectory("dir");

        assert Files.exists(path);
        assert Files.isDirectory(path);
    }

    @Test
    public void locateDirectoryWithTimeStamp() throws Exception {
        Instant now = Instant.now();
        Path path = room.locateDirectory("empty", now);

        assert Files.exists(path);
        assert Files.isDirectory(path);
        assert Files.getLastModifiedTime(path).toMillis() == now.toEpochMilli();
    }

    @Test
    public void locateAbsent() {
        Path path = room.locateAbsent("absent.txt");

        assert Files.notExists(path);
        assert !Files.isRegularFile(path);
        assert !Files.isDirectory(path);
    }

    @Test
    public void locatePresentFile() {
        Path file = room.locateAbsent("present.txt");

        // the specified file doesn't exist yet
        assert !Files.exists(file);

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