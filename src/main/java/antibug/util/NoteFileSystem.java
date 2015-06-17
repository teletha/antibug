/*
 * Copyright (C) 2015 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.util;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Set;

import kiss.I;
import kiss.Manageable;
import kiss.Singleton;

/**
 * @version 2012/02/17 15:47:27
 */
@Manageable(lifestyle = Singleton.class)
class NoteFileSystem extends FileSystem {

    /**
     * {@inheritDoc}
     */
    @Override
    public FileSystemProvider provider() {
        return I.make(NoteFileSystemProvider.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOpen() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadOnly() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSeparator() {
        // If this exception will be thrown, it is bug of this program. So we must rethrow the
        // wrapped error in here.
        throw new Error();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Path> getRootDirectories() {
        return Collections.EMPTY_LIST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<FileStore> getFileStores() {
        return Collections.EMPTY_LIST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> supportedFileAttributeViews() {
        // If this exception will be thrown, it is bug of this program. So we must rethrow the
        // wrapped error in here.
        throw new Error();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Path getPath(String first, String... more) {
        // If this exception will be thrown, it is bug of this program. So we must rethrow the
        // wrapped error in here.
        throw new Error();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {
        // If this exception will be thrown, it is bug of this program. So we must rethrow the
        // wrapped error in here.
        throw new Error();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        // If this exception will be thrown, it is bug of this program. So we must rethrow the
        // wrapped error in here.
        throw new Error();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WatchService newWatchService() throws IOException {
        // If this exception will be thrown, it is bug of this program. So we must rethrow the
        // wrapped error in here.
        throw new Error();
    }
}
