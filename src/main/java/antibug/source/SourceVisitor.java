/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package antibug.source;

import java.io.Writer;

import kiss.XML;

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EmptyStatementTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.IntersectionTypeTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.LineMap;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.UnionTypeTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.WildcardTree;
import com.sun.tools.javac.tree.Pretty;

/**
 * @version 2014/07/31 11:13:15
 */
public class SourceVisitor<R> extends Pretty implements TreeVisitor<R, XML> {

    /** The line mapper. */
    private final LineMap lines;

    /**
     * @param lines
     */
    public SourceVisitor(Writer writer, LineMap lines) {
        super(writer, false);

        this.lines = lines;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitAnnotatedType(AnnotatedTypeTree arg0, XML arg1) {
        System.out.println("visitAnnotatedType");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitAnnotation(AnnotationTree arg0, XML arg1) {
        System.out.println("visitAnnotation");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitArrayAccess(ArrayAccessTree arg0, XML arg1) {
        System.out.println("visitArrayAccess");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitArrayType(ArrayTypeTree arg0, XML arg1) {
        System.out.println("visitArrayType");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitAssert(AssertTree arg0, XML arg1) {
        System.out.println("visitAssert");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitAssignment(AssignmentTree arg0, XML arg1) {
        System.out.println("visitAssignment");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitBinary(BinaryTree arg0, XML arg1) {
        System.out.println("visitBinary");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitBlock(BlockTree arg0, XML arg1) {
        System.out.println("visitBlock");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitBreak(BreakTree arg0, XML arg1) {
        System.out.println("visitBreak");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitCase(CaseTree arg0, XML arg1) {
        System.out.println("visitCase");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitCatch(CatchTree arg0, XML arg1) {
        System.out.println("visitCatch");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitClass(ClassTree tree, XML xml) {
        System.out.println("visitClass + " + tree.getClass());

        ModifiersTree modifiers = tree.getModifiers();
        xml.child("modifier").text(modifiers.toString());

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitCompilationUnit(CompilationUnitTree tree, XML xml) {
        // comment

        ExpressionTree packageName = tree.getPackageName();

        // package
        xml.child("package").text("package " + packageName.toString() + ";");
        System.out.println(tree.getClass());

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitCompoundAssignment(CompoundAssignmentTree arg0, XML arg1) {
        System.out.println("visitCompoundAssignment");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitConditionalExpression(ConditionalExpressionTree arg0, XML arg1) {
        System.out.println("visitConditionalExpression");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitContinue(ContinueTree arg0, XML arg1) {
        System.out.println("visitContinue");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitDoWhileLoop(DoWhileLoopTree arg0, XML arg1) {
        System.out.println("visitDoWhileLoop");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitEmptyStatement(EmptyStatementTree arg0, XML arg1) {
        System.out.println("visitEmptyStatement");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitEnhancedForLoop(EnhancedForLoopTree arg0, XML arg1) {
        System.out.println("visitEnhancedForLoop");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitErroneous(ErroneousTree arg0, XML arg1) {
        System.out.println("visitErroneous");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitExpressionStatement(ExpressionStatementTree arg0, XML arg1) {
        System.out.println("visitExpressionStatement");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitForLoop(ForLoopTree arg0, XML arg1) {
        System.out.println("visitForLoop");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitIdentifier(IdentifierTree arg0, XML arg1) {
        System.out.println("visitIdentifier");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitIf(IfTree arg0, XML arg1) {
        System.out.println("visitIf");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitImport(ImportTree arg0, XML arg1) {
        System.out.println("visitImport");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitInstanceOf(InstanceOfTree arg0, XML arg1) {
        System.out.println("visitInstanceOf");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitIntersectionType(IntersectionTypeTree arg0, XML arg1) {
        System.out.println("visitIntersectionType");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitLabeledStatement(LabeledStatementTree arg0, XML arg1) {
        System.out.println("visitLabeledStatement");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitLambdaExpression(LambdaExpressionTree arg0, XML arg1) {
        System.out.println("visitLambdaExpression");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitLiteral(LiteralTree arg0, XML arg1) {
        System.out.println("visitLiteral");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitMemberReference(MemberReferenceTree arg0, XML arg1) {
        System.out.println("visitMemberReference");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitMemberSelect(MemberSelectTree arg0, XML arg1) {
        System.out.println("visitMemberSelect");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitMethod(MethodTree arg0, XML arg1) {
        System.out.println("visitMethod");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitMethodInvocation(MethodInvocationTree arg0, XML arg1) {
        System.out.println("visitMethodInvocation");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitModifiers(ModifiersTree arg0, XML arg1) {
        System.out.println("visitModifiers");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitNewArray(NewArrayTree arg0, XML arg1) {
        System.out.println("visitNewArray");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitNewClass(NewClassTree arg0, XML arg1) {
        System.out.println("visitNewClass");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitOther(Tree arg0, XML arg1) {
        System.out.println("visitOther");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitParameterizedType(ParameterizedTypeTree arg0, XML arg1) {
        System.out.println("visitParameterizedType");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitParenthesized(ParenthesizedTree arg0, XML arg1) {
        System.out.println("visitParenthesized");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitPrimitiveType(PrimitiveTypeTree arg0, XML arg1) {
        System.out.println("visitPrimitiveType");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitReturn(ReturnTree arg0, XML arg1) {
        System.out.println("visitReturn");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitSwitch(SwitchTree arg0, XML arg1) {
        System.out.println("visitSwitch");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitSynchronized(SynchronizedTree arg0, XML arg1) {
        System.out.println("visitSynchronized");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitThrow(ThrowTree arg0, XML arg1) {
        System.out.println("visitThrow");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitTry(TryTree arg0, XML arg1) {
        System.out.println("visitTry");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitTypeCast(TypeCastTree arg0, XML arg1) {
        System.out.println("visitTypeCast");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitTypeParameter(TypeParameterTree arg0, XML arg1) {
        System.out.println("visitTypeParameter");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitUnary(UnaryTree arg0, XML arg1) {
        System.out.println("visitUnary");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitUnionType(UnionTypeTree arg0, XML arg1) {
        System.out.println("visitUnionType");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitVariable(VariableTree arg0, XML arg1) {
        System.out.println("visitVariable");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitWhileLoop(WhileLoopTree arg0, XML arg1) {
        System.out.println("visitWhileLoop");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R visitWildcard(WildcardTree arg0, XML arg1) {
        System.out.println("visitWildcard");
        return null;
    }
}
