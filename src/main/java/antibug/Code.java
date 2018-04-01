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

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import kiss.WiseBiConsumer;
import kiss.WiseConsumer;
import kiss.WiseRunnable;
import net.bytebuddy.jar.asm.Type;

/**
 * @version 2018/04/01 19:52:20
 */
public class Code {

    private static final Map<String, Object> defaults = new HashMap();

    static {
        defaults.put(Type.getInternalName(String.class), "");
    }

    /**
     * Capture error.
     * 
     * @param code An executable code.
     * @return A error result or null.
     */
    public static Throwable catches(WiseRunnable code) {
        try {
            code.run();

            return null;
        } catch (Throwable e) {
            return e;
        }
    }

    public static boolean requireNonNull(WiseRunnable code) {
        return true;
    }

    public static <P> boolean rejectNullArgs(WiseConsumer<P> code) {
        assert catches(() -> code.accept(null)) instanceof NullPointerException : "Code must reject null argument.";
        return true;
    }

    public static <P1, P2> boolean rejectNullArgs(WiseBiConsumer<P1, P2> code) {
        execute(code, params -> {
            code.accept((P1) params.get(0), (P2) params.get(1));
        });
        return true;
    }

    private static void execute(Serializable code, Consumer<List> executor) {
        SerializedLambda lambda = getSerializedLambda(code);
        Type type = Type.getMethodType(lambda.getImplMethodSignature());
        Type[] types = type.getArgumentTypes();
        List defaultValues = new ArrayList();

        for (int i = 0; i < types.length; i++) {
            defaultValues.add(defaults.get(types[i].getInternalName()));
        }

        for (int i = 0; i < types.length; i++) {
            List list = new ArrayList(defaultValues);
            list.set(i, null);

            Throwable error = catches(() -> executor.accept(list));
            assert error instanceof NullPointerException || error instanceof IllegalArgumentException;
        }

    }

    // getting the SerializedLambda
    public static SerializedLambda getSerializedLambda(Object function) {
        if (function == null || !(function instanceof java.io.Serializable)) {
            throw new IllegalArgumentException();
        }

        for (Class<?> clazz = function.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Method replaceMethod = clazz.getDeclaredMethod("writeReplace");
                replaceMethod.setAccessible(true);
                Object serializedForm = replaceMethod.invoke(function);

                if (serializedForm instanceof SerializedLambda) {
                    return (SerializedLambda) serializedForm;
                }
            } catch (NoSuchMethodError e) {
                // fall through the loop and try the next class
            } catch (Throwable t) {
                throw new RuntimeException("Error while extracting serialized lambda", t);
            }
        }

        throw new RuntimeException("writeReplace method not found");
    }
}
