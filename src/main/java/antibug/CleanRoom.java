/*
 * Copyright (C) 2021 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug;

import static java.nio.file.FileVisitResult.*;
import static java.nio.file.StandardCopyOption.*;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * The environmental rule for test that depends on file system.
 */
public class CleanRoom implements BeforeEachCallback, AfterEachCallback, AfterAllCallback {

    /** The randomizer. */
    private static final Random RANDOM = new Random();

    /** The counter for instances. */
    private static final AtomicInteger counter = new AtomicInteger();

    /** The root bioclean room for tests which are related with file system. */
    private static final Path clean = Paths.get("target/clean-room");

    /** The temporary bioclean room for this instance which are related with file system. */
    public final Path root = clean.resolve(String.valueOf(counter.incrementAndGet()));

    /** The all used archives. */
    private final Set<FileSystem> archives = new HashSet();

    /**
     * Assume platform encoding.
     * 
     * @param charset Your exepcted charcter encoding.
     */
    public void assume(String charset) {
        assume(Charset.forName(charset));
    }

    /**
     * Assume platform encoding.
     * 
     * @param charset Your exepcted charcter encoding.
     */
    public void assume(Charset charset) {
        Assumptions.assumeTrue(Charset.defaultCharset() == charset, "Platform charset must be " + charset + ".");
    }

    /**
     * Build file tree by {@link FileSystemDSL}.
     * 
     * @param context DSL context.
     */
    public void with(Consumer<FileSystemDSL> context) {
        context.accept(new FileSystemDSL(root));
    }

    /**
     * Locate a present resource file which is assured that the spcified file exists.
     * 
     * @param name A file name.
     * @return A located present file.
     */
    public Path locateFile(String name) {
        return locateFile(name, (Instant) null);
    }

    /**
     * Locate a present resource file which is assured that the spcified file exists.
     * 
     * @param name A file name.
     * @param modified A last modified time.
     * @return A located present file.
     */
    public Path locateFile(String name, Instant modified) {
        return locateFile(name, modified, (Iterable) null);
    }

    /**
     * Locate a present resource file which is assured that the spcified file exists.
     * 
     * @param name A file name.
     * @param lines A text contents.
     * @return A located present file.
     */
    public Path locateFile(String name, String... lines) {
        return locateFile(name, null, lines);
    }

    /**
     * Locate a present resource file which is assured that the spcified file exists.
     * 
     * @param name A file name.
     * @param modified A last modified time.
     * @param lines A text contents.
     * @return A located present file.
     */
    public Path locateFile(String name, Instant modified, String... lines) {
        return locateFile(name, modified, Arrays.asList(lines));
    }

    /**
     * Locate a present resource file which is assured that the spcified file exists.
     * 
     * @param name A file name.
     * @param lines A text contents.
     * @return A located present file.
     */
    public Path locateFile(String name, Iterable<? extends CharSequence> lines) {
        return locateFile(name, null, lines);
    }

    /**
     * Locate a present resource file which is assured that the spcified file exists.
     * 
     * @param name A file name.
     * @param modified A last modified time.
     * @param lines A text contents.
     * @return A located present file.
     */
    public Path locateFile(String name, Instant modified, Iterable<? extends CharSequence> lines) {
        try {
            Path file = locate(name, true, true);
            if (lines != null) {
                Files.writeString(file, StreamSupport.stream(lines.spliterator(), false).collect(Collectors.joining("\r\n")));
            }
            if (modified != null) Files.setLastModifiedTime(file, FileTime.from(modified));
            return file;
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    /**
     * Locate a present resource file which is assured that the spcified file exists as archive.
     * 
     * @param name A file name.
     * @return A located present archive file.
     */
    public Path locateArchive(String name) {
        return locateArchive(name, null);
    }

    /**
     * Locate a present resource file which is assured that the spcified file exists as archive.
     * 
     * @param name A file name.
     * @return A located present archive file.
     */
    public Path locateArchive(String name, Consumer<FileSystemDSL> structure) {
        return locateArchive(locateFile(name), structure);
    }

    /**
     * Locate a present resource file which is assured that the spcified file exists as archive.
     * 
     * @param path A file path.
     * @return A located present archive file.
     */
    public Path locateArchive(Path path) {
        return locateArchive(path, null);
    }

    /**
     * Locate a present resource file which is assured that the spcified file exists as archive.
     * 
     * @param path A file path.
     * @return A located present archive file.
     */
    public Path locateArchive(Path path, Consumer<FileSystemDSL> structure) {
        FileSystemDSL dsl = new FileSystemDSL(path.getParent());
        dsl.zip(path.getFileName().toString(), () -> {
            if (structure != null) structure.accept(dsl);
        });

        try {
            FileSystem system = FileSystems.newFileSystem(path, (ClassLoader) null);
            archives.add(system);
            return system.getPath("/");
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    /**
     * Locate a present resource directory which is assured that the specified directory exists.
     * 
     * @param name A directory name.
     * @return A located present directory.
     */
    public Path locateDirectory(String name) {
        return locateDirectory(name, null, null);
    }

    /**
     * Locate a present resource directory which is assured that the specified directory exists.
     * 
     * @param name A directory name.
     * @return A located present directory.
     */
    public Path locateDirectory(String name, Instant modified) {
        return locateDirectory(name, modified, null);
    }

    /**
     * Locate a present resource directory which is assured that the specified directory exists.
     * 
     * @param name A directory name.
     * @param children
     * @return A located present directory.
     */
    public Path locateDirectory(String name, Consumer<FileSystemDSL> children) {
        return locateDirectory(name, null, children);
    }

    /**
     * Locate a present resource directory which is assured that the specified directory exists.
     * 
     * @param name A directory name.
     * @param children
     * @return A located present directory.
     */
    public Path locateDirectory(String name, Instant modified, Consumer<FileSystemDSL> children) {
        try {
            Path directory = locate(name, true, false);
            if (children != null) children.accept(new FileSystemDSL(directory));
            if (modified != null) Files.setLastModifiedTime(directory, FileTime.from(modified));

            return directory;
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    /**
     * Locate an absent resource which is assured that the specified resource doesn't exists.
     * 
     * @param name A resource name.
     * @return A located absent file system resource.
     */
    public Path locateAbsent(String name) {
        return locate(name, false, false);
    }

    /**
     * Locate a resource with random name.
     * 
     * @return A located file system resource.
     */
    public Path locateRadom() {
        return locate(String.valueOf(RANDOM.nextInt(1000000)));
    }

    /**
     * Locate a resource.
     * 
     * @param name A resource name.
     * @return A located file system resource.
     */
    public Path locate(String name) {
        // null check
        if (name == null) {
            name = "";
        }

        // locate virtual file in the clean room
        return root.resolve(name);
    }

    /**
     * Helper method to locate file in clean room.
     * 
     * @param path
     * @return
     */
    private Path locate(String path, boolean isPresent, boolean isFile) {
        // null check
        if (path == null) {
            path = "";
        }

        // locate virtual file in the clean room
        Path virtual = root.resolve(path);

        // create virtual file if needed
        if (isPresent) {
            if (isFile) {
                // create parent directory
                try {
                    Files.createDirectories(virtual.getParent());
                } catch (IOException e) {
                    throw new IOError(e);
                }

                // create requested file
                try {
                    Files.createFile(virtual);
                } catch (FileAlreadyExistsException e) {
                    // ignore
                } catch (IOException e) {
                    throw new IOError(e);
                }
            } else {
                // create requested directory
                try {
                    Files.createDirectories(virtual);
                } catch (IOException e) {
                    throw new IOError(e);
                }
            }
        }

        // validate file state
        assert Files.exists(virtual) == isPresent;
        assert Files.isRegularFile(virtual) == isFile;

        // API definition
        return virtual;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        // renew clean room for this test if needed
        // clean up all resources
        sweep(root);

        Files.createDirectories(root);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        for (FileSystem system : archives) {
            try {
                system.close();
            } catch (NoSuchFileException e) {
                // if the archive file is deleted during test, we can ignore
            } catch (IOException e) {
                throw new IOError(e);
            }
        }
        archives.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        // Dispose clean room actually.
        sweep(root);

        // Delete root directory of clean room.
        try {
            Files.deleteIfExists(clean);
        } catch (DirectoryNotEmptyException | NoSuchFileException e) {
            // CleanRoom is used by other testcase, So we can't delete.
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    /**
     * <p>
     * Copy a input file to an output file. You can override this method to change file copy
     * behavior.
     * </p>
     * 
     * @param input A input file. (not directory)
     * @param output An output file. (not directory)
     * @throws IOException I/O error.
     */
    protected void copyFile(Path input, Path output) throws IOException {
        Files.copy(input, output, COPY_ATTRIBUTES);
    }

    /**
     * Helper method to delete files.
     * 
     * @param path
     */
    private void sweep(Path path) {
        if (Files.exists(path)) {
            try {
                Files.walkFileTree(path, new Sweeper());
            } catch (IOException e) {
                throw new IOError(e);
            }
        }
    }

    /**
     * @version 2011/03/10 9:35:05
     */
    private static final class Sweeper extends SimpleFileVisitor<Path> {

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            try {
                Files.delete(file);
            } catch (FileSystemException e) {
                // ignore
            }
            return FileVisitResult.CONTINUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            try {
                Files.delete(dir);
            } catch (FileSystemException e) {
                // ignore
            }
            return FileVisitResult.CONTINUE;
        }
    }

    /**
     * @version 2018/03/31 3:05:04
     */
    public static class FileSystemDSL {

        /** The directory stack. */
        private final Deque<Path> directories = new ArrayDeque();

        /**
         * Hide.
         */
        private FileSystemDSL(Path root) {
            directories.add(root);
        }

        /**
         * <p>
         * Locate a present resource file which is assured that the spcified file exists.
         * </p>
         * 
         * @param name A file name.
         * @return A located present file.
         */
        public final Path file(String name) {
            return file(name, Collections.EMPTY_LIST);
        }

        /**
         * <p>
         * Locate a present resource file which is assured that the spcified file exists.
         * </p>
         * 
         * @param name A file name.
         * @return A located present file.
         */
        public final Path file(String name, String... lines) {
            return file(name, Arrays.asList(lines));
        }

        /**
         * <p>
         * Locate a present resource file which is assured that the spcified file exists.
         * </p>
         * 
         * @param name A file name.
         * @return A located present file.
         */
        public final Path file(String name, Iterable<? extends CharSequence> lines) {
            Path file = directories.peekLast().resolve(name);

            try {
                if (Files.notExists(file)) {
                    Files.createFile(file);
                }

                Files.write(file, lines);
            } catch (IOException e) {
                throw new IOError(e);
            }
            return file;
        }

        /**
         * <p>
         * Locate a present resource empty directory which is assured that the specified directory
         * exists.
         * </p>
         * 
         * @param name A directory name.
         * @return A located present directory.
         */
        public final Path dir(String name) {
            return dir(name, null);
        }

        /**
         * <p>
         * Locate a present resource directory which is assured that the specified directory exists.
         * </p>
         * 
         * @param name A directory name.
         * @return A located present directory.
         */
        public final Path dir(String name, Runnable child) {
            Path dir = directories.peekLast().resolve(name);
            directories.add(dir);

            try {
                if (Files.notExists(dir)) {
                    Files.createDirectory(dir);
                }
                if (child != null) child.run();
            } catch (IOException e) {
                throw new IOError(e);
            } finally {
                directories.pollLast();
            }
            return dir;
        }

        /**
         * <p>
         * Locate a present resource zip file which is assured that the specified archive exists.
         * </p>
         * 
         * @param name A archive name.
         * @return A located present zip file.
         */
        public final Path zip(String name, Runnable child) {
            Path zip = directories.peekLast().resolve(name);
            Path temp;

            try {
                temp = Files.createTempDirectory("antibug");
                directories.add(temp);

                if (Files.notExists(temp)) {
                    Files.createDirectory(temp);
                }
                child.run();
            } catch (IOException e) {
                throw new IOError(e);
            } finally {
                directories.pollLast();
            }

            try {
                Archiver archiver = new Archiver(temp, zip);
                Files.walkFileTree(temp, archiver);
                archiver.dispose();
            } catch (IOException e) {
                throw new IOError(e);
            }
            return zip;
        }
    }

    /**
     * @version 2015/07/13 22:01:18
     */
    private static class Archiver extends ZipOutputStream implements FileVisitor<Path> {

        /** The base path. */
        private Path base;

        /**
         * @param output
         */
        private Archiver(Path in, Path destination) throws IOException {
            super(Files.newOutputStream(destination), StandardCharsets.UTF_8);

            base = in;
        }

        /**
         * <p>
         * Create entry path.
         * </p>
         * 
         * @param path
         * @return
         */
        private String name(Path path) {
            return base.relativize(path).toString().replace(File.separatorChar, '/');
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            if (base != dir) {
                try {
                    ZipEntry entry = new ZipEntry(name(dir) + "/");
                    entry.setCreationTime(attrs.creationTime());
                    entry.setLastAccessTime(attrs.lastAccessTime());
                    entry.setLastModifiedTime(attrs.lastModifiedTime());
                    putNextEntry(entry);
                    closeEntry();
                } catch (IOException e) {
                    // ignore
                }
            }
            return CONTINUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            try {
                ZipEntry entry = new ZipEntry(name(file));
                entry.setSize(attrs.size());
                entry.setCreationTime(attrs.creationTime());
                entry.setLastAccessTime(attrs.lastAccessTime());
                entry.setLastModifiedTime(attrs.lastModifiedTime());
                putNextEntry(entry);
                Files.newInputStream(file).transferTo(this);
                closeEntry();
            } catch (IOException e) {
                // ignore
            }

            // API definition
            return CONTINUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            // API definition
            return CONTINUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            // API definition
            return CONTINUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void close() throws IOException {
            // super.close();
        }

        public void dispose() {
            try {
                super.close();
            } catch (IOException e) {
                throw new IOError(e);
            }
        }
    }
}