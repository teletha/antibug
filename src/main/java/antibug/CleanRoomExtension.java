/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @version 2018/03/31 0:45:00
 */
class CleanRoomExtension implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback {

    static CleanRoom room = new CleanRoom();

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        room = new CleanRoom();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        room.beforeEach(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        room.afterEach(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        room.afterAll(context);
    }

}
