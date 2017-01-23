package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import java.util.Arrays;

import javax.lang.model.type.TypeMirror;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.Generator.Listener;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.MethodResolution;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.ReportType;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeFunction;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.MultilineBlock;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Assignment;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.DeclarationList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Destination;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BinaryOperation;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;

public class IterativeLoopAssembler extends FlowAssembler<Ast.EnhancedForLoop>{
	
	public IterativeLoopAssembler( RoutineTools routineTools ) { super(routineTools) ; }

	/*
	 * for ( String <key> : <value-producer>.keys() )
	 *   <statement>
	 */

	
	/*
	 * CMD: NEW | <key>
	 * CMD: FOR | (-) {
	 *   CMD: SET | <key>=$O(<value-producer>(key))
	 *   CMD: QUIT:<key>="" | (-)
	 *   <statement>
	 * }
	 */

	@Override
	public void assemble(Ast.EnhancedForLoop enhancedForLoopNode, Block block, Listener delegate) {
		Ast.Variable variable = enhancedForLoopNode.variable().variables().get(0) ;
		TypeMirror typeMirror = variable.type() ;
		String variableName = variable.name().toString() ;
		Expression expr = enhancedForLoopNode.expression().accept( new Ast.Interpreter<Expression,Listener>(tools()) {

			@Override public Expression visitExpression(Ast.Expression expressionNode, Listener delegate) {
				return tools().expressions().generate( expressionNode, delegate ) ;
			}

			@Override public Expression visitMethodInvocation( Ast.MethodInvocation methodInvocationNode, Listener delegate) {
				MethodResolution methodResolution = tools().resolveMethodInvocationTarget( methodInvocationNode ) ;
				NativeFunction nativeFunctionAnnotation = methodResolution.declaration().getAnnotation( NativeFunction.class ) ;
				if ( null == nativeFunctionAnnotation ) {
					report(ReportType.ERROR, "for loop can only iterate over annotated value methods", methodInvocationNode ) ;
				} else {
					switch ( nativeFunctionAnnotation.value() ) {
						case VALUE_KEYS_ITERATION:
							/* good */
							break ;
						default:
							report(ReportType.ERROR, "for loop can only iterate value methods annotated for iteration", methodInvocationNode ) ;
					}
				}
				Expression target = methodInvocationNode.methodSelect().accept( new Ast.Interpreter<Expression,Listener>(tools()) {

					@Override
					public Expression visitMemberSelect( Ast.MemberSelect memberSelectNode, Listener parameter) {
						return tools().expressions().generate( memberSelectNode.expression(), delegate ) ;
					}
					
				}, delegate);
				return target ;
			}

		}, delegate) ;
		VariableReference keySource = (VariableReference)expr ;
		
		try ( BlockManager blockManager = new BlockManager(block, delegate) ) {
	
			DirectVariableReference loopVar = new DirectVariableReference(Scope.LOCAL, variableName ) ;
			blockManager.appendElement( new Command( CommandType.NEW, new DeclarationList(loopVar) ) ) ;
				
			Block bodyBlock = tools().blocks().generate( enhancedForLoopNode.statement(), delegate ) ;
			
			Block iterationBlock = new MultilineBlock() ;
			try ( BlockManager iterationBlockManager = new BlockManager(iterationBlock, delegate) ) {
				BuiltinFunctionCall nextKeyFunctionCall = new BuiltinFunctionCall( BuiltinFunction.ORDER, Arrays.asList( keySource.child(loopVar) ) ) ;
				iterationBlockManager.appendElement( new Command( CommandType.SET, new AssignmentList( new Assignment( Destination.wrap(loopVar), nextKeyFunctionCall ) ) ));
				
				Expression validKeyCondition = new BinaryOperation( loopVar, OperatorType.EQUALS, Constant.NULL ) ;  
				iterationBlockManager.appendElement( new Command( validKeyCondition, CommandType.QUIT, Argument.NOTHING ) ) ;
			}
			bodyBlock.prependElements(iterationBlock);

			Block inlineBlock = wrapInsideInlineBlock( bodyBlock ) ;
			blockManager.appendElement( new Command( CommandType.FOR, Argument.NOTHING, inlineBlock ) ) ;
		}
	}
	
}
