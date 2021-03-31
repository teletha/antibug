/*
 * Copyright (C) 2021 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package antibug.powerassert;

interface Journal {

    /**
     * Write array index acess.
     */
    void arrayIndex(Object value);

    /**
     * Write create array.
     */
    void arrayNew(String className, Object value);

    /**
     * Write array store acess.
     */
    void arrayStore();

    /**
     * Write conditional expression.
     * 
     * @param contionalExpression
     */
    void condition(String contionalExpression);

    /**
     * Write constant value.
     * 
     * @param constant
     */
    void constant(Object constant);

    /**
     * Write constructor call.
     * 
     * @param name A constructor name.
     * @param description A constructor parameter size.
     * @param value A constructed value.
     */
    void constructor(String name, String description, Object value);

    /**
     * Write local variable value.
     * 
     * @param methodId A method id.
     * @param index A local variable index.
     * @param variable A value.
     */
    void local(int methodId, int index, Object variable);

    /**
     * Write field access.
     * 
     * @param expression
     * @param description
     * @param variable
     * @param methodId A accessing method id.
     */
    void field(String expression, String description, Object variable, int methodId);

    /**
     * Write static field access.
     * 
     * @param className
     * @param fieldName
     * @param description
     * @param variable
     */
    void fieldStatic(String className, String fieldName, String description, Object variable);

    /**
     * Write increment operation.
     * 
     * @param methodId A method id.
     * @param index A local variable index.
     * @param increment A increment value.
     */
    void increment(int methodId, int index, int increment);

    /**
     * Write instanceof operation.
     * 
     * @param className
     */
    void instanceOf(String className);

    /**
     * Write method call.
     * 
     * @param name A method name.
     * @param description A method parameter size.
     * @param value A returned value.
     */
    void method(String name, String description, Object value);

    /**
     * Write static method call.
     * 
     * @param className A class name.
     * @param methodName A method name.
     * @param description A method parameter size.
     * @param value A returned value.
     */
    void methodStatic(String className, String methodName, String description, Object value);

    /**
     * Write negative value operation.
     */
    void negative();

    /**
     * Write operator.
     * 
     * @param operator
     */
    void operator(String operator);

    /**
     * Write lambda method call.
     * 
     * @param methodName A method name.
     * @param description A method parameter size.
     */
    void lambda(String methodName, String description, int referenceSize);

    /**
     * Write method reference call.
     * 
     * @param className A class name.
     * @param methodName A method name.
     */
    void methodReference(String className, String methodName, int referenceSize);
}