/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.doc;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree.Kind;

import kiss.I;

public class TypeResolver {

    /** java.lang pacakage */
    private static final Map<String, String> langs = new HashMap();

    static {
        Class[] classes = {Appendable.class, AutoCloseable.class, CharSequence.class, Cloneable.class, Comparable.class, Iterable.class,
                ProcessHandle.class, ProcessHandle.Info.class, Readable.class, Runnable.class, StackWalker.StackFrame.class,
                System.Logger.class, Thread.UncaughtExceptionHandler.class, Boolean.class, Byte.class, Character.class,
                Character.Subset.class, Character.UnicodeBlock.class, Class.class, ClassLoader.class, ClassValue.class, Compiler.class,
                Double.class, Enum.class, Float.class, InheritableThreadLocal.class, Integer.class, Long.class, Math.class, Module.class,
                ModuleLayer.class, ModuleLayer.Controller.class, Number.class, Object.class, Package.class, Process.class,
                ProcessBuilder.class, ProcessBuilder.Redirect.class, Runtime.class, Runtime.Version.class, SecurityManager.class,
                Short.class, StackTraceElement.class, StackWalker.class, StrictMath.class, String.class, StringBuffer.class,
                StringBuilder.class, System.class, System.LoggerFinder.class, Thread.class, ThreadGroup.class, ThreadLocal.class,
                Throwable.class, Void.class, Character.UnicodeScript.class, ProcessBuilder.Redirect.Type.class, StackWalker.Option.class,
                System.Logger.Level.class, Thread.State.class, ArithmeticException.class, ArrayIndexOutOfBoundsException.class,
                ArrayStoreException.class, ClassCastException.class, ClassNotFoundException.class, CloneNotSupportedException.class,
                EnumConstantNotPresentException.class, Exception.class, IllegalAccessException.class, IllegalArgumentException.class,
                IllegalCallerException.class, IllegalMonitorStateException.class, IllegalStateException.class,
                IllegalThreadStateException.class, IndexOutOfBoundsException.class, InstantiationException.class,
                InterruptedException.class, LayerInstantiationException.class, NegativeArraySizeException.class, NoSuchFieldException.class,
                NoSuchMethodException.class, NullPointerException.class, NumberFormatException.class, ReflectiveOperationException.class,
                RuntimeException.class, SecurityException.class, StringIndexOutOfBoundsException.class, TypeNotPresentException.class,
                UnsupportedOperationException.class, AbstractMethodError.class, AssertionError.class, BootstrapMethodError.class,
                ClassCircularityError.class, Error.class, ExceptionInInitializerError.class, IllegalAccessError.class,
                IncompatibleClassChangeError.class, InstantiationError.class, InternalError.class, LinkageError.class,
                NoClassDefFoundError.class, NoSuchFieldError.class, NoSuchMethodError.class, OutOfMemoryError.class,
                StackOverflowError.class, ThreadDeath.class, UnknownError.class, UnsatisfiedLinkError.class,
                UnsupportedClassVersionError.class, VerifyError.class, VirtualMachineError.class, Deprecated.class,
                FunctionalInterface.class, Override.class, SafeVarargs.class, SuppressWarnings.class};

        for (Class clazz : classes) {
            langs.put(clazz.getSimpleName(), clazz.getCanonicalName());
        }
    }

    private final Map<String, String> imported = new HashMap();

    public void register(Element e) {
        I.signal(DocTool.DocUtils.getPath(e))
                .take(tree -> tree.getKind() == Kind.COMPILATION_UNIT)
                .as(CompilationUnitTree.class)
                .flatIterable(CompilationUnitTree::getImports)
                .to(tree -> {
                    if (tree.isStatic()) {

                    } else {
                        String fqcn = tree.getQualifiedIdentifier().toString();
                        imported.put(fqcn.substring(fqcn.lastIndexOf(".") + 1), fqcn);
                    }
                });
    }

    /**
     * @param className
     */
    public String resolve(String className) {
        String fqcn = imported.get(className);
        if (fqcn == null) fqcn = langs.get(className);

        return fqcn == null ? className : fqcn;
    }
}
