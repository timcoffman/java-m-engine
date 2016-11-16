package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.IdentifierResolution;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.MethodResolution;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.ReportType;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.TypeResolution;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.VariableResolution;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeFunction;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeValue;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;
import edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle;
import edu.vanderbilt.clinicalsystems.m.lang.RoutineAccess;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Assignment;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BinaryOperation;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.FunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.InvalidExpression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.TagReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.UnaryOperation;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;

public class ExpressionGenerator extends Generator<Expression,Ast.Expression> {
		
	public ExpressionGenerator(RoutineTools builderTools) {
		super( builderTools ) ;
	}
	
	private final class ExpressionInterpreter extends Ast.Interpreter<Expression, Listener> {
		
		public ExpressionInterpreter() { super(tools()) ; }

		@Override
		public Expression visitTypeCast(Ast.TypeCast typeCastNode, Listener listener) {
			// there is no type checking in M
			return tools().expressions().generate( typeCastNode.expression(), listener );
		}

		@Override
		public Expression visitLiteral(Ast.Literal literalNode, Listener listener) {
			switch ( literalNode.literalType() ) {
			case STRING_LITERAL:
			case INT_LITERAL:
			case FLOAT_LITERAL:
			case DOUBLE_LITERAL:
			case CHAR_LITERAL:
			case LONG_LITERAL:
				return new Constant( literalNode.value().toString() ) ;
			case BOOLEAN_LITERAL:
				return new Constant( ((Boolean)literalNode.value()) ? "1" : "0" ) ;
			case NULL_LITERAL:
				return new Constant( "" ) ;
			default:
				return super.visitLiteral( literalNode, listener );
			}
		}
		
		@Override
		public Expression visitIdentifier(Ast.Identifier identifierNode, Listener listener) {
			IdentifierResolution resolvedIdentifier = tools().resolveIdentifier( identifierNode ) ;
			if ( null == resolvedIdentifier ) {

				report( RoutineTools.ReportType.ERROR, "cannot resolve identifier", identifierNode ) ;
				return new VariableReference( Scope.LOCAL, ReferenceStyle.DIRECT, identifierNode.name().toString() );

			}
			
			return resolvedIdentifier.accept( new IdentifierResolution.Visitor<Expression, Listener>() {
				
				@Override
				public Expression visit(VariableResolution variableResolution, Listener listener) {
					Object constantValue = variableResolution.constantValue();
					if ( null != constantValue )
						return new Constant( constantValue.toString() );
					
					else
						return new VariableReference( Scope.LOCAL, ReferenceStyle.DIRECT, identifierNode.name().toString() );
					
				}
				
				@Override
				public VariableReference visit( MethodResolution methodResolution, Listener listener ) {
					
					return new VariableReference( Scope.LOCAL, ReferenceStyle.DIRECT, identifierNode.name().toString() );
					
				}
				
				@Override
				public VariableReference visit( TypeResolution typeResolution, Listener listener ) {

					throw new IllegalArgumentException("what the heck") ;
					
				}
				

//				@Override
//				public VariableReference visitType(TypeElement typeElement, Void p) {
//					
//					Library libraryAnnotation = resolvedIdentifier.getAnnotation( Library.class ) ;
//					NativeType nativeTypeAnnotation = resolvedIdentifier.getAnnotation( NativeType.class ) ;
//					EpicRoutineLibrary epicRoutineLibraryAnnotation = resolvedIdentifier.getAnnotation( EpicRoutineLibrary.class ) ;
//					if ( null != libraryAnnotation ) {
//						/* OK */
//					} else if ( null != nativeTypeAnnotation ) {
//						/* OK */
//					} else if ( null != epicRoutineLibraryAnnotation ) {
//						/* OK */
//					} else {
//						tools().reportError( "type without a Library or EpicRoutineLibrary cannot be used", identifierNode ) ;
//					}
//					return new VariableReference( Scope.LOCAL, ReferenceStyle.DIRECT, identifierNode.name().toString() );
//					
//				}



//				@Override
//				public VariableReference visitMethod(Ast.Method method, Listener listener) {
//					
//					Function functionAnnotation = resolvedIdentifier.getAnnotation( Function.class ) ;
//					EpicTag epicTagAnnotation = resolvedIdentifier.getAnnotation( EpicTag.class ) ;
//					if ( null != functionAnnotation ) {
//						/* OK */
//					} else if ( null != epicTagAnnotation ) {
//						/* OK */
//					} else {
//						tools().reportError( "type without a Function or EpicTag cannot be used", identifierNode ) ;
//					}
//					return new VariableReference( Scope.LOCAL, ReferenceStyle.DIRECT, identifierNode.name().toString() );
//
//				}
			}, listener) ;
			
//			return resolvedIdentifier.asType().accept( new TypeInterpreter<VariableReference, Void>(tools()) {
//
//				@Override
//				protected VariableReference unsupportedTypeMirrorTypeResult( TypeMirror typeMirror, Listener listener ) {
//					reportError( "no checking defined for type " + typeMirror.toString(), identifierTree );
//					return new VariableReference( Scope.LOCAL, ReferenceStyle.DIRECT, identifierTree.getName().toString() );
//					
//				}
//
//				@Override
//				public VariableReference visitPrimitive(PrimitiveType primitiveType, Listener listener) {
//					switch( primitiveType.getKind() ) {
//					case DOUBLE:
//					case FLOAT:
//					case INT:
//					case SHORT:
//					case LONG:
//					case BOOLEAN:
//						/* acceptable */
//						break ;
//					case CHAR:
//					case BYTE:
//					default:
//						reportError("primitive type " + primitiveType.getKind() + " not supported", identifierTree);
//						break ;
//					}
//					return new VariableReference( Scope.LOCAL, ReferenceStyle.DIRECT, identifierTree.getName().toString() );
//
//				}
//
//
//
//				@Override
//				public VariableReference visitDeclared(DeclaredType declaredType, Listener listener) {
//					
//					return new VariableReference( Scope.LOCAL, ReferenceStyle.DIRECT, identifierTree.getName().toString() );
//					
//				}
//
//				@Override
//				public VariableReference visitExecutable(ExecutableType declaredType, Listener listener) {
//					Function functionAnnotation = declaredType.getAnnotation( Function.class ) ;
//					EpicTag epicTagAnnotation = declaredType.getAnnotation( EpicTag.class ) ;
//					if ( null != functionAnnotation ) {
//						/* OK */
//					} else if ( null != epicTagAnnotation ) {
//						/* OK */
//					} else {
//						reportError( "type without a Function or EpicTag cannot be used", identifierTree ) ;
//					}
//					return new VariableReference( Scope.LOCAL, ReferenceStyle.DIRECT, identifierTree.getName().toString() );
//				}
//				
//			}, null ) ;
		}

		@Override
		public Expression visitParenthesized(Ast.Parenthesized parenthesizedNode, Listener listener) {
			return parenthesizedNode.expression().accept(this,listener) ;
		}

		@Override
		public Expression visitAssignment(Ast.Assignment assignment, Listener listener) {
			Expression operand = tools().expressions().generate( assignment.variable(), listener ) ;
			if ( operand instanceof VariableReference ) {
				VariableReference variable = (VariableReference)operand ;
				Expression expression = tools().expressions().generate( assignment.expression(), listener ) ;
				
				Command assignmentCommand = new Command( CommandType.SET, new AssignmentList( new Assignment( variable, expression) ) );
				listener.generateSideEffect(Listener.Location.BEFORE_EXPRESSION, assignmentCommand ) ;
			} else {
				report( ReportType.ERROR, "assignment does not identify a variable", assignment.variable());
			}
			
			return operand ;
		}
		
		@Override
		public Expression visitCompoundAssignment( Ast.CompoundAssignment compoundAssignment, Listener listener) {
			Expression operand = tools().expressions().generate( compoundAssignment.variable(), listener);
			if ( operand instanceof VariableReference ) {
				VariableReference variable = (VariableReference)operand ;
				Expression operation = buildOperation( compoundAssignment, listener ) ;
				
				Command assignmentCommand = new Command( CommandType.SET, new AssignmentList( new Assignment( variable, operation) ) ) ;
				listener.generateSideEffect( Listener.Location.BEFORE_EXPRESSION, assignmentCommand);
			} else {
				report( ReportType.ERROR, "assignment does not identify a variable", compoundAssignment.variable());
			}
			return operand ;
		}

		@Override
		public Expression visitBinary(Ast.Binary binaryNode, Listener listener) {
			Expression expression = buildOperation(binaryNode, listener);
			if ( expression instanceof BinaryOperation ) {
				BinaryOperation binaryOperation = (BinaryOperation)expression ;
				if ( binaryOperation.operator().commutative() ) {
					Expression rhs = binaryOperation.rightHandSide();
					Expression lhs = binaryOperation.leftHandSide();
					if (rhs instanceof BinaryOperation && !(lhs instanceof BinaryOperation) ) {
						expression = new BinaryOperation(rhs, binaryOperation.operator(), lhs ) ;
					}
				}
			}
			return expression;
		}

		@Override
		public Expression visitUnary(Ast.Unary unaryNode, Listener listener) {
			Expression operation = buildOperation(unaryNode, listener);
			return operation;
		}

		@Override
		public Expression visitMethodInvocation(Ast.MethodInvocation methodInvocationNode, Listener listener) {
			return buildFunctionCall( methodInvocationNode, listener ) ;
		}

		@Override
		public Expression visitMemberSelect(Ast.MemberSelect memberSelectNode, Listener listener) {
			TagReference tagRef ;
			Expression instance = tools().expressions().generate( memberSelectNode.expression(), listener );
			tagRef = new TagReference( Scope.LOCAL, ReferenceStyle.DIRECT, memberSelectNode.identifier().toString(), ((VariableReference)instance).variableName(), RoutineAccess.EXPLICIT ) ;
			return tagRef;
		}
		
		
		
	}

	private static final ThreadLocal<Integer> tl_generationDepth = new ThreadLocal<Integer>() {
		@Override protected Integer initialValue() { return 0 ; }
	} ; 
	
	@Override
	public Expression generate( Ast.Expression expressionTree, Listener listener ) {
		int generationDepth = tl_generationDepth.get() ;
		if ( generationDepth > 10000 )
			throw new IllegalArgumentException( "exceeded maximum expression depth" ) ;
		try {
			tl_generationDepth.set( generationDepth + 1 );
			return expressionTree.accept( new ExpressionInterpreter(), listener ) ;
		} finally {
			tl_generationDepth.set( generationDepth );
		}
	}
	
	private Expression buildFunctionCall(Ast.MethodInvocation methodInvocationNode, Listener listener) {
		MethodResolution methodInvocationTarget = tools().resolveMethodInvocationTarget( methodInvocationNode );
		if ( null == methodInvocationTarget ) {
			TagReference tagRef = methodInvocationNode.methodSelect().accept( new Ast.Interpreter<TagReference, Listener>(tools()) {

				@Override
				public TagReference visitIdentifier(Ast.Identifier identifierNode, Listener listener) {
					return new TagReference( Scope.LOCAL, ReferenceStyle.DIRECT, identifierNode.name().toString(), null, RoutineAccess.LOCAL ) ;
				}

				@Override
				public TagReference visitMemberSelect(Ast.MemberSelect memberSelectNode, Listener listener) {
					return new TagReference( Scope.LOCAL, ReferenceStyle.DIRECT, memberSelectNode.identifier().toString(), memberSelectNode.expression().toString(), RoutineAccess.LOCAL ) ;
				}
				
				
				
			}, listener) ;
			List<Expression> arguments = StreamSupport.stream(methodInvocationNode.arguments().spliterator(),false).map(e->tools().expressions().generate(e,listener)).collect(Collectors.toList());
			FunctionCall functionCall = new RoutineFunctionCall( tagRef, FunctionCall.Returning.UNKNOWN, arguments ) ;
			return functionCall ;
		}
		
		NativeValue nativeValueAnnotation = methodInvocationTarget.declaration().getAnnotation( NativeValue.class ) ;
		if ( null != nativeValueAnnotation ) {
			switch ( nativeValueAnnotation.value() ) {
			case INITIAL_VALUE:
				return Constant.NULL ;
			default:
				report( RoutineTools.ReportType.ERROR, "unrecognized native value", methodInvocationNode ) ;
			}
		}
		NativeFunction nativeFunctionAnnotation = methodInvocationTarget.declaration().getAnnotation( NativeFunction.class ) ;
		if ( null != nativeFunctionAnnotation ) {
			switch ( nativeFunctionAnnotation.value() ) {
			case VALUE_INDEX:
				return buildValueIndex( methodInvocationNode, listener ) ;
			case IMPLICIT_CAST:
				return buildImplicitCast( methodInvocationNode, listener ) ;
			default:
				report( RoutineTools.ReportType.ERROR, "unrecognized native function", methodInvocationNode ) ;
			}
		}
			
		String tagName = tools().tagName( methodInvocationTarget.declaration() ) ;
		String routineName = tools().routineName( methodInvocationTarget.declaration() ) ;
		RoutineAccess routineAccess = tools().routineAccess( methodInvocationTarget.declaration() ) ;
		TagReference tagRef = new TagReference( Scope.LOCAL, ReferenceStyle.DIRECT, tagName, routineName, routineAccess ) ;
		FunctionCall.Returning returning ;
		if ( tools().isTypeOfAnything( methodInvocationTarget.returnType() ) )
			returning = FunctionCall.Returning.SOME_VALUE ;
		else
			returning = FunctionCall.Returning.NO_VALUE ;
		List<Expression> arguments = StreamSupport.stream(methodInvocationNode.arguments().spliterator(),false).map(e->tools().expressions().generate(e,listener)).collect(Collectors.toList());
		FunctionCall functionCall = new RoutineFunctionCall( tagRef, returning, arguments ) ;
		return functionCall ;
	}
	
	private Expression buildValueIndex( Ast.MethodInvocation methodInvocationNode, Listener listener ) {
		List<Expression> keys = StreamSupport.stream(methodInvocationNode.arguments().spliterator(),false).map(e->tools().expressions().generate(e,listener)).collect(Collectors.toList());
		
		return methodInvocationNode.methodSelect().accept( new Ast.Interpreter<VariableReference, Listener>(tools()) {
			@Override
			public VariableReference visitIdentifier(Ast.Identifier identifierNode, Listener listener) {
				return new VariableReference( Scope.LOCAL, ReferenceStyle.DIRECT, identifierNode.name().toString(), keys ) ;
			}
			@Override
			public VariableReference visitMemberSelect(Ast.MemberSelect memberSelectNode, Listener listener) {
				Ast.Identifier identifierNode = ((Ast.Identifier)memberSelectNode.expression()) ;
				return new VariableReference( Scope.LOCAL, ReferenceStyle.DIRECT, identifierNode.name().toString(), keys ) ;
			}
		}, listener) ;
	}
	
	private Expression buildImplicitCast( Ast.MethodInvocation methodInvocationNode, Listener listener ) {
		Ast.Expression expression = methodInvocationNode.methodSelect().accept( new Ast.Interpreter<Ast.Expression,Listener>(tools()) {

			@Override public Ast.Expression visitExpression( Ast.Expression expression, Listener listener ) {
				return expression ;
			}
			
			@Override public Ast.Expression visitMemberSelect( Ast.MemberSelect memberSelectNode, Listener listener ) {
				return memberSelectNode.expression() ;
			}

		}, listener) ;
		return tools().expressions().generate( expression, listener ) ;
	}
	
	private void generateSideEffect( Generator.Listener listener, Generator.Listener.Location location, RoutineElement element ) {
		listener.generateSideEffect( location, element );
	}
	
	private void generateVariableSideEffect( Expression operand, OperatorType operatorType, Ast.Unary.OperationType operationType, Listener listener, Generator.Listener.Location location, Ast.Node node ) {
		if ( operand instanceof VariableReference )
			generateVariableSideEffect( (VariableReference)operand, operatorType, listener, location, node);
		else
			report( RoutineTools.ReportType.ERROR, "target of operation " + operationType + " must be a " + VariableReference.class.getSimpleName() + ", not a " + operand.getClass().getSimpleName(), node ) ;
	}
	
	private void generateVariableSideEffect( VariableReference variable, OperatorType operatorType, Listener listener, Generator.Listener.Location location, Ast.Node node ) {
		generateSideEffect( listener, location, new Command( CommandType.SET, new AssignmentList( new Assignment( variable, new BinaryOperation(variable, operatorType, Constant.from(1)) ) ) ) ) ;
	}
	
	private Expression buildOperation(Ast.Unary unaryNode, Listener listener) {
		Expression operand = tools().expressions().generate( unaryNode.expression(), listener ) ;
		switch ( unaryNode.operationType() ) {
		case UNARY_PLUS:
			return new UnaryOperation( OperatorType.ADD, operand) ;
		case UNARY_MINUS:
			return new UnaryOperation( OperatorType.SUBTRACT, operand) ;
		case LOGICAL_COMPLEMENT:
			return new UnaryOperation( OperatorType.NOT, operand) ;
		case POSTFIX_INCREMENT:
			generateVariableSideEffect( operand, OperatorType.ADD, unaryNode.operationType(), listener, Listener.Location.AFTER_EXPRESSION, unaryNode ) ;
			return operand ;
		case PREFIX_INCREMENT:
			generateVariableSideEffect( operand, OperatorType.ADD, unaryNode.operationType(), listener, Listener.Location.BEFORE_EXPRESSION, unaryNode ) ;
			return operand ;
		case POSTFIX_DECREMENT:
			generateVariableSideEffect( operand, OperatorType.SUBTRACT, unaryNode.operationType(), listener, Listener.Location.AFTER_EXPRESSION, unaryNode ) ;
			return operand ;
		case PREFIX_DECREMENT:
			generateVariableSideEffect( operand, OperatorType.SUBTRACT, unaryNode.operationType(), listener, Listener.Location.BEFORE_EXPRESSION, unaryNode ) ;
			return operand ;
		default:
			report(RoutineTools.ReportType.ERROR, "does not support " + unaryNode.operationType(), unaryNode);
			return InvalidExpression.instance("unrecognized unary operation " + unaryNode.operationType() ) ;
		}
	}
	
	private Expression buildOperation(Ast.CompoundAssignment compoundAssignment, Listener listener) {
		Expression variable = tools().expressions().generate( compoundAssignment.variable(), listener ) ;
		Expression operand =  tools().expressions().generate( compoundAssignment.operand(), listener ) ;
		switch ( compoundAssignment.operationType() ) {
		case PLUS_ASSIGNMENT:
			return new BinaryOperation( variable, OperatorType.ADD, operand ) ;
		case MINUS_ASSIGNMENT:
			return new BinaryOperation( variable, OperatorType.SUBTRACT, operand ) ;
		case MULTIPLY_ASSIGNMENT:
			return new BinaryOperation( variable, OperatorType.MULTIPLY, operand ) ;
		case DIVIDE_ASSIGNMENT:
			return new BinaryOperation( variable, OperatorType.DIVIDE, operand ) ;
		default:
			report(RoutineTools.ReportType.ERROR, "does not support " + compoundAssignment.operationType(), compoundAssignment);
			return InvalidExpression.instance("unrecognized assignment operation " + compoundAssignment.operationType() ) ;
		}
	}
	
	private Expression buildOperation(Ast.Binary binaryNode, Listener listener) {
		Expression lhs = tools().expressions().generate( binaryNode.leftOperand(), listener ) ;
		Expression rhs = tools().expressions().generate( binaryNode.rightOperand(), listener ) ;
		switch ( binaryNode.operationType() ) {
		case PLUS:
			if ( tools().isTypeOfString( binaryNode.leftOperand() ) )
				return new BinaryOperation(lhs, OperatorType.CONCAT, rhs) ;
			else
				return new BinaryOperation(lhs, OperatorType.ADD, rhs) ;
		case MINUS:
			return new BinaryOperation(lhs, OperatorType.SUBTRACT, rhs) ;
		case MULTIPLY:
			return new BinaryOperation(lhs, OperatorType.MULTIPLY, rhs) ;
		case DIVIDE:
			// determine type here
			if ( binaryNode.leftOperand() instanceof Ast.Literal && Ast.Literal.LiteralType.INT_LITERAL == ((Ast.Literal)binaryNode.leftOperand()).literalType() )
				return new BinaryOperation(lhs, OperatorType.DIVIDE_INT, rhs) ;
			else if ( binaryNode.rightOperand() instanceof Ast.Literal && Ast.Literal.LiteralType.INT_LITERAL == ((Ast.Literal)binaryNode.rightOperand()).literalType() )
				return new BinaryOperation(lhs, OperatorType.DIVIDE_INT, rhs) ;
			else
				return new BinaryOperation(lhs, OperatorType.DIVIDE, rhs) ;
		case EQUAL_TO:
			return new BinaryOperation(lhs, OperatorType.EQUALS, rhs) ;
		case LESS_THAN:
			return new BinaryOperation(lhs, OperatorType.LESS_THAN, rhs) ;
		case GREATER_THAN:
			return new BinaryOperation(lhs, OperatorType.GREATER_THAN, rhs) ;
			
		case NOT_EQUAL_TO:
			return new UnaryOperation(OperatorType.NOT, new BinaryOperation(lhs, OperatorType.EQUALS, rhs) ) ;
		case GREATER_THAN_EQUAL:
			return new UnaryOperation(OperatorType.NOT, new BinaryOperation(lhs, OperatorType.LESS_THAN, rhs) ) ;
		case LESS_THAN_EQUAL:
			return new UnaryOperation(OperatorType.NOT, new BinaryOperation(lhs, OperatorType.GREATER_THAN, rhs) ) ;
			
		case MODULO:
			return new UnaryOperation(OperatorType.NOT, new BinaryOperation(lhs, OperatorType.MODULO, rhs) ) ;
		default:
			report(RoutineTools.ReportType.ERROR, "does not support " + binaryNode.operationType(), binaryNode);
			return InvalidExpression.instance("unrecognized binary operation " + binaryNode.operationType() ) ;
		}
	}
}