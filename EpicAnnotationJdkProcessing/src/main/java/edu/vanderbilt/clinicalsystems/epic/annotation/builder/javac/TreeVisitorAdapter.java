package edu.vanderbilt.clinicalsystems.epic.annotation.builder.javac;

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

public class TreeVisitorAdapter<R,P> implements TreeVisitor<R,P> {

	protected RuntimeException unsupportedTreeTypeException(Tree tree, P parameter) {
		return new UnsupportedOperationException(tree.getClass().getSimpleName() + " not supported here") ;
	}
	
	protected R unsupportedTreeTypeResult(Tree tree, P parameter) {
		throw unsupportedTreeTypeException(tree,parameter);
	}
	
	@Override
	public R visitAnnotatedType(AnnotatedTypeTree annotatedTypeTree, P parameter) { return unsupportedTreeTypeResult(annotatedTypeTree,parameter); }

	@Override
	public R visitAnnotation(AnnotationTree annotationTree, P parameter) { return unsupportedTreeTypeResult(annotationTree,parameter); }

	@Override
	public R visitArrayAccess(ArrayAccessTree arrayAccessTree, P parameter) { return unsupportedTreeTypeResult(arrayAccessTree,parameter); }

	@Override
	public R visitArrayType(ArrayTypeTree arrayTypeTree, P parameter) { return unsupportedTreeTypeResult(arrayTypeTree,parameter); }

	@Override
	public R visitAssert(AssertTree assertTree, P parameter) { return unsupportedTreeTypeResult(assertTree,parameter); }

	@Override
	public R visitAssignment(AssignmentTree assignmentTree, P parameter) { return unsupportedTreeTypeResult(assignmentTree,parameter); }

	@Override
	public R visitBinary(BinaryTree binaryTree, P parameter) { return unsupportedTreeTypeResult(binaryTree,parameter); }

	@Override
	public R visitBlock(BlockTree blockTree, P parameter) { return unsupportedTreeTypeResult(blockTree,parameter); }

	@Override
	public R visitBreak(BreakTree breakTree, P parameter) { return unsupportedTreeTypeResult(breakTree,parameter); }

	@Override
	public R visitCase(CaseTree caseTree, P parameter) { return unsupportedTreeTypeResult(caseTree,parameter); }

	@Override
	public R visitCatch(CatchTree catchTree, P parameter) { return unsupportedTreeTypeResult(catchTree,parameter); }

	@Override
	public R visitClass(ClassTree classTree, P parameter) { return unsupportedTreeTypeResult(classTree,parameter); }

	@Override
	public R visitCompilationUnit(CompilationUnitTree compilationUnitTree, P parameter) { return unsupportedTreeTypeResult(compilationUnitTree,parameter); }

	@Override
	public R visitCompoundAssignment(CompoundAssignmentTree compoundAssignmentTree, P parameter) { return unsupportedTreeTypeResult(compoundAssignmentTree,parameter); }

	@Override
	public R visitConditionalExpression(ConditionalExpressionTree conditionalExpressionTree, P parameter) { return unsupportedTreeTypeResult(conditionalExpressionTree,parameter); }

	@Override
	public R visitContinue(ContinueTree continueTree, P parameter) { return unsupportedTreeTypeResult(continueTree,parameter); }

	@Override
	public R visitDoWhileLoop(DoWhileLoopTree doWhileLoopTree, P parameter) { return unsupportedTreeTypeResult(doWhileLoopTree,parameter); }

	@Override
	public R visitEmptyStatement(EmptyStatementTree emptyStatementTree, P parameter) { return unsupportedTreeTypeResult(emptyStatementTree,parameter); }

	@Override
	public R visitEnhancedForLoop(EnhancedForLoopTree enhancedForLoopTree, P parameter) { return unsupportedTreeTypeResult(enhancedForLoopTree,parameter); }

	@Override
	public R visitErroneous(ErroneousTree erroneousTree, P parameter) { return unsupportedTreeTypeResult(erroneousTree,parameter); }

	@Override
	public R visitExpressionStatement(ExpressionStatementTree expressionStatementTree, P parameter) { return unsupportedTreeTypeResult(expressionStatementTree,parameter); }

	@Override
	public R visitForLoop(ForLoopTree forLoopTree, P parameter) { return unsupportedTreeTypeResult(forLoopTree,parameter); }

	@Override
	public R visitIdentifier(IdentifierTree identifierTree, P parameter) { return unsupportedTreeTypeResult(identifierTree,parameter); }

	@Override
	public R visitIf(IfTree ifTree, P parameter) { return unsupportedTreeTypeResult(ifTree,parameter); }

	@Override
	public R visitImport(ImportTree importTree, P parameter) { return unsupportedTreeTypeResult(importTree,parameter); }

	@Override
	public R visitInstanceOf(InstanceOfTree instanceOfTree, P parameter) { return unsupportedTreeTypeResult(instanceOfTree,parameter); }

	@Override
	public R visitIntersectionType(IntersectionTypeTree intersectionTypeTree, P parameter) { return unsupportedTreeTypeResult(intersectionTypeTree,parameter); }

	@Override
	public R visitLabeledStatement(LabeledStatementTree labeledStatementTree, P parameter) { return unsupportedTreeTypeResult(labeledStatementTree,parameter); }

	@Override
	public R visitLambdaExpression(LambdaExpressionTree lambdaExpressionTree, P parameter) { return unsupportedTreeTypeResult(lambdaExpressionTree,parameter); }

	@Override
	public R visitLiteral(LiteralTree literalTree, P parameter) { return unsupportedTreeTypeResult(literalTree,parameter); }

	@Override
	public R visitMemberReference(MemberReferenceTree memberReferenceTree, P parameter) { return unsupportedTreeTypeResult(memberReferenceTree,parameter); }

	@Override
	public R visitMemberSelect(MemberSelectTree memberSelectTree, P parameter) { return unsupportedTreeTypeResult(memberSelectTree,parameter); }

	@Override
	public R visitMethod(MethodTree methodTree, P parameter) { return unsupportedTreeTypeResult(methodTree,parameter); }

	@Override
	public R visitMethodInvocation(MethodInvocationTree methodInvocationTree, P parameter) { return unsupportedTreeTypeResult(methodInvocationTree,parameter); }

	@Override
	public R visitModifiers(ModifiersTree modifiersTree, P parameter) { return unsupportedTreeTypeResult(modifiersTree,parameter); }

	@Override
	public R visitNewArray(NewArrayTree newArrayTree, P parameter) { return unsupportedTreeTypeResult(newArrayTree,parameter); }

	@Override
	public R visitNewClass(NewClassTree newClassTree, P parameter) { return unsupportedTreeTypeResult(newClassTree,parameter); }

	@Override
	public R visitOther(Tree treeTree, P parameter) { return unsupportedTreeTypeResult(treeTree,parameter); }

	@Override
	public R visitParameterizedType(ParameterizedTypeTree parameterizedTypeTree, P parameter) { return unsupportedTreeTypeResult(parameterizedTypeTree,parameter); }

	@Override
	public R visitParenthesized(ParenthesizedTree parenthesizedTree, P parameter) { return unsupportedTreeTypeResult(parenthesizedTree,parameter); }

	@Override
	public R visitPrimitiveType(PrimitiveTypeTree primitiveTypeTree, P parameter) { return unsupportedTreeTypeResult(primitiveTypeTree,parameter); }

	@Override
	public R visitReturn(ReturnTree returnTree, P parameter) { return unsupportedTreeTypeResult(returnTree,parameter); }

	@Override
	public R visitSwitch(SwitchTree switchTree, P parameter) { return unsupportedTreeTypeResult(switchTree,parameter); }

	@Override
	public R visitSynchronized(SynchronizedTree synchronizedTree, P parameter) { return unsupportedTreeTypeResult(synchronizedTree,parameter); }

	@Override
	public R visitThrow(ThrowTree throwTree, P parameter) { return unsupportedTreeTypeResult(throwTree,parameter); }

	@Override
	public R visitTry(TryTree tryTree, P parameter) { return unsupportedTreeTypeResult(tryTree,parameter); }

	@Override
	public R visitTypeCast(TypeCastTree typeCastTree, P parameter) { return unsupportedTreeTypeResult(typeCastTree,parameter); }

	@Override
	public R visitTypeParameter(TypeParameterTree typeParameterTree, P parameter) { return unsupportedTreeTypeResult(typeParameterTree,parameter); }

	@Override
	public R visitUnary(UnaryTree unaryTree, P parameter) { return unsupportedTreeTypeResult(unaryTree,parameter); }

	@Override
	public R visitUnionType(UnionTypeTree unionTypeTree, P parameter) { return unsupportedTreeTypeResult(unionTypeTree,parameter); }

	@Override
	public R visitVariable(VariableTree variableTree, P parameter) { return unsupportedTreeTypeResult(variableTree,parameter); }

	@Override
	public R visitWhileLoop(WhileLoopTree whileLoopTree, P parameter) { return unsupportedTreeTypeResult(whileLoopTree,parameter); }

	@Override
	public R visitWildcard(WildcardTree wildcardTree, P parameter) { return unsupportedTreeTypeResult(wildcardTree,parameter); }

}
