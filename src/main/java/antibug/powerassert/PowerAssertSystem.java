/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.powerassert;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import antibug.bytecode.Agent;

/**
 * @version 2018/03/31 11:27:36
 */
public class PowerAssertSystem {

    /** The recode for the translated classes. */
    private static final Set<String> translated = new CopyOnWriteArraySet();

    /** The actual translator. */
    private static final Agent agent = new Agent(PowerAssertTranslator.class);

    public static boolean translate(Class clazz) {
        if (translated.add(clazz.getName())) {
            agent.transform(clazz);

            return true;
        } else {
            return false;
        }
    }
}
