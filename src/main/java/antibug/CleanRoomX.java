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

import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @version 2018/03/31 0:47:32
 */
@ExtendWith(CleanRoomExtension.class)
public interface CleanRoomX {

    default CleanRoom room() {
        return CleanRoomExtension.room;
    }
}
