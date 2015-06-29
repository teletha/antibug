/*
 * Copyright (C) 2015 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug;

import static antibug.AntiBug.*;
import static antibug.util.UnsafeUtility.*;
import static java.nio.file.StandardCopyOption.*;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
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
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.AssumptionViolatedException;

import kiss.I;

/**
 * <p>
 * The environmental rule for test that depends on file system.
 * </p>
 * 
 * @version 2015/06/23 21:19:51
 */
public class CleanRoom extends Sandbox {

    /** The counter for instances. */
    private static final AtomicInteger counter = new AtomicInteger();

    /** The root bioclean room for tests which are related with file system. */
    private static final Path clean = Paths.get("target/clean-room");

    /** The temporary bioclean room for this instance which are related with file system. */
    public final Path root = clean.resolve(String.valueOf(counter.incrementAndGet()));

    /** The host directory for test. */
    private final Path host;

    /** The clean room monitor. */
    private final Monitor monitor = new Monitor(root);

    /** The all used archives. */
    private final Set<FileSystem> archives = new HashSet();

    /**
     * Create a clean room for the current directory.
     */
    public CleanRoom() {
        this.host = null;
    }

    /**
     * Create a clean room for the directory that the specified path indicates.
     * 
     * @param relativePath A relative location path you want to use.
     */
    public CleanRoom(String relativePath) {
        this(I.locate(relativePath));
    }

    /**
     * Create a clean room for the directory that the specified path indicates.
     * 
     * @param path A relative location path you want to use.
     */
    public CleanRoom(Path path) {
        Path directory = locatePackage(speculateInstantiator());

        if (path != null) {
            if (path.isAbsolute()) {
                directory = path;

                if (Files.notExists(directory)) {
                    try {
                        Files.createDirectories(directory);
                    } catch (IOException e) {
                        throw I.quiet(e);
                    }
                }
            } else {
                directory = directory.resolve(path);
            }
        }

        if (!Files.isDirectory(directory)) {
            directory = directory.getParent();
        }

        this.host = directory;

        // access control
        writable(false, host);
    }

    /**
     * <p>
     * Assume platform encoding.
     * </p>
     * 
     * @param charset Your exepcted charcter encoding.
     */
    public void assume(String charset) {
        assume(Charset.forName(charset));
    }

    /**
     * <p>
     * Assume platform encoding.
     * </p>
     * 
     * @param charset Your exepcted charcter encoding.
     */
    public void assume(Charset charset) {
        if (Charset.defaultCharset() != charset) {
            throw new AssumptionViolatedException("Charset must be " + charset + ".");
        }
    }

    /**
     * <p>
     * Build file tree by {@link FileSystemDSL}.
     * </p>
     * 
     * @param context DSL context.
     */
    public void with(Consumer<FileSystemDSL> context) {
        context.accept(new FileSystemDSL(root));
    }

    /**
     * <p>
     * Locate a present resource file which is assured that the spcified file exists.
     * </p>
     * 
     * @param name A file name.
     * @return A located present file.
     */
    public Path locateFile(String name) {
        return locateFile(name, (Instant) null);
    }

    /**
     * <p>
     * Locate a present resource file which is assured that the spcified file exists.
     * </p>
     * 
     * @param name A file name.
     * @param modified A last modified time.
     * @return A located present file.
     */
    public Path locateFile(String name, Instant modified) {
        return locateFile(name, modified, (Iterable) null);
    }

    /**
     * <p>
     * Locate a present resource file which is assured that the spcified file exists.
     * </p>
     * 
     * @param name A file name.
     * @param lines A text contents.
     * @return A located present file.
     */
    public Path locateFile(String name, String... lines) {
        return locateFile(name, null, lines);
    }

    /**
     * <p>
     * Locate a present resource file which is assured that the spcified file exists.
     * </p>
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
     * <p>
     * Locate a present resource file which is assured that the spcified file exists.
     * </p>
     * 
     * @param name A file name.
     * @param lines A text contents.
     * @return A located present file.
     */
    public Path locateFile(String name, Iterable<? extends CharSequence> lines) {
        return locateFile(name, null, lines);
    }

    /**
     * <p>
     * Locate a present resource file which is assured that the spcified file exists.
     * </p>
     * 
     * @param name A file name.
     * @param modified A last modified time.
     * @param lines A text contents.
     * @return A located present file.
     */
    public Path locateFile(String name, Instant modified, Iterable<? extends CharSequence> lines) {
        try {
            Path file = locate(name, true, true);
            if (lines != null) Files.write(file, lines);
            if (modified != null) Files.setLastModifiedTime(file, FileTime.from(modified));
            return file;
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }

    /**
     * <p>
     * Locate a present resource file which is assured that the spcified file exists as archive.
     * </p>
     * 
     * @param name A file name.
     * @return A located present archive file.
     */
    public Path locateArchive(String name) {
        return locateArchive(name, null);
    }

    /**
     * <p>
     * Locate a present resource file which is assured that the spcified file exists as archive.
     * </p>
     * 
     * @param name A file name.
     * @return A located present archive file.
     */
    public Path locateArchive(String name, Consumer<FileSystemDSL> structure) {
        return locateArchive(locateFile(name), structure);
    }

    /**
     * <p>
     * Locate a present resource file which is assured that the spcified file exists as archive.
     * </p>
     * 
     * @param name A file name.
     * @return A located present archive file.
     */
    public Path locateArchive(Path path) {
        return locateArchive(path, null);
    }

    /**
     * <p>
     * Locate a present resource file which is assured that the spcified file exists as archive.
     * </p>
     * 
     * @param name A file name.
     * @return A located present archive file.
     */
    public Path locateArchive(Path path, Consumer<FileSystemDSL> structure) {
        FileSystemDSL dsl = new FileSystemDSL(path.getParent());
        dsl.zip(path.getFileName().toString(), () -> {
            if (structure != null) structure.accept(dsl);
        });

        try {
            FileSystem system = FileSystems.newFileSystem(path, null);
            archives.add(system);
            return system.getPath("/");
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }

    /**
     * <p>
     * Locate a present resource directory which is assured that the specified directory exists.
     * </p>
     * 
     * @param name A directory name.
     * @return A located present directory.
     */
    public Path locateDirectory(String name) {
        return locate(name, true, false);
    }

    /**
     * <p>
     * Locate a present resource directory which is assured that the specified directory exists.
     * </p>
     * 
     * @param name A directory name.
     * @param children
     * @return A located present directory.
     */
    public Path locateDirectory(String name, Consumer<FileSystemDSL> children) {
        Path directory = locateDirectory(name);
        children.accept(new FileSystemDSL(directory));
        return directory;
    }

    /**
     * <p>
     * Locate an absent resource which is assured that the specified resource doesn't exists.
     * </p>
     * 
     * @param name A resource name.
     * @return A located absent file system resource.
     */
    public Path locateAbsent(String name) {
        return locate(name, false, false);
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
                    throw I.quiet(e);
                }

                // create requested file
                try {
                    Files.createFile(virtual);
                } catch (FileAlreadyExistsException e) {
                    // ignore
                } catch (IOException e) {
                    throw I.quiet(e);
                }
            } else {
                // create requested directory
                try {
                    Files.createDirectories(virtual);
                } catch (IOException e) {
                    throw I.quiet(e);
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
    protected void before(Method method) throws Exception {
        super.before(method);

        // start monitoring clean room
        use(monitor);

        // renew clean room for this test if needed
        if (monitor.modified) {
            // clean up all resources
            sweep(root);

            // copy all resources newly
            copyDirectory(host, root);

            // reset
            monitor.modified = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void after(Method method) {
        for (FileSystem system : archives) {
            try {
                system.close();
            } catch (IOException e) {
                catchError(e);
            }
        }
        archives.clear();
        super.after(method);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void afterClass() {
        // Dispose clean room actually.
        sweep(root);

        // Delete root directory of clean room.
        try {
            Files.deleteIfExists(clean);
        } catch (DirectoryNotEmptyException | NoSuchFileException e) {
            // CleanRoom is used by other testcase, So we can't delete.
        } catch (IOException e) {
            catchError(e);
        }

        // delegate
        super.afterClass();
    }

    /**
     * <p>
     * Helper method to copy all resource in the specified directory.
     * </p>
     * 
     * @param input A input directory.
     * @param output An output directory.
     * @throws IOException I/O error.
     */
    private void copyDirectory(Path input, Path output) throws IOException {
        Files.createDirectories(output);

        if (input != null) {
            for (Path path : Files.newDirectoryStream(input, monitor)) {
                if (Files.isDirectory(path)) {
                    copyDirectory(path, output.resolve(path.getFileName()));
                } else {
                    copyFile(path, output.resolve(path.getFileName()));
                }
            }
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
                catchError(e);
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
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    }

    /**
     * @version 2010/02/13 13:23:22
     */
    private class Monitor extends Security implements Filter<Path> {

        /** The path prefix. */
        private final String prefix;

        /** The flag for file resource modification. */
        private boolean modified = true;

        /**
         * @param root
         */
        public Monitor(Path root) {
            this.prefix = root.toString();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void checkDelete(String file) {
            if (!modified && file.startsWith(prefix)) {
                modified = true;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void checkWrite(FileDescriptor fd) {
            if (!modified && fd.toString().startsWith(prefix)) {
                modified = true;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void checkWrite(String file) {
            if (!modified && file.startsWith(prefix)) {
                modified = true;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean accept(Path path) throws IOException {
            String name = path.getFileName().toString();

            return !name.equals("package-info.html") && !name.endsWith(".class");
        }
    }

    /**
     * @version 2015/06/23 21:20:01
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
                throw I.quiet(e);
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
            return dir(name, () -> {
            });
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
                child.run();
            } catch (IOException e) {
                throw I.quiet(e);
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

            Path temp = I.locateTemporary();
            directories.add(temp);

            try {
                if (Files.notExists(temp)) {
                    Files.createDirectory(temp);
                }
                child.run();
            } catch (IOException e) {
                throw I.quiet(e);
            } finally {
                directories.pollLast();
            }

            try {
                ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(zip), I.$encoding);

                for (Path path : I.walk(temp)) {
                    ZipEntry entry = new ZipEntry(temp.relativize(path).toString().replace(File.separatorChar, '/'));
                    entry.setSize(Files.size(path));
                    entry.setTime(Files.getLastModifiedTime(path).toMillis());
                    out.putNextEntry(entry);

                    InputStream in = Files.newInputStream(path);
                    I.copy(in, out, false);
                    in.close();
                    out.closeEntry();
                }
                out.close();
            } catch (IOException e) {
                throw I.quiet(e);
            }
            return zip;
        }
    }
}
