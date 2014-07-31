/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.javadoc.info;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.junit.Rule;
import org.junit.Test;

import antibug.javadoc.JavadocParser;
import antibug.javadoc.info.annotation.Primitive;
import antibug.javadoc.info.annotation.SourceAnnotationValue;
import antibug.javadoc.info.annotation.SourceMarker;
import antibug.javadoc.info.annotation.SourceValue;

/**
 * @version 2014/07/26 21:55:11
 */
public class MethodInfoTest {

    @Rule
    public static final JavadocParser parser = new JavadocParser();

    /**
     * Text
     */
    @Test
    public void method() {
        MethodInfo info = parser.getMethod();
        assert info.id.memberName.equals("method()");
    }

    @Test
    public void parameter() {
        Method method = parser.findMethod("parames", String.class, int.class);
        MethodInfo info = parser.getMethod(method);
        assert parser.equals(info, method);

        ParamInfo param = info.params.get(0);
        assert param.name.equals("name");
        assert parser.equals(param.type, String.class);
        assert param.annotation.size() == 0;

        param = info.params.get(1);
        assert param.name.equals("age");
        assert parser.equals(param.type, int.class);
        assert param.annotation.size() == 0;
    }

    public void parames(String name, int age) {
    }

    @Test
    public void parameterAnnotation() {
        Method method = parser.findMethod("paramAnnotation", Runnable.class);
        MethodInfo info = parser.getMethod(method);
        assert parser.equals(info, method);

        ParamInfo param = info.params.get(0);
        assert param.name.equals("task");
        assert parser.equals(param.type, Runnable.class);
        assert param.annotation.size() == 1;

        AnnotationInfo anno = param.annotation.get(0);
        assert parser.equals(anno.type, SourceMarker.class);
    }

    public void paramAnnotation(@SourceMarker Runnable task) {
    }

    @Test
    public void parameterAnnotations() {
        Method method = parser.findMethod("paramAnnotations", Callable.class);
        MethodInfo info = parser.getMethod(method);
        assert parser.equals(info, method);

        ParamInfo param = info.params.get(0);
        assert param.name.equals("task");
        assert parser.equals(param.type, Callable.class);
        assert param.annotation.size() == 1;

        AnnotationInfo anno = param.annotation.get(0);
        assert parser.equals(anno.type, SourceValue.class);
    }

    public void paramAnnotations(@SourceValue("test") Callable task) {
    }

    @Test
    @SourceAnnotationValue({@Primitive(intValue = 10, booleanValue = true),
            @Primitive(intValue = 20, booleanValue = false),
            @Primitive(intValue = 30, longValue = -100, booleanValue = false)})
    public void methodAnnotation() {
        // <Anno>
        // _<type href="~" >@SourceAnnotationValue</type>
        // _<values>
        // __<
        // _</value>
    }
}
