/*
 * Copyright (C) 2015 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source.low;

/**
 * @version 2014/08/02 12:59:02
 */
public interface Interface<T> {

    /**
     * <p>
     * Retrieve name by id.
     * </p>
     * 
     * @param id A target id.
     * @return A target name.
     * @throws Exception If id is invalid.
     */
    String getNameBy(int id) throws Exception;

    /**
     * <p>
     * Retrieve all names.
     * </p>
     * 
     * @return A list of names.
     */
    default String[] getNames() {
        String[] names = new String[100];

        for (int i = 0; i < names.length; i++) {
            try {
                names[i] = getNameBy(i);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return names;
    }

    /**
     * Compute the current mumbers size.
     * 
     * @return A number of members.
     */
    static long size() {
        return 98765432109876543L;
    }
}
