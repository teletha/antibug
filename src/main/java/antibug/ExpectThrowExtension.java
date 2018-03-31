/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.platform.commons.support.AnnotationSupport;

import kiss.I;

/**
 * @version 2018/03/31 22:56:33
 */
class ExpectThrowExtension implements TestExecutionExceptionHandler {

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        context.getElement().flatMap(v -> AnnotationSupport.findAnnotation(v, ExpectThrow.class)).map(v -> v.value()).ifPresent(type -> {
            if (type.isInstance(throwable)) {
                return;
            } else {
                throw I.quiet(throwable);
            }
        });
    }
}
