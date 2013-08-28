/*
 * Copyright (C) 2013 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.powerassert2;

import static antibug.powerassert2.PAssert.*;

import org.junit.Test;

/*

 mv.visitJumpInsn(IFNE, l2);
 mv.visitVarInsn(ALOAD, 1);
 mv.visitIntInsn(BIPUSH, 8);
 mv.visitInsn(ICONST_0);
 mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "insert", "(II)Ljava/lang/StringBuilder;");
 mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
 mv.visitLdcInsn("01234567089");
 mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
 mv.visitJumpInsn(IFNE, l2);
 mv.visitTypeInsn(NEW, "java/lang/AssertionError");
 mv.visitInsn(DUP);
 mv.visitMethodInsn(INVOKESPECIAL, "java/lang/AssertionError", "<init>", "()V");
 mv.visitInsn(ATHROW);
 mv.visitLabel(l2);
 mv.visitLineNumber(52, l2);
 mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {"java/lang/StringBuilder"}, 0, null);
 mv.visitInsn(RETURN);
 Label l3 = new Label();
 mv.visitLabel(l3);
 mv.visitLocalVariable("this", "Ljs/lang/StringBuilderTest;", null, l0, l3, 0);
 mv.visitLocalVariable("builder", "Ljava/lang/StringBuilder;", null, l1, l3, 1);
 mv.visitMaxs(3, 2);
 mv.visitEnd();
 }





 mv.visitJumpInsn(IFNE, l2);
 mv.visitTypeInsn(NEW, "antibug/powerassert/PowerAssertContext");
 mv.visitInsn(DUP);
 mv.visitMethodInsn(INVOKESPECIAL, "antibug/powerassert/PowerAssertContext", "<init>", "()V");
 mv.visitVarInsn(ASTORE, 3);

 mv.visitVarInsn(ALOAD, 1);

 mv.visitVarInsn(ALOAD, 3);
 mv.visitLdcInsn(new Integer(538265811));
 mv.visitLdcInsn(new Integer(1));
 mv.visitVarInsn(ALOAD, 1);
 mv.visitMethodInsn(INVOKEVIRTUAL, "antibug/powerassert/PowerAssertContext", "local", "(IILjava/lang/Object;)V");

 mv.visitIntInsn(BIPUSH, 8);

 mv.visitVarInsn(ALOAD, 3);
 mv.visitIntInsn(BIPUSH, 8);
 mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
 mv.visitMethodInsn(INVOKEVIRTUAL, "antibug/powerassert/PowerAssertContext", "constant", "(Ljava/lang/Object;)V");

 mv.visitInsn(ICONST_0);

 mv.visitVarInsn(ALOAD, 3);
 mv.visitInsn(ICONST_0);
 mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
 mv.visitMethodInsn(INVOKEVIRTUAL, "antibug/powerassert/PowerAssertContext", "constant", "(Ljava/lang/Object;)V");

 mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "insert", "(II)Ljava/lang/StringBuilder;");

 mv.visitInsn(DUP);
 mv.visitVarInsn(ASTORE, 5);
 mv.visitVarInsn(ALOAD, 3);
 mv.visitLdcInsn("insert");
 mv.visitLdcInsn("(II)Ljava/lang/StringBuilder;");
 mv.visitVarInsn(ALOAD, 5);
 mv.visitMethodInsn(INVOKEVIRTUAL, "antibug/powerassert/PowerAssertContext", "method", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V");

 mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");

 mv.visitInsn(DUP);
 mv.visitVarInsn(ASTORE, 7);
 mv.visitVarInsn(ALOAD, 3);
 mv.visitLdcInsn("toString");
 mv.visitLdcInsn("()Ljava/lang/String;");
 mv.visitVarInsn(ALOAD, 7);
 mv.visitMethodInsn(INVOKEVIRTUAL, "antibug/powerassert/PowerAssertContext", "method", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V");

 mv.visitLdcInsn("01234567089");

 mv.visitVarInsn(ALOAD, 3);
 mv.visitLdcInsn("01234567089");
 mv.visitMethodInsn(INVOKEVIRTUAL, "antibug/powerassert/PowerAssertContext", "constant", "(Ljava/lang/Object;)V");

 mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");

 mv.visitInsn(DUP);
 mv.visitVarInsn(ISTORE, 9);
 mv.visitVarInsn(ALOAD, 3);
 mv.visitLdcInsn("equals");
 mv.visitLdcInsn("(Ljava/lang/Object;)Z");
 mv.visitVarInsn(ILOAD, 9);
 mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
 mv.visitMethodInsn(INVOKEVIRTUAL, "antibug/powerassert/PowerAssertContext", "method", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V");

 mv.visitJumpInsn(IFNE, l2);

 mv.visitVarInsn(ALOAD, 3);
 mv.visitInsn(ICONST_0);
 mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
 mv.visitMethodInsn(INVOKEVIRTUAL, "antibug/powerassert/PowerAssertContext", "constant", "(Ljava/lang/Object;)V");
 mv.visitVarInsn(ALOAD, 3);
 mv.visitLdcInsn("!=");
 mv.visitMethodInsn(INVOKEVIRTUAL, "antibug/powerassert/PowerAssertContext", "condition", "(Ljava/lang/String;)V");
 mv.visitTypeInsn(NEW, "antibug/powerassert/PowerAssertionError");
 mv.visitInsn(DUP);
 mv.visitVarInsn(ALOAD, 3);
 mv.visitMethodInsn(INVOKESPECIAL, "antibug/powerassert/PowerAssertionError", "<init>", "(Lantibug/powerassert/PowerAssertContext;)V");
 mv.visitInsn(ATHROW);
 mv.visitLabel(l2);
 mv.visitLineNumber(52, l2);
 mv.visitFrame(Opcodes.F_NEW, 2, new Object[] {"js/lang/StringBuilderTest", "java/lang/StringBuilder"}, 0, new Object[] {});
 mv.visitInsn(RETURN);
 Label l3 = new Label();
 mv.visitLabel(l3);
 mv.visitLocalVariable("this", "Ljs/lang/StringBuilderTest;", null, l0, l3, 0);
 mv.visitLocalVariable("builder", "Ljava/lang/StringBuilder;", null, l1, l3, 1);
 mv.visitMaxs(5, 10);
 mv.visitEnd();
 }

 */
public class Sample {

    @Test
    public void sample() throws Exception {
        String a = "a";
        String b = "b";

        assert $($(a, "a") == $(b, "b"), "a == b");
    }

    @Test
    public void sample2() throws Exception {
        String a = "a";
        String b = "b";

        assert $($(a, "a").startsWith($(b, "b")), "a.startsWith(b)");
    }
}
