/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.bytecode;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @version 2018/03/27 15:49:03
 */
public class ZZZ {

    public static void main(String[] args) throws Exception {
        ByteBuddyAgent.install();
        new ByteBuddy().redefine(Foo.class)
                .method(ElementMatchers.named("sayHelloFoo"))
                .intercept(FixedValue.value("Hello Foo Redefined"))
                .make()
                .load(Foo.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        Foo f = new Foo();

        System.out.println(f.sayHelloFoo());
    }

    private static class Foo {

        public String sayHelloFoo() {
            return "OK";
        }
    }
}
