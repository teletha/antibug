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

import java.io.File;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import kiss.I;
import kiss.WiseBiConsumer;
import kiss.WiseConsumer;
import kiss.WiseRunnable;
import kiss.WiseTriConsumer;
import net.bytebuddy.jar.asm.Type;

/**
 * @version 2018/04/01 19:52:20
 */
public class Code {

    private static final Map<String, Object> defaults = new HashMap();

    static {
        defaults.put(Type.getInternalName(int.class), 0);
        defaults.put(Type.getInternalName(long.class), 0L);
        defaults.put(Type.getInternalName(float.class), 0F);
        defaults.put(Type.getInternalName(double.class), 0D);
        defaults.put(Type.getInternalName(byte.class), 0);
        defaults.put(Type.getInternalName(char.class), ' ');
        defaults.put(Type.getInternalName(boolean.class), false);
        defaults.put(Type.getInternalName(BigInteger.class), BigInteger.ZERO);
        defaults.put(Type.getInternalName(BigDecimal.class), BigDecimal.ZERO);
        defaults.put(Type.getInternalName(String.class), "");
        defaults.put(Type.getInternalName(Path.class), Paths.get(""));
        defaults.put(Type.getInternalName(File.class), new File(""));
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

    /**
     * Capture {@link NullPointerException}.
     * 
     * @param code An executable code.
     * @return A result.
     */
    public static boolean catcheNPE(WiseRunnable code) {
        assert catches(code) instanceof NullPointerException;

        return true;
    }

    /**
     * Check <code>null</code> paramter.
     * 
     * @param code
     * @return
     */
    public static <P> boolean rejectNullArgs(WiseConsumer<P> code) {
        return rejectNullArgs(code, params -> code.accept((P) params.get(0)));
    }

    /**
     * Check <code>null</code> paramter.
     * 
     * @param code
     * @return
     */
    public static <P1, P2> boolean rejectNullArgs(WiseBiConsumer<P1, P2> code) {
        return rejectNullArgs(code, params -> code.accept((P1) params.get(0), (P2) params.get(1)));
    }

    /**
     * Check <code>null</code> paramter.
     * 
     * @param code
     * @return
     */
    public static <P1, P2, P3> boolean rejectNullArgs(WiseTriConsumer<P1, P2, P3> code) {
        return rejectNullArgs(code, params -> code.accept((P1) params.get(0), (P2) params.get(1), (P3) params.get(2)));
    }

    private static boolean rejectNullArgs(Serializable code, Consumer<List> executor) {
        SerializedLambda lambda = lambda(code);
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
        return true;
    }

    /**
     * Compute {@link SerializedLambda}.
     * 
     * @param function
     * @return
     */
    private static SerializedLambda lambda(Serializable function) {
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
            } catch (Throwable e) {
                throw I.quiet(e);
            }
        }
        throw new RuntimeException("writeReplace method not found");
    }
}
