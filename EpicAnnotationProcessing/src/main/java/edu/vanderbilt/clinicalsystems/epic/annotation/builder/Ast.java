package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import java.util.List;

import javax.lang.model.type.TypeMirror;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.ReportType;

public final class Ast {

	private Ast() {}
	
	public static class Name implements CharSequence {
		private final CharSequence m_name ; 
		public Name( CharSequence name ) { m_name = name ; }
		@Override public int length() { return m_name.length() ; }
		@Override public char charAt(int index) { return m_name.charAt(index) ; }
		@Override public CharSequence subSequence(int start, int end) { return m_name.subSequence(start,end) ; }
		@Override public int hashCode() { return m_name.hashCode() ; }
		@Override public String toString() { return m_name.toString() ; }
		@Override public boolean equals(Object obj) {
			if ( obj == this )
				return true ;
			else if ( obj instanceof Name )
				return m_name.toString().equals( ((Name)obj).toString() ) ;
			else
				return m_name.toString().equals( obj ) ;
		}
	}
	
	public interface Node {
		<R,P> R accept( Visitor<R,P> visitorNode, P parameter ) ;
		void report(ReportType reportType, String message);
	}
	
	public interface Statement extends Node { } // supertypes 
	public interface Expression extends Node { } // supertypes 

	public interface AnnotatedType extends Expression { }
	public interface Annotation extends Expression { }
	public interface ArrayAccess extends Expression { }
	public interface ArrayType extends Node { }
	public interface Assert extends Statement { }
	public interface Assignment extends Expression {
		Expression expression();
		Expression variable();
		enum AssignmentType { 
			ASSIGNMENT,
			PLUS_ASSIGNMENT, MINUS_ASSIGNMENT, MULTIPLY_ASSIGNMENT, DIVIDE_ASSIGNMENT, REMAINDER_ASSIGNMENT,
			BIT_AND_ASSIGNMENT, BIT_OR_ASSIGNMENT, BIT_XOR_ASSIGNMENT,
			LEFT_SHIFT_ASSIGNMENT, RIGHT_SHIFT_SIGNED_ASSIGNMENT, RIGHT_SHIFT_UNSIGNED_ASSIGNMENT, 
			UNKNOWN 
		}
		AssignmentType assignmentType() ;
	}
	public interface Binary extends Expression {
		Expression leftOperand();
		Expression rightOperand();
		enum OperationType {
			PLUS, MINUS, MULTIPLY, DIVIDE, MODULO,
			EQUAL_TO, NOT_EQUAL_TO,
			LESS_THAN, GREATER_THAN, GREATER_THAN_EQUAL, LESS_THAN_EQUAL,
			PLUS_ASSIGNMENT, MINUS_ASSIGNMENT, MULTIPLY_ASSIGNMENT, DIVIDE_ASSIGNMENT, MODULO_ASSIGNMENT,
			UNKNOWN
			}
		OperationType operationType();
	}
	public interface Block extends Statement {
		List<? extends Statement> statements();
	}
	public interface Break extends Statement { }
	public interface Case extends Statement { }
	public interface Catch extends Node { }
	public interface Class extends Statement { }
	public interface CompilationUnit extends Node { }
	public interface CompoundAssignment extends Expression {
		Expression variable();
		Expression operand();
		enum OperationType {
			PLUS_ASSIGNMENT, MINUS_ASSIGNMENT, MULTIPLY_ASSIGNMENT, DIVIDE_ASSIGNMENT,
			UNKNOWN
		}
		OperationType operationType();
	}
	public interface ConditionalExpression extends Expression { }
	public interface Continue extends Statement { }
	public interface DoWhileLoop extends Statement {
		Statement statement();
		Expression condition();
	}
	public interface EmptyStatement extends Statement { }
	public interface EnhancedForLoop extends Statement {
		VariableDeclarationsStatement variable();
		Expression expression();
		Statement statement();
	}
	public interface Erroneous extends Expression { }
	public interface ExpressionStatement extends Statement {
		Expression expression();
	}
	public interface ForLoop extends Statement {
		List<? extends Statement> initializer();
		Expression condition();
		List<? extends ExpressionStatement> update();
		Statement statement();
	}
	public interface Identifier extends Expression {
		Name name(); }
	public interface If extends Statement {
		Expression condition();
		Statement thenStatement();
		Statement elseStatement();
	}
	public interface Import extends Node { }
	public interface InstanceOf extends Expression { }
	public interface IntersectionType extends Node { }
	public interface LabeledStatement extends Statement { }
	public interface LambdaExpression extends Expression { }
	public interface Literal extends Expression {
		Object value();
		enum LiteralType {
			STRING_LITERAL, CHAR_LITERAL,
			INT_LITERAL, FLOAT_LITERAL, DOUBLE_LITERAL, BOOLEAN_LITERAL, LONG_LITERAL,
			NULL_LITERAL,
			UNKNOWN
		}
		LiteralType literalType();
	}
	public interface MemberReference extends Expression { }
	public interface MemberSelect extends Expression {
		Expression expression();
		Name identifier();
	}
	public interface Method extends Node {
		TypeMirror returnType();
		Block body() ;
		List<? extends Variable> parameters();
	}
	public interface MethodInvocation extends Expression {
		List<? extends Expression> arguments() ;
		Expression methodSelect();
	}
	public interface Modifiers extends Node { }
	public interface NewArray extends Expression { }
	public interface NewClass extends Expression { }
	public interface ParameterizedType extends Node { }
	public interface Parenthesized extends Expression {
		Expression expression(); }
	public interface PrimitiveType extends Node { }
	public interface Return extends Statement {
		Expression expression();
	}
	public interface Switch extends Statement { }
	public interface Synchronized extends Statement { }
	public interface Throw extends Statement { }
	public interface Try extends Statement { }
	public interface TypeCast extends Expression {
		Expression expression();
	}
	public interface TypeParameter extends Node { }
	public interface Unary extends Expression {
		Expression expression();
		enum OperationType {
			PREFIX_INCREMENT, POSTFIX_INCREMENT, PREFIX_DECREMENT, POSTFIX_DECREMENT,
			UNARY_PLUS, UNARY_MINUS,
			LOGICAL_COMPLEMENT,
			UNKNOWN
		}
		OperationType operationType();
	}
	public interface UnionType extends Node { }
	public interface Variable extends Node {
		TypeMirror type();
		Name name();
		Expression initializer();
	}
	public interface VariableDeclarationsStatement extends Statement {
		List<? extends Variable> variables() ;
	}
	public interface VariableDeclarationsExpression extends Expression {
		List<? extends Variable> variables() ;
	}
	
	public interface WhileLoop extends Statement {
		Expression condition();
		Statement statement();
	}
	public interface Wildcard extends Node { }	
	
	public static abstract class Interpreter<R,P> extends Adapter<R,P> {

		private final RoutineTools m_tools;
		public Interpreter(RoutineTools tools) { m_tools = tools ; }

		@Override protected R unsupportedNodeTypeResult(Node node, P parameter) {
			m_tools.report(ReportType.ERROR, node.getClass().getSimpleName() + " not supported yet",node);
			return null ;
		}

	}

	public interface Visitor<R,P> {
		R visitNode(Node node, P parameter) ;
		R visitExpression(Expression expressionNode, P parameter) ;
		R visitStatement(Statement statementNode, P parameter) ;
		
		R visitAnnotatedType(AnnotatedType annotatedTypeNode, P parameter) ;
		R visitAnnotation(Annotation annotationNode, P parameter) ;
		R visitArrayAccess(ArrayAccess arrayAccessNode, P parameter) ;
		R visitArrayType(ArrayType arrayTypeNode, P parameter) ;
		R visitAssert(Assert assertNode, P parameter) ;
		R visitAssignment(Assignment assignmentNode, P parameter) ;
		R visitBinary(Binary binaryNode, P parameter) ;
		R visitBlock(Block blockNode, P parameter) ;
		R visitBreak(Break breakNode, P parameter) ;
		R visitCase(Case caseNode, P parameter) ;
		R visitCatch(Catch catchNode, P parameter) ;
		R visitClass(Class classNode, P parameter) ;
		R visitCompilationUnit(CompilationUnit compilationUnitNode, P parameter) ;
		R visitCompoundAssignment(CompoundAssignment compoundAssignmentNode, P parameter) ;
		R visitConditionalExpression(ConditionalExpression conditionalExpressionNode, P parameter) ;
		R visitContinue(Continue continueNode, P parameter) ;
		R visitDoWhileLoop(DoWhileLoop doWhileLoopNode, P parameter) ;
		R visitEmptyStatement(EmptyStatement emptyStatementNode, P parameter) ;
		R visitEnhancedForLoop(EnhancedForLoop enhancedForLoopNode, P parameter) ;
		R visitErroneous(Erroneous erroneousNode, P parameter) ;
		R visitExpressionStatement(ExpressionStatement expressionStatementNode, P parameter) ;
		R visitForLoop(ForLoop forLoopNode, P parameter) ;
		R visitIdentifier(Identifier identifierNode, P parameter) ;
		R visitIf(If ifNode, P parameter) ;
		R visitImport(Import importNode, P parameter) ;
		R visitInstanceOf(InstanceOf instanceOfNode, P parameter) ;
		R visitIntersectionType(IntersectionType intersectionTypeNode, P parameter) ;
		R visitLabeledStatement(LabeledStatement labeledStatementNode, P parameter) ;
		R visitLambdaExpression(LambdaExpression lambdaExpressionNode, P parameter) ;
		R visitLiteral(Literal literalNode, P parameter) ;
		R visitMemberReference(MemberReference memberReferenceNode, P parameter) ;
		R visitMemberSelect(MemberSelect memberSelectNode, P parameter) ;
		R visitMethod(Method methodNode, P parameter) ;
		R visitMethodInvocation(MethodInvocation methodInvocationNode, P parameter) ;
		R visitModifiers(Modifiers modifiersNode, P parameter) ;
		R visitNewArray(NewArray newArrayNode, P parameter) ;
		R visitNewClass(NewClass newClassNode, P parameter) ;
		R visitOther(Node node, P parameter) ;
		R visitParameterizedType(ParameterizedType parameterizedTypeNode, P parameter) ;
		R visitParenthesized(Parenthesized parenthesizedNode, P parameter) ;
		R visitPrimitiveType(PrimitiveType primitiveTypeNode, P parameter) ;
		R visitReturn(Return returnNode, P parameter) ;
		R visitSwitch(Switch switchNode, P parameter) ;
		R visitSynchronized(Synchronized synchronizedNode, P parameter) ;
		R visitThrow(Throw throwNode, P parameter) ;
		R visitTry(Try tryNode, P parameter) ;
		R visitTypeCast(TypeCast typeCastNode, P parameter) ;
		R visitTypeParameter(TypeParameter typeParameterNode, P parameter) ;
		R visitUnary(Unary unaryNode, P parameter) ;
		R visitUnionType(UnionType unionTypeNode, P parameter) ;
		R visitVariable(Variable variableNode, P parameter) ;
		R visitVariablesStatement(VariableDeclarationsStatement variablesStatementNode, P parameter) ;
		R visitVariablesExpression(VariableDeclarationsExpression variablesExpressionNode, P parameter) ;
		R visitWhileLoop(WhileLoop whileLoopNode, P parameter) ;
		R visitWildcard(Wildcard wildcardNode, P parameter) ;
	}

	public static class Adapter<R,P> implements Visitor<R,P> {
		protected RuntimeException unsupportedNodeTypeException(Node node, P parameter) {
			return new UnsupportedOperationException(node.getClass().getSimpleName() + " not supported here") ;
		}
		
		protected R unsupportedNodeTypeResult(Node node, P parameter) {
			throw unsupportedNodeTypeException(node,parameter);
		}
		
		@Override public R visitNode(Node node, P parameter) { return unsupportedNodeTypeResult(node,parameter) ; }
		@Override public R visitExpression(Expression expressionNode, P parameter) { return visitNode(expressionNode,parameter) ; }
		@Override public R visitStatement(Statement statementNode, P parameter) { return visitNode(statementNode,parameter) ; }
		
		@Override public R visitAnnotatedType(AnnotatedType annotatedTypeNode, P parameter) { return visitExpression(annotatedTypeNode,parameter) ; }
		@Override public R visitAnnotation(Annotation annotationNode, P parameter) { return visitExpression(annotationNode,parameter) ; }
		@Override public R visitArrayAccess(ArrayAccess arrayAccessNode, P parameter) { return visitExpression(arrayAccessNode,parameter) ; }
		@Override public R visitArrayType(ArrayType arrayTypeNode, P parameter) { return visitNode(arrayTypeNode,parameter) ; }
		@Override public R visitAssert(Assert assertNode, P parameter) { return visitStatement(assertNode,parameter) ; }
		@Override public R visitAssignment(Assignment assignmentNode, P parameter) { return visitExpression(assignmentNode,parameter) ; }
		@Override public R visitBinary(Binary binaryNode, P parameter) { return visitExpression(binaryNode,parameter) ; }
		@Override public R visitBlock(Block blockNode, P parameter) { return visitStatement(blockNode,parameter) ; }
		@Override public R visitBreak(Break breakNode, P parameter) { return visitStatement(breakNode,parameter) ; }
		@Override public R visitCase(Case caseNode, P parameter) { return visitStatement(caseNode,parameter) ; }
		@Override public R visitCatch(Catch catchNode, P parameter) { return visitNode(catchNode,parameter) ; }
		@Override public R visitClass(Class classNode, P parameter) { return visitStatement(classNode,parameter) ; }
		@Override public R visitCompilationUnit(CompilationUnit compilationUnitNode, P parameter) { return visitNode(compilationUnitNode,parameter) ; }
		@Override public R visitCompoundAssignment(CompoundAssignment compoundAssignmentNode, P parameter) { return visitExpression(compoundAssignmentNode,parameter) ; }
		@Override public R visitConditionalExpression(ConditionalExpression conditionalExpressionNode, P parameter) { return visitExpression(conditionalExpressionNode,parameter) ; }
		@Override public R visitContinue(Continue continueNode, P parameter) { return visitStatement(continueNode,parameter) ; }
		@Override public R visitDoWhileLoop(DoWhileLoop doWhileLoopNode, P parameter) { return visitStatement(doWhileLoopNode,parameter) ; }
		@Override public R visitEmptyStatement(EmptyStatement emptyStatementNode, P parameter) { return visitStatement(emptyStatementNode,parameter) ; }
		@Override public R visitEnhancedForLoop(EnhancedForLoop enhancedForLoopNode, P parameter) { return visitStatement(enhancedForLoopNode,parameter) ; }
		@Override public R visitErroneous(Erroneous erroneousNode, P parameter) { return visitExpression(erroneousNode,parameter) ; }
		@Override public R visitExpressionStatement(ExpressionStatement expressionStatementNode, P parameter) { return visitStatement(expressionStatementNode,parameter) ; }
		@Override public R visitForLoop(ForLoop forLoopNode, P parameter) { return visitStatement(forLoopNode,parameter) ; }
		@Override public R visitIdentifier(Identifier identifierNode, P parameter) { return visitExpression(identifierNode,parameter) ; }
		@Override public R visitIf(If ifNode, P parameter) { return visitStatement(ifNode,parameter) ; }
		@Override public R visitImport(Import importNode, P parameter) { return visitNode(importNode,parameter) ; }
		@Override public R visitInstanceOf(InstanceOf instanceOfNode, P parameter) { return visitExpression(instanceOfNode,parameter) ; }
		@Override public R visitIntersectionType(IntersectionType intersectionTypeNode, P parameter) { return visitNode(intersectionTypeNode,parameter) ; }
		@Override public R visitLabeledStatement(LabeledStatement labeledStatementNode, P parameter) { return visitStatement(labeledStatementNode,parameter) ; }
		@Override public R visitLambdaExpression(LambdaExpression lambdaExpressionNode, P parameter) { return visitExpression(lambdaExpressionNode,parameter) ; }
		@Override public R visitLiteral(Literal literalNode, P parameter) { return visitExpression(literalNode,parameter) ; }
		@Override public R visitMemberReference(MemberReference memberReferenceNode, P parameter) { return visitExpression(memberReferenceNode,parameter) ; }
		@Override public R visitMemberSelect(MemberSelect memberSelectNode, P parameter) { return visitExpression(memberSelectNode,parameter) ; }
		@Override public R visitMethod(Method methodNode, P parameter) { return visitNode(methodNode,parameter) ; }
		@Override public R visitMethodInvocation(MethodInvocation methodInvocationNode, P parameter) { return visitExpression(methodInvocationNode,parameter) ; }
		@Override public R visitModifiers(Modifiers modifiersNode, P parameter) { return visitNode(modifiersNode,parameter) ; }
		@Override public R visitNewArray(NewArray newArrayNode, P parameter) { return visitExpression(newArrayNode,parameter) ; }
		@Override public R visitNewClass(NewClass newClassNode, P parameter) { return visitExpression(newClassNode,parameter) ; }
		@Override public R visitOther(Node node, P parameter) { return visitNode(node,parameter) ; }
		@Override public R visitParameterizedType(ParameterizedType parameterizedTypeNode, P parameter) { return visitNode(parameterizedTypeNode,parameter) ; }
		@Override public R visitParenthesized(Parenthesized parenthesizedNode, P parameter) { return visitExpression(parenthesizedNode,parameter) ; }
		@Override public R visitPrimitiveType(PrimitiveType primitiveTypeNode, P parameter) { return visitNode(primitiveTypeNode,parameter) ; }
		@Override public R visitReturn(Return returnNode, P parameter) { return visitStatement(returnNode,parameter) ; }
		@Override public R visitSwitch(Switch switchNode, P parameter) { return visitStatement(switchNode,parameter) ; }
		@Override public R visitSynchronized(Synchronized synchronizedNode, P parameter) { return visitStatement(synchronizedNode,parameter) ; }
		@Override public R visitThrow(Throw throwNode, P parameter) { return visitStatement(throwNode,parameter) ; }
		@Override public R visitTry(Try tryNode, P parameter) { return visitStatement(tryNode,parameter) ; }
		@Override public R visitTypeCast(TypeCast typeCastNode, P parameter) { return visitExpression(typeCastNode,parameter) ; }
		@Override public R visitTypeParameter(TypeParameter typeParameterNode, P parameter) { return visitNode(typeParameterNode,parameter) ; }
		@Override public R visitUnary(Unary unaryNode, P parameter) { return visitExpression(unaryNode,parameter) ; }
		@Override public R visitUnionType(UnionType unionTypeNode, P parameter) { return visitNode(unionTypeNode,parameter) ; }
		@Override public R visitVariable(Variable variableNode, P parameter) { return visitNode(variableNode,parameter) ; }
		@Override public R visitVariablesStatement(VariableDeclarationsStatement variablesStatementNode, P parameter) { return visitStatement(variablesStatementNode,parameter) ; }
		@Override public R visitVariablesExpression(VariableDeclarationsExpression variablesExpressionNode, P parameter) { return visitExpression(variablesExpressionNode,parameter) ; }
		@Override public R visitWhileLoop(WhileLoop whileLoopNode, P parameter) { return visitStatement(whileLoopNode,parameter) ; }
		@Override public R visitWildcard(Wildcard wildcardNode, P parameter) { return visitNode(wildcardNode,parameter) ; }
	}
}
