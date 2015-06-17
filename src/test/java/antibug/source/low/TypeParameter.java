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
 * @version 2014/08/04 10:37:35
 */
public class TypeParameter<A> {

    class TypedClass<X, Y> {
    }

    interface TypedInterface<Z> {
    }

    class Child<O, P> extends TypedClass<String, O> implements TypedInterface<P> {
    }

    class Extend<E extends TypedClass> {
    }

    class ExtendMultiple<E extends TypedClass & TypedInterface & Runnable> {
    }

    class ExtendGeneric<E extends TypedClass<E, String>> {
    }

    TypedClass<?, ?> wildcard;

    TypedInterface<? extends String> wildcardExtends;

    TypedInterface<? super String> wildcardSuper;

    TypedInterface<? extends TypedClass<A, A>> wildcardGenerics;
}
