/*
 * Copyright (C) 2015 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.powerassert;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import kiss.Extensible;
import kiss.I;
import kiss.Manageable;
import kiss.Singleton;

/**
 * <p>
 * Don't use automatic {@link Extensible} mechanism because {@link I#load(Class)} against test
 * classes directory will may cause some unpredictable error for tests. So we adopts special
 * extended mechanism by {@link #register(Class)}.
 * </p>
 * 
 * @version 2012/02/15 12:32:18
 */
public abstract class PowerAssertRenderer<T> {

    /** The registered renderers. */
    private static final Map<Type, Class<? extends PowerAssertRenderer>> renderers = new HashMap();

    static {
        register(CharSequenceRenderer.class);
        register(EnumRenderer.class);
        register(CharacterRnederer.class);
        register(ClassRnederer.class);
        register(IntArrayRnederer.class);
        register(LongArrayRnederer.class);
        register(FloatArrayRnederer.class);
        register(DoubleArrayRnederer.class);
        register(BooleanArrayRnederer.class);
        register(CharArrayRnederer.class);
        register(ByteArrayRnederer.class);
        register(ShortArrayRnederer.class);
        register(ObjectArrayRnederer.class);

        // Don't register ObjectRenderer because Object type must be evaluated at last.
        // register(ObjectRnederer.class);
    }

    /**
     * <p>
     * Register the object renderer.
     * </p>
     * 
     * @param renderer
     */
    public static final void register(Class<? extends PowerAssertRenderer> renderer) {
        Type[] params = I.collectParameters(renderer, PowerAssertRenderer.class);

        if (params.length == 1) {
            renderers.put(params[0], renderer);
        }
    }

    /**
     * <p>
     * Format the target object.
     * </p>
     * 
     * @param object A object to format.
     * @return A formatted message.
     */
    public static final String format(Object object) {
        for (Class type : I.collectTypes(object.getClass())) {
            Class<? extends PowerAssertRenderer> renderer = renderers.get(type);

            if (renderer != null) {
                return I.make(renderer).render(object);
            }
        }
        return I.make(ObjectRnederer.class).render(object);
    }

    /**
     * <p>
     * Render the specified value for human.
     * </p>
     * 
     * @param value A target.
     * @return A human-readable description.
     */
    protected abstract String render(T value);

    /**
     * @version 2012/02/15 12:04:49
     */
    private static final class CharSequenceRenderer extends PowerAssertRenderer<CharSequence> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected String render(CharSequence value) {
            return "\"" + value + "\"";
        }
    }

    /**
     * @version 2012/02/15 12:06:00
     */
    private static final class EnumRenderer extends PowerAssertRenderer<Enum> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected String render(Enum value) {
            return value.getDeclaringClass().getSimpleName() + '.' + value.name();
        }
    }

    /**
     * @version 2012/02/15 12:06:51
     */
    private static final class CharacterRnederer extends PowerAssertRenderer<Character> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected String render(Character value) {
            return "'" + value + "'";
        }
    }

    /**
     * @version 2012/02/15 12:06:51
     */
    private static final class ClassRnederer extends PowerAssertRenderer<Class> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected String render(Class value) {
            return value.getSimpleName() + ".class";
        }
    }

    /**
     * @version 2012/02/15 12:06:51
     */
    private static final class IntArrayRnederer extends PowerAssertRenderer<int[]> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected String render(int[] value) {
            return Arrays.toString(value);
        }
    }

    /**
     * @version 2012/02/15 12:06:51
     */
    private static final class LongArrayRnederer extends PowerAssertRenderer<long[]> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected String render(long[] value) {
            return Arrays.toString(value);
        }
    }

    /**
     * @version 2012/02/15 12:06:51
     */
    private static final class FloatArrayRnederer extends PowerAssertRenderer<float[]> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected String render(float[] value) {
            return Arrays.toString(value);
        }
    }

    /**
     * @version 2012/02/15 12:06:51
     */
    private static final class DoubleArrayRnederer extends PowerAssertRenderer<double[]> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected String render(double[] value) {
            return Arrays.toString(value);
        }
    }

    /**
     * @version 2012/02/15 12:06:51
     */
    private static final class BooleanArrayRnederer extends PowerAssertRenderer<boolean[]> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected String render(boolean[] value) {
            return Arrays.toString(value);
        }
    }

    /**
     * @version 2012/02/15 12:06:51
     */
    private static final class CharArrayRnederer extends PowerAssertRenderer<char[]> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected String render(char[] value) {
            return Arrays.toString(value);
        }
    }

    /**
     * @version 2012/02/15 12:06:51
     */
    private static final class ByteArrayRnederer extends PowerAssertRenderer<byte[]> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected String render(byte[] value) {
            return Arrays.toString(value);
        }
    }

    /**
     * @version 2012/02/15 12:06:51
     */
    private static final class ShortArrayRnederer extends PowerAssertRenderer<short[]> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected String render(short[] value) {
            return Arrays.toString(value);
        }
    }

    /**
     * @version 2012/02/15 12:06:51
     */
    private static final class ObjectArrayRnederer extends PowerAssertRenderer<Object[]> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected String render(Object[] value) {
            return Arrays.toString(value);
        }
    }

    /**
     * @version 2012/02/15 12:06:51
     */
    @Manageable(lifestyle = Singleton.class)
    private static final class ObjectRnederer extends PowerAssertRenderer<Object> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected String render(Object value) {
            return value.toString();
        }
    }
}
