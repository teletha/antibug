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

import static com.sun.tools.javac.tree.JCTree.Tag.*;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;

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
import com.sun.source.tree.StatementTree;
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
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;

/**
 * @version 2014/07/31 21:39:41
 */
class SourceTreeVisitor implements TreeVisitor<SourceXML, SourceXML> {

    /** Stop counting of precedence level. */
    private static final int STOP = 100;

    /** Start counting of precedence level. */
    private static final int START = 0;

    /** The root precedence level. */
    private static final int ROOT = 1;

    /** The root xml. */
    private final SourceXML root;

    /** The latest line xml. */
    private SourceXML latestLine;

    /** The actual line mapper. */
    private final SourceMapper mapper;

    /** The current AST line number. */
    private int logicalLine = 1;

    /** The current AST indent size. */
    private int indentLevel = 0;

    /** The indent pattern. */
    private String indent = "    ";

    /** The current precedence level. */
    private int precedenceLevel = START;

    /**
     * @param mapper
     */
    SourceTreeVisitor(SourceXML xml, SourceMapper mapper) {
        this.root = xml;
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitAnnotatedType(AnnotatedTypeTree type, SourceXML context) {
        traceLine(type, context);

        for (AnnotationTree annotation : type.getAnnotations()) {
            annotation.accept(this, context).space();
        }
        type.getUnderlyingType().accept(this, context);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitAnnotation(AnnotationTree annotation, SourceXML context) {
        context = traceLine(annotation, context);

        SourceXML anno = context.child("annotation").text("@" + annotation.getAnnotationType());
        anno.join("(", ")", annotation.getArguments(), tree -> tree.accept(this, anno));

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitArrayAccess(ArrayAccessTree arg0, SourceXML context) {
        System.out.println("visitArrayAccess");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitArrayType(ArrayTypeTree array, SourceXML context) {
        traceLine(array, context);

        array.getType().accept(this, context).text("[]");

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitAssert(AssertTree arg0, SourceXML context) {
        System.out.println("visitAssert");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitAssignment(AssignmentTree assign, SourceXML context) {
        context.variable(assign.getVariable().toString()).space().text("=").space();
        assign.getExpression().accept(this, context);
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitBinary(BinaryTree binary, SourceXML context) {
        traceLine(binary, context);

        String operator = "";

        switch (binary.getKind()) {
        case LESS_THAN:
            operator = "<";
            break;

        case LESS_THAN_EQUAL:
            operator = "<=";
            break;

        case GREATER_THAN:
            operator = ">";
            break;

        case GREATER_THAN_EQUAL:
            operator = ">=";
            break;

        case EQUAL_TO:
            operator = "==";
            break;

        case NOT_EQUAL_TO:
            operator = "!=";
            break;

        case AND:
            operator = "&&";
            break;

        case OR:
            operator = "||";
            break;

        default:
            throw new Error(binary.getKind().toString());
        }

        binary.getLeftOperand().accept(this, context);
        context.space().text(operator).space();
        binary.getRightOperand().accept(this, context);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitBlock(BlockTree block, SourceXML context) {
        context = traceLine(block, context);

        if (block.isStatic()) {
            context.reserved("static");
        }
        context.space().text("{");
        indentLevel++;

        for (StatementTree tree : block.getStatements()) {
            context = tree.accept(this, context);
        }
        indentLevel--;
        startNewLine().text("}");

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitBreak(BreakTree arg0, SourceXML context) {
        System.out.println("visitCase");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitCase(CaseTree arg0, SourceXML context) {
        System.out.println("visitCase");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitCatch(CatchTree arg0, SourceXML context) {
        System.out.println("visitCatch");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitClass(ClassTree clazz, SourceXML context) {
        // ===========================================
        // Annotations and Modifiers
        // ===========================================
        context = clazz.getModifiers().accept(this, context);

        // ===========================================
        // Type Declaration
        // ===========================================
        switch (clazz.getKind()) {
        case CLASS:
            latestLine.reserved("class").space();
            break;

        case INTERFACE:
            latestLine.reserved("interface").space();
            break;

        case ANNOTATION_TYPE:
            latestLine.reserved("@interface").space();
            break;

        case ENUM:
            latestLine.reserved("enum").space();
            break;

        default:
            throw new Error(clazz.getKind().toString());
        }
        latestLine.type(clazz.getSimpleName().toString());

        // ===========================================
        // Type Parameters
        // ===========================================
        writeTypeParameter(clazz.getTypeParameters(), latestLine);

        // ===========================================
        // Extends
        // ===========================================
        Tree extend = clazz.getExtendsClause();

        if (extend != null) {
            latestLine.space().reserved("extends").space();
            extend.accept(this, latestLine);
        }

        // ===========================================
        // Implements
        // ===========================================
        List<? extends Tree> implement = clazz.getImplementsClause();

        if (!implement.isEmpty()) {
            latestLine.space().reserved("implements").space().join(implement, tree -> tree.accept(this, latestLine));
        }

        latestLine.space().text("{");
        indentLevel++;

        // ===========================================
        // Members
        // ===========================================
        for (Tree tree : clazz.getMembers()) {
            tree.accept(this, context);
        }

        indentLevel--;
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitCompilationUnit(CompilationUnitTree unit, SourceXML context) {
        context = traceLine(unit, context);

        context.reserved("package").space().text(unit.getPackageName()).semiColon().line();

        for (ImportTree tree : unit.getImports()) {
            context = visitImport(tree, context);
        }

        for (Tree tree : unit.getTypeDecls()) {
            tree.accept(this, context);
        }
        return startNewLine();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitCompoundAssignment(CompoundAssignmentTree arg0, SourceXML context) {
        System.out.println("visitCompoundAssignment");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitConditionalExpression(ConditionalExpressionTree arg0, SourceXML context) {
        System.out.println("visitConditionalExpression");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitContinue(ContinueTree arg0, SourceXML context) {
        System.out.println("visitContinue");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitDoWhileLoop(DoWhileLoopTree arg0, SourceXML context) {
        System.out.println("visitDoWhileLoop");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitEmptyStatement(EmptyStatementTree arg0, SourceXML context) {
        System.out.println("visitEmptyStatement");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitEnhancedForLoop(EnhancedForLoopTree loop, SourceXML context) {
        context = traceLine(loop, context);

        latestLine.reserved("for").space().text("(");
        loop.getVariable().accept(this, latestLine);
        latestLine.space().text(":").space();
        loop.getExpression().accept(this, latestLine);
        latestLine.text(")");
        loop.getStatement().accept(this, latestLine);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitErroneous(ErroneousTree arg0, SourceXML context) {
        System.out.println("visitErroneous");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitExpressionStatement(ExpressionStatementTree expression, SourceXML context) {
        context = traceLine(expression, context);

        expression.getExpression().accept(this, context);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitForLoop(ForLoopTree loop, SourceXML context) {
        precedenceLevel = STOP;
        context = traceLine(loop, context);

        context.reserved("for").space().text("(").join(loop.getInitializer(), this).semiColon().space();
        loop.getCondition().accept(this, context).semiColon().space().join(loop.getUpdate(), this).text(")");

        precedenceLevel = START;
        loop.getStatement().accept(this, context);

        // if (tree.init.nonEmpty()) {
        // if (tree.init.head.hasTag(VARDEF)) {
        // printExpr(tree.init.head);
        // for (List<JCStatement> l = tree.init.tail; l.nonEmpty(); l = l.tail) {
        // JCVariableDecl vdef = (JCVariableDecl) l.head;
        // print(", " + vdef.name + " = ");
        // printExpr(vdef.init);
        // }
        // } else {
        // printExprs(tree.init);
        // }
        // }
        // print("; ");
        // if (tree.cond != null) printExpr(tree.cond);
        // print("; ");
        // printExprs(tree.step);
        // print(") ");
        // printStat(tree.body);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitIdentifier(IdentifierTree identifier, SourceXML context) {
        context.type(identifier.getName().toString());

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitIf(IfTree tree, SourceXML context) {
        precedenceLevel++;
        traceLine(tree, context);

        // Condition
        latestLine.reserved("if").space();
        tree.getCondition().accept(this, latestLine);

        // Then
        tree.getThenStatement().accept(this, latestLine);

        StatementTree elseStatement = tree.getElseStatement();

        if (elseStatement != null) {
            latestLine.space().reserved("else").space();
            elseStatement.accept(this, latestLine);
        }

        precedenceLevel--;
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitImport(ImportTree tree, SourceXML context) {
        context = traceLine(tree, context);

        latestLine.reserved("import").space();
        if (tree.isStatic()) latestLine.reserved("static").space();
        latestLine.text(tree.getQualifiedIdentifier()).semiColon().line();
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitInstanceOf(InstanceOfTree arg0, SourceXML context) {
        System.out.println("visitInstanceOf");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitIntersectionType(IntersectionTypeTree arg0, SourceXML context) {
        System.out.println("visitIntersectionType");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitLabeledStatement(LabeledStatementTree arg0, SourceXML context) {
        System.out.println("visitLabeledStatement");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitLambdaExpression(LambdaExpressionTree arg0, SourceXML context) {
        System.out.println("visitLambdaExpression");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitLiteral(LiteralTree literal, SourceXML context) {
        traceLine(literal, context);

        Object value = literal.getValue();

        switch (literal.getKind()) {
        case STRING_LITERAL:
            context.string(value.toString());
            break;

        case INT_LITERAL:
            context.number(value.toString());
            break;

        case LONG_LITERAL:
            context.number(value.toString() + "L");
            break;

        case NULL_LITERAL:
        case BOOLEAN_LITERAL:
            context.reserved(String.valueOf(value));
            break;

        default:
            throw new Error(literal.getKind().toString());
        }

        // try {
        // switch (tree.typetag) {
        // case INT:
        // print(tree.value.toString());
        // break;
        // case LONG:
        // print(tree.value + "L");
        // break;
        // case FLOAT:
        // print(tree.value + "F");
        // break;
        // case DOUBLE:
        // print(tree.value.toString());
        // break;
        // case CHAR:
        // print("\'" + Convert.quote(String.valueOf((char) ((Number) tree.value).intValue())) +
        // "\'");
        // break;
        // case BOOLEAN:
        // print(((Number) tree.value).intValue() == 1 ? "true" : "false");
        // break;
        // case BOT:
        // print("null");
        // break;
        // default:
        // print("\"" + Convert.quote(tree.value.toString()) + "\"");
        // break;
        // }
        // } catch (IOException e) {
        // throw new UncheckedIOException(e);
        // }
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitMemberReference(MemberReferenceTree arg0, SourceXML context) {
        System.out.println("visitMemberReference");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitMemberSelect(MemberSelectTree select, SourceXML context) {
        context = traceLine(select, context);

        select.getExpression().accept(this, context);
        context.text(".").memberAccess(select.getIdentifier().toString());

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitMethod(MethodTree method, SourceXML context) {
        traceLine(method, context);

        // ===========================================
        // Annotations and Modifiers
        // ===========================================
        visitModifiers(method.getModifiers(), latestLine);

        // ===========================================
        // Type Parameter Declarations
        // ===========================================
        writeTypeParameter(method.getTypeParameters(), latestLine);

        // ===========================================
        // Return Type
        // ===========================================
        method.getReturnType().accept(this, latestLine).space();

        // ===========================================
        // Method Name And Parameters
        // ===========================================
        latestLine.declaraMember(method.getName().toString()).text("(");
        latestLine.join(method.getParameters(), tree -> tree.accept(this, latestLine));
        latestLine.text(")");

        // ===========================================
        // Throws
        // ===========================================
        List<? extends ExpressionTree> exceptions = method.getThrows();

        if (!exceptions.isEmpty()) {
            latestLine.space().reserved("throws").space().join(exceptions, item -> item.accept(this, latestLine));
        }

        // ===========================================
        // Default Value for Annotation
        // ===========================================
        Tree defaultValue = method.getDefaultValue();

        if (defaultValue != null) {
            latestLine.space().reserved("default").space();
            defaultValue.accept(this, latestLine);
        }

        // ===========================================
        // Method Body
        // ===========================================
        BlockTree body = method.getBody();

        if (body == null) {
            // abstract
            latestLine.semiColon();
        } else {
            // concreat
            body.accept(this, latestLine);
        }

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitMethodInvocation(MethodInvocationTree invoke, SourceXML context) {
        precedenceLevel++;
        traceLine(invoke, context);

        JCMethodInvocation tree = (JCMethodInvocation) invoke;
        List<? extends Tree> types = invoke.getTypeArguments();

        if (!types.isEmpty()) {
            if (tree.meth.hasTag(SELECT)) {
                JCFieldAccess left = (JCFieldAccess) tree.meth;
                context = left.selected.accept(this, context);
                context.text(".");
                writeTypeParameter(types, context).space();
                context.text(left.name.toString());
            } else {
                writeTypeParameter(types, context);
                tree.meth.accept(this, context);
            }
        } else {
            tree.meth.accept(this, context);
        }

        SourceXML xml = context;

        context.text("(").join(invoke.getArguments(), item -> item.accept(this, xml)).text(")");

        if (precedenceLevel == ROOT) {
            context.semiColon();
        }
        precedenceLevel--;

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitModifiers(ModifiersTree modifiers, SourceXML context) {
        context = traceLine(modifiers, context);

        // ===========================================
        // Annotations
        // ===========================================
        List<? extends AnnotationTree> annotations = modifiers.getAnnotations();

        if (!annotations.isEmpty()) {
            for (AnnotationTree tree : annotations) {
                context = visitAnnotation(tree, context);
            }
            context = startNewLine();
        }

        // ===========================================
        // Modifiers
        // ===========================================
        context = writeModifier(modifiers.getFlags(), context);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitNewArray(NewArrayTree array, SourceXML context) {
        precedenceLevel++;
        traceLine(array, context);

        context.reserved("new").space();
        array.getType().accept(this, context);
        context.text("[").join(array.getDimensions(), item -> item.accept(this, context)).text("]");

        if (precedenceLevel == ROOT) {
            context.semiColon();
        }
        // if (tree.elemtype != null) {
        // print("new ");
        // JCTree elem = tree.elemtype;
        // printBaseElementType(elem);
        //
        // if (!tree.annotations.isEmpty()) {
        // print(' ');
        // printTypeAnnotations(tree.annotations);
        // }
        // if (tree.elems != null) {
        // print("[]");
        // }
        //
        // int i = 0;
        // List<List<JCAnnotation>> da = tree.dimAnnotations;
        // for (List<JCExpression> l = tree.dims; l.nonEmpty(); l = l.tail) {
        // if (da.size() > i && !da.get(i).isEmpty()) {
        // print(' ');
        // printTypeAnnotations(da.get(i));
        // }
        // print("[");
        // i++;
        // printExpr(l.head);
        // print("]");
        // }
        // printBrackets(elem);
        // }
        // if (tree.elems != null) {
        // print("{");
        // printExprs(tree.elems);
        // print("}");
        // }

        precedenceLevel--;
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitNewClass(NewClassTree clazz, SourceXML context) {
        precedenceLevel++;
        traceLine(clazz, context);

        // check enclosing class
        ExpressionTree enclosing = clazz.getEnclosingExpression();

        if (enclosing != null) {
            enclosing.accept(this, context);
            context.text(".");
        }

        context.reserved("new").space();
        writeTypeParameter(clazz.getTypeArguments(), context);
        clazz.getIdentifier().accept(this, context);
        context.text("(");
        context.join(clazz.getArguments(), item -> item.accept(this, context));
        context.text(")");

        ClassTree body = clazz.getClassBody();

        if (body != null) {
            body.accept(this, context);
        }

        if (precedenceLevel == ROOT) {
            context.semiColon();
        }

        precedenceLevel--;
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitOther(Tree arg0, SourceXML context) {
        System.out.println("visitOther");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitParameterizedType(ParameterizedTypeTree type, SourceXML context) {
        traceLine(type, context);

        type.getType().accept(this, context);
        writeTypeParameter(type.getTypeArguments(), context);

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitParenthesized(ParenthesizedTree tree, SourceXML context) {
        traceLine(tree, context);

        latestLine.text("(");
        tree.getExpression().accept(this, latestLine);
        latestLine.text(")");

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitPrimitiveType(PrimitiveTypeTree primitive, SourceXML context) {
        traceLine(primitive, context);

        TypeKind kind = primitive.getPrimitiveTypeKind();

        switch (kind) {
        case BOOLEAN:
        case BYTE:
        case CHAR:
        case DOUBLE:
        case FLOAT:
        case INT:
        case LONG:
        case NULL:
        case SHORT:
        case VOID:
            context.reserved(kind.name().toLowerCase());
            break;

        default:
            throw new Error(kind.toString());
        }

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitReturn(ReturnTree tree, SourceXML context) {
        precedenceLevel = STOP;
        context = traceLine(tree, context);

        context.reserved("return").space();
        tree.getExpression().accept(this, context);
        context.semiColon();

        precedenceLevel = START;
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitSwitch(SwitchTree arg0, SourceXML context) {
        System.out.println("visitSwitch");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitSynchronized(SynchronizedTree arg0, SourceXML context) {
        System.out.println("visitSynchronized");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitThrow(ThrowTree tree, SourceXML context) {
        precedenceLevel = STOP;
        traceLine(tree, context);

        latestLine.reserved("throw").space().visit(tree.getExpression()).semiColon();

        precedenceLevel = START;
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitTry(TryTree tree, SourceXML context) {
        traceLine(tree, context);

        latestLine.reserved("try");
        tree.getBlock().accept(this, latestLine);

        for (CatchTree catchTree : tree.getCatches()) {
            latestLine.space().reserved("catch").space().text("(");
            catchTree.getParameter().accept(this, latestLine).text(")");
            catchTree.getBlock().accept(this, latestLine);
        }

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitTypeCast(TypeCastTree arg0, SourceXML context) {
        System.out.println("visitTypeCast");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitTypeParameter(TypeParameterTree param, SourceXML context) {
        context.type(param.toString());
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitUnary(UnaryTree unary, SourceXML context) {
        precedenceLevel++;
        ExpressionTree expression = unary.getExpression();

        switch (unary.getKind()) {
        case POSTFIX_INCREMENT:
            expression.accept(this, context).text("++");
            break;

        case POSTFIX_DECREMENT:
            expression.accept(this, context).text("--");
            break;

        case PREFIX_INCREMENT:
            expression.accept(this, context.text("++"));
            break;
        case PREFIX_DECREMENT:
            expression.accept(this, context.text("--"));
            break;

        default:
            throw new Error(unary.getKind().toString());
        }

        if (precedenceLevel == ROOT) {
            context.semiColon();
        }

        precedenceLevel--;
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitUnionType(UnionTypeTree arg0, SourceXML context) {
        System.out.println("visitUnionType");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitVariable(VariableTree variable, SourceXML context) {
        context = traceLine(variable, context);

        // Annotations and Modifiers
        visitModifiers(variable.getModifiers(), context);

        // Type
        variable.getType().accept(this, latestLine).space();

        // Name
        latestLine.variable(variable.getName().toString());

        // Value
        ExpressionTree initializer = variable.getInitializer();

        if (initializer != null) {
            latestLine.space().text("=").space();
            initializer.accept(this, latestLine);
        }

        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitWhileLoop(WhileLoopTree arg0, SourceXML context) {
        System.out.println("visitWhileLoop");
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceXML visitWildcard(WildcardTree arg0, SourceXML context) {
        System.out.println("visitWildcard");
        return context;
    }

    /**
     * <p>
     * Create new line.
     * </p>
     */
    private SourceXML startNewLine() {
        SourceXML newLine = root.child("line").attr("n", logicalLine++);

        for (int i = 0; i < indentLevel; i++) {
            newLine.text(indent);
        }
        return this.latestLine = newLine;
    }

    /**
     * <p>
     * Trace line.
     * </p>
     * 
     * @param current
     * @param context TODO
     */
    private SourceXML traceLine(Tree current, SourceXML context) {
        int actualLine = mapper.getLine(current);

        if (actualLine < logicalLine) {
            return context;
        }

        while (logicalLine < actualLine) {
            startNewLine().text(mapper.readLineFrom(logicalLine) + "☆");
        }
        return startNewLine();
    }

    private static final Modifier[] MODIFIE_ORDER = {Modifier.PUBLIC, Modifier.PROTECTED, Modifier.PRIVATE,
            Modifier.STATIC, Modifier.FINAL, Modifier.DEFAULT};

    /**
     * <p>
     * Write modifiers.
     * </p>
     */
    private SourceXML writeModifier(Set<Modifier> modifiers, SourceXML context) {
        for (Modifier modifier : MODIFIE_ORDER) {
            if (modifiers.contains(modifier)) {
                context.reserved(modifier.name().toLowerCase()).space();
            }
        }
        return context;
    }

    /**
     * <p>
     * Write type parameters.
     * </p>
     * 
     * @param list
     * @param context
     */
    private SourceXML writeTypeParameter(List<? extends Tree> list, SourceXML context) {
        return context.children("typeParam", "<", ">", list, (tree, xml) -> tree.accept(this, xml));
    }
}
