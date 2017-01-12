package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.Ast.DoWhileLoop;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.Ast.EmptyStatement;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.MethodResolution;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeCommand;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.MultilineBlock;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Assignment;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.DeclarationList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Destination;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCallList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BinaryOperation;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.FunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.TagReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;

public class BlockGenerator extends Generator<Block,Ast.Statement> {
	
	private final ConditionalFlowAssembler m_conditionalFlowAssembler;
	private final IncrementalLoopAssembler m_incrementalLoopAssembler;
	private final ConditionalLoopAssembler m_conditionalLoopAssembler ;
	private final PostConditionalLoopAssembler m_postConditionalLoopAssembler ;

	public BlockGenerator(RoutineTools builderTools) {
		super( builderTools ) ;
		m_conditionalFlowAssembler = new ConditionalFlowAssembler( builderTools );
		m_incrementalLoopAssembler = new IncrementalLoopAssembler( builderTools );
		m_conditionalLoopAssembler = new ConditionalLoopAssembler( builderTools );
		m_postConditionalLoopAssembler = new PostConditionalLoopAssembler( builderTools );
	}
	
	protected BlockGenerator createInstance() {
		return null ;
	}
	
	@Override
	public Block generate(Ast.Statement statement, Listener listener) {
//		if ( null != listener )
//			throw new IllegalArgumentException( "listener must be null; " + this.getClass().getSimpleName() + " never generates side-effects and ignores listener" ) ;
		Block block = new MultilineBlock() ;
		if ( statement instanceof Ast.Block ) {
			for ( Ast.Statement s : ((Ast.Block)statement).statements() ) {
				
				s.accept( new StatementInterpreter(listener), block ) ;
				
			}
		} else {
			statement.accept( new StatementInterpreter(listener), block) ;
		}
		
		return block ;
	}
	
	private final class StatementInterpreter extends Ast.Interpreter<Void, Block> {

		private final Generator.Listener m_delegate ;
		
		public StatementInterpreter(Generator.Listener delegate) {
			super(tools()) ;
			Objects.requireNonNull(delegate) ;
			m_delegate = delegate ;
		}
		
		@Override protected Void unsupportedNodeTypeResult(Ast.Node node, Block block) {
			report(RoutineTools.ReportType.ERROR, node.getClass().getSimpleName() + " not supported yet", node);
			return null ;
		}
		
		@Override
		public Void visitExpression(Ast.Expression expression, Block block) {
			try ( BlockManager blockManager = new BlockManager(block, m_delegate) ) {
				tools().expressions().generate( expression, blockManager ) ;
				// only side-effects
			}
			return null;
		}

		@Override
		public Void visitEmptyStatement(EmptyStatement emptyStatementNode, Block parameter) {
			/* fine, nothing to do */
			return null ;
		}

		@Override
		public Void visitReturn(Ast.Return returnNode, Block block) {
			try ( BlockManager blockManager = new BlockManager(block, m_delegate) ) {
				blockManager.appendElement( new Command( CommandType.QUIT, new ExpressionList( tools().expressions().generate(returnNode.expression(),blockManager) ) ) );
			}
			return null ;
		}

		
		@Override
		public Void visitUnary(Ast.Unary unaryNode, Block block) {
			try ( BlockManager blockManager = new BlockManager(block, m_delegate) ) {
				DirectVariableReference variableReference = (DirectVariableReference)tools().expressions().generate( unaryNode.expression(), blockManager ) ;
				Expression source ;
				switch ( unaryNode.operationType() ) {
				case PREFIX_INCREMENT:
				case POSTFIX_INCREMENT:
					source = new BinaryOperation(variableReference, OperatorType.ADD, Constant.from("1") );
					break ;
				case PREFIX_DECREMENT:
				case POSTFIX_DECREMENT:
					source = new BinaryOperation(variableReference, OperatorType.SUBTRACT, new Constant("1") );
					break ;
				default:
					return unsupportedNodeTypeResult(unaryNode, block) ;
				}
				blockManager.appendElement( new Command( CommandType.SET, new AssignmentList( new Assignment( Destination.wrap(variableReference), source)) ) );
			}
			return null ;
		}
		
		
		@Override
		public Void visitBinary(Ast.Binary binaryNode, Block block) {
			try ( BlockManager blockManager = new BlockManager(block, m_delegate) ) {
				DirectVariableReference variableReference = (DirectVariableReference)tools().expressions().generate( binaryNode.leftOperand(), blockManager ) ;
				Expression operand = tools().expressions().generate( binaryNode.leftOperand(), blockManager ) ;
				Expression source ;
				switch ( binaryNode.operationType() ) {
				case PLUS_ASSIGNMENT:
					source = new BinaryOperation(variableReference, OperatorType.ADD, operand );
					break ;
				case MINUS_ASSIGNMENT:
					source = new BinaryOperation(variableReference, OperatorType.SUBTRACT, operand );
					break ;
				default:
					return unsupportedNodeTypeResult(binaryNode, block) ;
				}
				blockManager.appendElement( new Command( CommandType.SET, new AssignmentList( new Assignment( Destination.wrap(variableReference), source)) ) );
			}
			return null ;
		}

		@Override
		public Void visitExpressionStatement(Ast.ExpressionStatement expressionStatementNode, Block block) {
			return expressionStatementNode.expression().accept( new Ast.Interpreter<Void,Block>(tools()) {

				@Override
				protected Void unsupportedNodeTypeResult(Ast.Node node,	Block block) {
					return node.accept(StatementInterpreter.this, block) ;
				}
				
				@Override
				public Void visitMethodInvocation(Ast.MethodInvocation methodInvocationNode, Block block) {
					if ( assembleNativeCommand(methodInvocationNode, block, m_delegate) ) {
						/* ok, all done */
						return null ;
					} else {
						try ( BlockManager blockManager = new BlockManager(block, m_delegate) ) {
							FunctionCall functionCall = (FunctionCall)tools().expressions().generate( methodInvocationNode, blockManager ) ;
							if ( FunctionCall.Returning.SOME_VALUE == functionCall.returning() ) {
								blockManager.appendElement( new Command( CommandType.SET, new AssignmentList( new Assignment( Destination.wrap(DirectVariableReference.DEFAULT_TEMP_VARIABLE), functionCall)) ) );
							} else {
								RoutineFunctionCall rfc = (RoutineFunctionCall)functionCall ;
								TagReference tagRef = rfc.tagReference() ;
								List<Expression> arguments = StreamSupport.stream(rfc.arguments().spliterator(), false).collect( Collectors.toList() ) ;
								blockManager.appendElement( new Command( CommandType.DO, new TaggedRoutineCallList( new TaggedRoutineCall( tagRef, arguments ))));
							}
						}
						return null ;
					}
				}
				
			}, block) ;
		}
		
		@Override
		public Void visitVariablesStatement(Ast.VariableDeclarationsStatement variableStatementNode, Block block) {
			variableStatementNode.variables().forEach( (v)->v.accept(StatementInterpreter.this,block) ); 
			return null ;
		}

		@Override
		public Void visitVariable(Ast.Variable variableNode, Block block) {
			
			TypeMirror type = variableNode.type() ;
			if ( tools().isNativeType(type) ) {
				/* OK */
			} else if ( tools().isNativeWrapperType(type) ) {
				return null ; // helpers like this wrap a NativeType, should be omitted
			} else if ( tools().isTypeOfNumber(type) ) {
				/* OK */
			} else if ( tools().isTypeOfString(type) ) {
				/* OK */
			} else {
				report(RoutineTools.ReportType.WARNING, "a local variable or parameter must be a String, int, double, @NativeType ( e.g. Value ), or @NativeWrapperType", variableNode);
			}

			try ( BlockManager blockManager = new BlockManager(block, m_delegate) ) {
				String variableName = variableNode.name().toString();
				DirectVariableReference variableRef = new DirectVariableReference(Scope.LOCAL, variableName );
				DirectVariableReference[] variables = new DirectVariableReference[] { variableRef } ;
				blockManager.appendElement( new Command( CommandType.NEW, new DeclarationList(variables) ) ) ;
				if ( null != variableNode.initializer() ) {
					Expression initialExpression = tools().expressions().generate(variableNode.initializer(),blockManager);
					if ( initialExpression instanceof Constant && initialExpression.equals("") ) {
						/* initialization to literal empty string can be suppressed in the Epic environment */
					} else {
						blockManager.appendElement( new Command( CommandType.SET, new AssignmentList( new Assignment( Destination.wrap(variableRef), initialExpression ) ) ) ) ;
					}
				}
			}
			return null;
		}
		
		@Override
		public Void visitWhileLoop(Ast.WhileLoop whileLoop, Block block) {
			m_conditionalLoopAssembler.assemble( whileLoop, block, m_delegate ) ;
			return null ;
		}

		@Override
		public Void visitForLoop(Ast.ForLoop forLoop, Block block) {
			m_incrementalLoopAssembler.assemble( forLoop, block, m_delegate ) ;
			return null ;
		}
		
		@Override
		public Void visitDoWhileLoop(DoWhileLoop doLoop, Block block) {
			m_postConditionalLoopAssembler.assemble( doLoop, block, m_delegate ) ;
			return null ;
		}

		@Override
		public Void visitBreak(Ast.Break breakNode, Block block) {
			try ( RoutineElementsManager blockManager = new BlockManager(block, m_delegate) ) {
				blockManager.appendElement( new Command( CommandType.QUIT, Argument.NOTHING ) ) ;
			}
			return null ;
		}

		@Override
		public Void visitIf(Ast.If ifNode, Block block) {
			m_conditionalFlowAssembler.assemble( ifNode, block, m_delegate ) ;
			return null ;
		}

		@Override
		public Void visitBlock(Ast.Block blockNode, Block block) {
			try ( BlockManager blockManager = new BlockManager(block, m_delegate) ) {
				blockManager.appendElements( tools().blocks().generate(blockNode,blockManager) );
			}
			return null ;
		}
		
	}
	
	
	private boolean assembleNativeCommand( Ast.MethodInvocation methodInvocationNode, Block block, Generator.Listener delegate )  {
		MethodResolution methodInvocationTarget = tools().resolveMethodInvocationTarget( methodInvocationNode );
		if ( null == methodInvocationTarget ) {
			report( RoutineTools.ReportType.ERROR, "failed to resolve method", methodInvocationNode ) ;
			return false ;
		}

		NativeCommand nativeCommandAnnotation = methodInvocationTarget.declaration().getAnnotation( NativeCommand.class ) ;
		if ( null != nativeCommandAnnotation ) {
			switch ( nativeCommandAnnotation.value() ) {
			case VALUE_ASSIGN:
				assembleValueAssignment( methodInvocationNode.methodSelect(), methodInvocationNode.arguments().get(0), block, delegate ) ;
				return true ;
			case EXTENSION:
				assembleExtension( (ExecutableElement)methodInvocationTarget, methodInvocationNode, block ) ;
				return true ;
			default:
				report( RoutineTools.ReportType.ERROR, "unrecognized native command", methodInvocationNode ) ;
				return false ;
			}
		}
		return false ;
	}

//	private DirectVariableReference assembleDirectVariableReference( Ast.Expression variableProvidingExpression, BlockManager blockManager ) {
//		DirectVariableReference variableRef = variableProvidingExpression.accept( new Ast.Interpreter<DirectVariableReference, Listener>(tools()) {
//			@Override
//			public DirectVariableReference visitIdentifier(Ast.Identifier identifierNode, Listener listener) {
//				return new DirectVariableReference( Scope.LOCAL, identifierNode.name().toString() ) ;
//			}
//
//			@Override
//			public DirectVariableReference visitMemberSelect( Ast.MemberSelect memberSelectNode, Listener listener) {
//				Expression instance = tools().expressions().generate( memberSelectNode.expression(), listener ) ;
//				MethodResolution methodInvocationTarget = tools().resolveMethodInvocationTarget( memberSelectNode );
//				if ( null == methodInvocationTarget ) {
//					report( RoutineTools.ReportType.ERROR, "failed to resolve method", methodInvocationNode ) ;
//					return null ;
//				}
//				
//				NativeFunction nativeFunctionAnnotation = methodInvocationTarget.declaration().getAnnotation( NativeFunction.class ) ;
//				if ( null == nativeFunctionAnnotation ) {
//					report(RoutineTools.ReportType.ERROR, "assignment requires a variable", methodInvocationNode);
//					return null ;
//				}
//				if ( NativeFunctionType.VALUE_INDEX != nativeFunctionAnnotation.value() ) {
//					report(RoutineTools.ReportType.ERROR, "assignment requires indexing a variable", methodInvocationNode);
//					return null ;
//				}
//				Expression key = tools().expressions().generate( methodInvocationNode.arguments().get(0), blockManager );
//
//				DirectVariableReference variableRef = assembleDirectVariableReference( methodInvocationNode.methodSelect(), listener );
//				return (DirectVariableReference)variableRef.child( key ) ;
//			}
//
//			@Override
//			public DirectVariableReference visitMethodInvocation( Ast.MethodInvocation methodInvocationNode, Listener listener) {
//				MethodResolution methodInvocationTarget = tools().resolveMethodInvocationTarget( methodInvocationNode );
//				if ( null == methodInvocationTarget ) {
//					report( RoutineTools.ReportType.ERROR, "failed to resolve method", methodInvocationNode ) ;
//					return null ;
//				}
//				
//				NativeFunction nativeFunctionAnnotation = methodInvocationTarget.declaration().getAnnotation( NativeFunction.class ) ;
//				if ( null == nativeFunctionAnnotation ) {
//					report(RoutineTools.ReportType.ERROR, "assignment requires a variable", methodInvocationNode);
//					return null ;
//				}
//				if ( NativeFunctionType.VALUE_INDEX != nativeFunctionAnnotation.value() ) {
//					report(RoutineTools.ReportType.ERROR, "assignment requires indexing a variable", methodInvocationNode);
//					return null ;
//				}
//				Expression key = tools().expressions().generate( methodInvocationNode.arguments().get(0), blockManager );
//
//				DirectVariableReference variableRef = assembleDirectVariableReference( methodInvocationNode.methodSelect(), listener );
//				return (DirectVariableReference)variableRef.child( key ) ;
//			}
//		}, blockManager) ;
//		return variableRef ;
//	}
	
	private void assembleValueAssignment(Ast.Expression target, Ast.Expression sourceArgument, Block block, Generator.Listener delegate) {
		try ( BlockManager blockManager = new BlockManager(block, delegate) ) {
			Expression source = tools().expressions().generate( sourceArgument, blockManager );
			
			VariableReference variableRef = target.accept( new Ast.Interpreter<VariableReference, Listener>(tools()) {

				@Override
				public VariableReference visitExpression(Ast.Expression expressionNode, Listener listener) {
					return super.visitExpression(expressionNode, listener);
				}
				
				@Override
				public VariableReference visitMethodInvocation( Ast.MethodInvocation methodInvocationNode, Listener listener) {
					return (VariableReference) tools().expressions().generate( methodInvocationNode, listener ) ;
				}

				@Override
				public VariableReference visitMemberSelect( Ast.MemberSelect memberSelectNode, Listener listener) {
					return (VariableReference)tools().expressions().generate( memberSelectNode.expression(), listener ) ;
				}
				
			}, blockManager ) ;
			blockManager.appendElement( new Command( CommandType.SET, new AssignmentList( new Assignment( Destination.wrap(variableRef), source) ) ) );
			
		}
	}

	private void assembleExtension(ExecutableElement methodInvocationTarget, Ast.MethodInvocation methodInvocationNode, Block block) {
//		Ast.Method tree = trees().getNode( methodInvocationTarget );
//		blockManager.appendElements( tools().blocks().generate( tree.getBody() ) );
	}
}
