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
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;

public class StructuredTypeTest extends JavadocTestSupport {

    @ParameterizedTest
    @ValueSource(ints = 0)
    void primitiveInt(int type) {
        assert checkParamType(currentMethod(), "<type>int</type>");
    }

    @ParameterizedTest
    @ValueSource(longs = 0)
    void primitiveLong(long type) {
        assert checkParamType(currentMethod(), "<type>long</type>");
    }

    @ParameterizedTest
    @ValueSource(floats = 0)
    void primitiveFloat(float type) {
        assert checkParamType(currentMethod(), "<type>float</type>");
    }

    @ParameterizedTest
    @ValueSource(doubles = 0)
    void primitiveDouble(double type) {
        assert checkParamType(currentMethod(), "<type>double</type>");
    }

    @ParameterizedTest
    @ValueSource(shorts = 0)
    void primitiveShort(short type) {
        assert checkParamType(currentMethod(), "<type>short</type>");
    }

    @ParameterizedTest
    @ValueSource(bytes = 0)
    void primitiveByte(byte type) {
        assert checkParamType(currentMethod(), "<type>byte</type>");
    }

    @ParameterizedTest
    @ValueSource(chars = '0')
    void primitiveChar(char type) {
        assert checkParamType(currentMethod(), "<type>char</type>");
    }

    @ParameterizedTest
    @ValueSource(booleans = false)
    void primitiveBoolean(boolean type) {
        assert checkParamType(currentMethod(), "<type>boolean</type>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void topLevelType(String type) {
        assert checkParamType(currentMethod(), "<type package='java.lang'>String</type>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void memberType(Thread.State type) {
        assert checkParamType(currentMethod(), "<type package='java.lang' enclosing='Thread'>State</type>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void nestedMemberType(ProcessBuilder.Redirect.Type type) {
        assert checkParamType(currentMethod(), "<type package='java.lang' enclosing='ProcessBuilder.Redirect'>Type</type>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    <T> void variableType(T type) {
        assert checkParamType(currentMethod(), "<type>T</type>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void parameterizedType(List<String> type) {
        assert checkParamType(currentMethod(), "<type package='java.util'>List<parameters><type package='java.lang'>String</type></parameters></type>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void parameterizedTypes(Map<String, Class> type) {
        assert checkParamType(currentMethod(), "<type package='java.util'>Map<parameters><type package='java.lang'>String</type><type package='java.lang'>Class</type></parameters></type>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    <T> void parameterizedTypeByVariable(List<T> type) {
        assert checkParamType(currentMethod(), "<type package='java.util'>List<parameters><type>T</type></parameters></type>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    <T extends CharSequence> void parameterizedTypeByBoundedVariable(List<T> type) {
        assert checkParamType(currentMethod(), "<type package='java.util'>List<parameters><type>T</type></parameters></type>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void parameterizedTypeByWildcardType(List<?> type) {
        assert checkParamType(currentMethod(), "<type package='java.util'>List<parameters><type>?</type></parameters></type>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void parameterizedTypeByLowerBoundedType(List<? extends CharSequence> type) {
        assert checkParamType(currentMethod(), "<type package='java.util'>List<parameters><type bounded='extends' package='java.lang'>CharSequence</type></parameters></type>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    <T> void parameterizedTypeByLowerBoundedVariable(List<? extends T> type) {
        assert checkParamType(currentMethod(), "<type package='java.util'>List<parameters><type bounded='extends'>T</type></parameters></type>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    <T extends CharSequence> void parameterizedTypeByLowerBoundedBoundedVariable(List<? extends T> type) {
        assert checkParamType(currentMethod(), "<type package='java.util'>List<parameters><type bounded='extends'>T</type></parameters></type>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    void parameterizedTypeByUpperBoundedType(List<? super CharSequence> type) {
        assert checkParamType(currentMethod(), "<type package='java.util'>List<parameters><type bounded='super' package='java.lang'>CharSequence</type></parameters></type>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    <T> void parameterizedTypeByUpperBoundedVariable(List<? super T> type) {
        assert checkParamType(currentMethod(), "<type package='java.util'>List<parameters><type bounded='super'>T</type></parameters></type>");
    }

    @ParameterizedTest
    @ArgumentsSource(NullProvider.class)
    <T extends CharSequence> void parameterizedTypeByUpperBoundedBoundedVariable(List<? super T> type) {
        assert checkParamType(currentMethod(), "<type package='java.util'>List<parameters><type bounded='super'>T</type></parameters></type>");
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

    /**
     * Provide only null.
     */
    private static class NullProvider implements ArgumentsProvider {

        /**
         * {@inheritDoc}
         */
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext arg0) throws Exception {
            return Stream.of(Arguments.of(new Object[] {null}));
        }
    }
}
