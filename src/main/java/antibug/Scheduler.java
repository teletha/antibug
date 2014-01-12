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

import java.util.concurrent.ScheduledExecutorService;

/**
 * @version 2014/01/12 22:11:04
 */
interface Scheduler extends ScheduledExecutorService {

    /**
     * <p>
     * Wait all task executions.
     * </p>
     */
    void awaitTasks();
}
