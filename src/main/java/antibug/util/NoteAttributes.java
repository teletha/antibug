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

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 * @version 2012/02/17 15:54:09
 */
class NoteAttributes implements BasicFileAttributes {

    /** The actual memo. */
    private final Note memo;

    /**
     * @param memo
     */
    NoteAttributes(Note memo) {
        this.memo = memo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileTime lastModifiedTime() {
        return FileTime.fromMillis(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileTime lastAccessTime() {
        return FileTime.fromMillis(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileTime creationTime() {
        return FileTime.fromMillis(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRegularFile() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDirectory() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSymbolicLink() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOther() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long size() {
        return memo.length();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object fileKey() {
        // If this exception will be thrown, it is bug of this program. So we must rethrow the
        // wrapped error in here.
        throw new Error();
    }
}
