/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.doc;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;

public class StructuredTypeTest extends JavadocTestSupport {

    @ParameterizedTest
    @ValueSource(ints = 0)
    void primitiveInt(int type) {
        assert checkParamType(currentMethod(), "<i class='type'>int</i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void primitiveIntArray(int[] type) {
        assert checkParamType(currentMethod(), "<i class='type' array='fix'>int</i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void primitiveIntVarArray(int... type) {
        assert checkParamType(currentMethod(), "<i class='type' array='var'>int</i>");
    }

    @ParameterizedTest
    @ValueSource(longs = 0)
    void primitiveLong(long type) {
        assert checkParamType(currentMethod(), "<i class='type'>long</i>");
    }

    @ParameterizedTest
    @ValueSource(floats = 0)
    void primitiveFloat(float type) {
        assert checkParamType(currentMethod(), "<i class='type'>float</i>");
    }

    @ParameterizedTest
    @ValueSource(doubles = 0)
    void primitiveDouble(double type) {
        assert checkParamType(currentMethod(), "<i class='type'>double</i>");
    }

    @ParameterizedTest
    @ValueSource(shorts = 0)
    void primitiveShort(short type) {
        assert checkParamType(currentMethod(), "<i class='type'>short</i>");
    }

    @ParameterizedTest
    @ValueSource(bytes = 0)
    void primitiveByte(byte type) {
        assert checkParamType(currentMethod(), "<i class='type'>byte</i>");
    }

    @ParameterizedTest
    @ValueSource(chars = '0')
    void primitiveChar(char type) {
        assert checkParamType(currentMethod(), "<i class='type'>char</i>");
    }

    @ParameterizedTest
    @ValueSource(booleans = false)
    void primitiveBoolean(boolean type) {
        assert checkParamType(currentMethod(), "<i class='type'>boolean</i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void topLevelType(String type) {
        assert checkParamType(currentMethod(), "<i class='type' package='java.lang'>String</i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void memberType(Thread.State type) {
        assert checkParamType(currentMethod(), "<i class='type' package='java.lang' enclosing='Thread'>State</i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void nestedMemberType(ProcessBuilder.Redirect.Type type) {
        assert checkParamType(currentMethod(), "<i class='type' package='java.lang' enclosing='ProcessBuilder.Redirect'>Type</i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void arrayType(String[] type) {
        assert checkParamType(currentMethod(), "<i class='type' package='java.lang' array='fix'>String</i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void vararg(String... type) {
        assert checkParamType(currentMethod(), "<i class='type' package='java.lang' array='var'>String</i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    <T> void arrayGeneric(T[] type) {
        assert checkParamType(currentMethod(), "<i class='type' array='fix'>T</i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    <T> void varargGeneric(T... type) {
        assert checkParamType(currentMethod(), "<i class='type' array='var'>T</i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    <T> void variableType(T type) {
        assert checkParamType(currentMethod(), "<i class='type'>T</i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void parameterizedType(List<String> type) {
        assert checkParamType(currentMethod(), "<i class='type' package='java.util'>List</i><i class='parameters'><i class='type' package='java.lang'>String</i></i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void parameterizedTypes(Map<String, Class> type) {
        assert checkParamType(currentMethod(), "<i class='type' package='java.util'>Map</i><i class='parameters'><i class='type' package='java.lang'>String</i><i class='type' package='java.lang'>Class</i></i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    <T> void parameterizedTypeByVariable(List<T> type) {
        assert checkParamType(currentMethod(), "<i class='type' package='java.util'>List</i><i class='parameters'><i class='type'>T</i></i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    <T extends CharSequence> void parameterizedTypeByBoundedVariable(List<T> type) {
        assert checkParamType(currentMethod(), "<i class='type' package='java.util'>List</i><i class='parameters'><i class='type'>T</i></i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void parameterizedTypeByWildcardType(List<?> type) {
        assert checkParamType(currentMethod(), "<i class='type' package='java.util'>List</i><i class='parameters'><i class='type'>?</i></i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void parameterizedTypeByLowerBoundedType(List<? extends CharSequence> type) {
        assert checkParamType(currentMethod(), "<i class='type' package='java.util'>List</i><i class='parameters'><i class='type'>?</i><i class='extends'><i class='type' package='java.lang'>CharSequence</i></i></i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    <T> void parameterizedTypeByLowerBoundedVariable(List<? extends T> type) {
        assert checkParamType(currentMethod(), "<i class='type' package='java.util'>List</i><i class='parameters'><i class='type'>?</i><i class='extends'><i class='type'>T</i></i></i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    <T extends CharSequence> void parameterizedTypeByLowerBoundedBoundedVariable(List<? extends T> type) {
        assert checkParamType(currentMethod(), "<i class='type' package='java.util'>List</i><i class='parameters'><i class='type'>?</i><i class='extends'><i class='type'>T</i></i></i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void parameterizedTypeByUpperBoundedType(List<? super CharSequence> type) {
        assert checkParamType(currentMethod(), "<i class='type' package='java.util'>List</i><i class='parameters'><i class='type'>?</i><i class='super'><i class='type' package='java.lang'>CharSequence</i></i></i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    <T> void parameterizedTypeByUpperBoundedVariable(List<? super T> type) {
        assert checkParamType(currentMethod(), "<i class='type' package='java.util'>List</i><i class='parameters'><i class='type'>?</i><i class='super'><i class='type'>T</i></i></i>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    <T extends CharSequence> void parameterizedTypeByUpperBoundedBoundedVariable(List<? super T> type) {
        assert checkParamType(currentMethod(), "<i class='type' package='java.util'>List</i><i class='parameters'><i class='type'>?</i><i class='super'><i class='type'>T</i></i></i>");
    }

    /**
     * Shortcut method.
     * 
     * @param info
     * @param expected
     * @return
     */
    private boolean checkParamType(ExecutableInfo info, String expected) {
        return sameXML(info.params.get(0).ⅱ, expected);
    }
}
