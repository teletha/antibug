/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.javadoc;

/**
 * @version 2014/07/29 13:58:36
 */
public class Identifier implements Comparable<Identifier> {

    /** The module name. */
    private final String module;

    /** The type name. */
    private final String type;

    /** The member name. */
    private final String member;

    /**
     * <p>
     * Create identifier from {@link String} expression.
     * </p>
     * 
     * @param id
     */
    public Identifier(String id) {
        if (id == null) {
            id = "";
        }

        String[] parts = id.split(" ");

        switch (parts.length) {
        case 0:
            module = type = member = "";
            break;

        case 1:
            module = parts[0];
            type = member = "";
            break;

        case 2:
            module = parts[0];
            type = parts[1];
            member = "";
            break;

        default:
            module = parts[0];
            type = parts[1];
            member = parts[2];
            break;
        }
    }

    /**
     * @param module
     * @param type
     * @param member
     */
    public Identifier(String module, String type, String member) {
        this.module = module;
        this.type = type == null ? "" : type;
        this.member = type.length() == 0 || member == null ? "" : member;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Identifier o) {
        return toString().compareTo(o.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Identifier && toString().equals(obj.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(module);

        if (type.length() != 0) {
            builder.append(' ').append(type);

            if (member.length() != 0) {
                builder.append(' ').append(member);
            }
        }
        return builder.toString();
    }
}
