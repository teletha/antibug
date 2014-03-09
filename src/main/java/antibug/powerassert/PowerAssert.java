/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.powerassert;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import antibug.PrivateModule;
import antibug.bytecode.Agent;

/**
 * <p>
 * Enhance assertion message in testcase.
 * </p>
 * <p>
 * {@link PowerAssert} is declared implicitly, so you don't use this class except for testing
 * {@link PowerAssert} itself. Implicit declaration is achived by class loading replacement. You
 * have to add this jar file in classpath before the JUnit jar file.
 * </p>
 * <p>
 * Using {@link PowerAssertOff} annotation, you can stop {@link PowerAssert}'s functionality for
 * each test classes or testcase methods.
 * </p>
 * 
 * @version 2012/01/19 11:50:38
 */
public class PowerAssert implements TestRule {

    /** The recode for the translated classes. */
    private static final Set<String> translated = new CopyOnWriteArraySet();

    /** The actual translator. */
    private static final Agent agent = new Agent(PowerAssertTranslator.class);

    /** The self tester. (use Object type to cut the reference to PowerAssertTester) */
    private final Object tester;

    /** The validate method of tester. (use Method type to cut the reference to PowerAssertTester) */
    private final Method validate;

    /**
     * Assertion Utility.
     */
    public PowerAssert() {
        this(null);
    }

    /**
     * Test for {@link PowerAssert}. Parameter must implements {@link PowerAssertTester}.
     */
    PowerAssert(Object tester) {
        this.tester = tester;

        if (tester == null) {
            this.validate = null;
        } else {
            try {
                this.validate = tester.getClass().getDeclaredMethod("validate", PowerAssertContext.class);
            } catch (Exception e) {
                // If this exception will be thrown, it is bug of this program. So we must rethrow
                // the wrapped error in here.
                throw new Error(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Statement apply(final Statement statement, final Description description) {
        return new Statement() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void evaluate() throws Throwable {
                try {
                    statement.evaluate();
                } catch (PowerAssertOffError error) {
                    throw error.getCause();
                } catch (PowerAssertionError error) {
                    if (tester != null) {
                        validate.invoke(tester, error.context); // for self test
                    } else {
                        throw error; // rethrow for unit test
                    }
                } catch (AssertionError error) {
                    // should we print this error message in detal?
                    if (description.getAnnotation(PowerAssertOff.class) == null && !description.getTestClass()
                            .isAnnotationPresent(PowerAssertOff.class)) {
                        // find the class which rises assertion error
                        Class clazz = PrivateModule.forName(error.getStackTrace()[0].getClassName());

                        // translate assertion code only once
                        if (translated.add(clazz.getName())) {
                            agent.transform(clazz);

                            evaluate(); // retry testcase
                            return;
                        }
                    }
                    throw error; // rethrow for unit test
                }
            }
        };
    }
}
