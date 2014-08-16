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

import static antibug.javadoc.Location.*;

import java.nio.file.AccessMode;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

/**
 * @version 2014/08/16 9:03:23
 */
public class LocationJDKTest {

    @Test
    public void constructorWithoutParameter() {
        assert Location.ofExecutable(ArrayList.class)
                .getJavadocLocation()
                .equals(JDKDocLocation + "java/util/ArrayList.html#ArrayList--");
    }

    @Test
    public void constructorWithParameter() {
        assert Location.ofExecutable(ArrayList.class, int.class)
                .getJavadocLocation()
                .equals(JDKDocLocation + "java/util/ArrayList.html#ArrayList-int-");
    }

    @Test
    public void methodWithoutParameter() {
        assert Location.ofExecutable(ArrayList.class, "clear")
                .getJavadocLocation()
                .equals(JDKDocLocation + "java/util/ArrayList.html#clear--");
    }

    @Test
    public void methodWithPrimitiveParameter() {
        assert Location.ofExecutable(ArrayList.class, "get", int.class)
                .getJavadocLocation()
                .equals(JDKDocLocation + "java/util/ArrayList.html#get-int-");
    }

    @Test
    public void methodWithParameter() {
        assert Location.ofExecutable(ArrayList.class, "remove", Object.class)
                .getJavadocLocation()
                .equals(JDKDocLocation + "java/util/ArrayList.html#remove-java.lang.Object-");
    }

    @Test
    public void methodWithGenericParameter() {
        assert Location.ofExecutable(ArrayList.class, "add", Object.class)
                .getJavadocLocation()
                .equals(JDKDocLocation + "java/util/ArrayList.html#add-E-");
    }

    @Test
    public void methodWithGenericExtendParameter() {
        assert Location.ofExecutable(ArrayList.class, "addAll", Collection.class)
                .getJavadocLocation()
                .equals(JDKDocLocation + "java/util/ArrayList.html#addAll-java.util.Collection-");
    }

    @Test
    public void methodWithGenericSuperParameter() {
        assert Location.ofExecutable(ArrayList.class, "forEach", Consumer.class)
                .getJavadocLocation()
                .equals(JDKDocLocation + "java/util/ArrayList.html#forEach-java.util.function.Consumer-");
    }

    @Test
    public void methodWithParameters() {
        assert Location.ofExecutable(ArrayList.class, "add", int.class, Object.class)
                .getJavadocLocation()
                .equals(JDKDocLocation + "java/util/ArrayList.html#add-int-E-");
    }

    @Test
    public void methodWitArrayParameter() {
        assert Location.ofExecutable(Arrays.class, "hashCode", Object[].class)
                .getJavadocLocation()
                .equals(JDKDocLocation + "java/util/Arrays.html#hashCode-java.lang.Object:A-");
    }

    @Test
    public void methodWitPrimitiveArrayParameter() {
        assert Location.ofExecutable(Arrays.class, "hashCode", byte[].class)
                .getJavadocLocation()
                .equals(JDKDocLocation + "java/util/Arrays.html#hashCode-byte:A-");
    }

    @Test
    public void methodWitGenericArrayParameter() {
        assert Location.ofExecutable(Arrays.class, "stream", Object[].class)
                .getJavadocLocation()
                .equals(JDKDocLocation + "java/util/Arrays.html#stream-T:A-");
    }

    @Test
    public void methodWitVarArgsParameter() {
        assert Location.ofExecutable(Arrays.class, "asList", Object[].class)
                .getJavadocLocation()
                .equals(JDKDocLocation + "java/util/Arrays.html#asList-T...-");
    }

    @Test
    public void field() {
        assert Location.ofField(AbstractList.class, "modCount")
                .getJavadocLocation()
                .equals(JDKDocLocation + "java/util/AbstractList.html#modCount");
    }

    @Test
    public void enumConstant() {
        assert Location.ofField(AccessMode.class, "EXECUTE")
                .getJavadocLocation()
                .equals(JDKDocLocation + "java/nio/file/AccessMode.html#EXECUTE");
    }

    @Test
    public void clazz() {
        assert Location.of(List.class).getJavadocLocation().equals(JDKDocLocation + "java/util/List.html");
    }
}
