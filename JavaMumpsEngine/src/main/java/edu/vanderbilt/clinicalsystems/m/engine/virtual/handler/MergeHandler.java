package edu.vanderbilt.clinicalsystems.m.engine.virtual.handler;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.CommandHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.ExecutionFrame;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.Node;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Element;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Assignment;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Destination;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;

public class MergeHandler extends CommandHandler {

	public MergeHandler( ExecutionFrame frame ) {
		super(frame) ;
	}
	
	@Override protected ExecutionResult handle( AssignmentList assignmentList, Block block ) {
		return apply( assignmentList ) ;
	}

	private ExecutionResult apply( AssignmentList assignments ) {
		ExecutionResult result = ExecutionResult.CONTINUE ; 
		for (Assignment assignment : assignments.elements())
			if ( (result=apply( assignment )) != ExecutionResult.CONTINUE )
				break ;
		return result ;
	}
	
	private ExecutionResult apply( Assignment assignment ) {
		return assignment.destination().visit( new Destination.Visitor<ExecutionResult>() {
			
			@Override public ExecutionResult visitElement(Element element) {
				throw new UnsupportedOperationException(element.getClass().getSimpleName() + " not supported for merge") ;
			}
			
			@Override public ExecutionResult visitVariableReference(VariableReference variable) {
				try { return merge( frame().findNode( variable ), assignment.source() ) ; }
				catch ( EngineException ex ) { return caughtError(ex) ; }
			}

			@Override public ExecutionResult visitBuiltinFunctionCall(BuiltinFunctionCall builtinFunctionCall) {
				throw new UnsupportedOperationException(builtinFunctionCall.getClass().getSimpleName() + " not supported for merge") ;
			}
			
		});
	}
	
	private ExecutionResult merge( Node dstNode, Expression source ) {
		return source.visit( new Expression.Visitor<ExecutionResult>() {
			
			@Override
			public ExecutionResult visitExpression(Expression expression) {
				throw new UnsupportedOperationException(expression.getClass().getSimpleName() + " not supported for merge") ;
			}

			@Override
			public ExecutionResult visitVariableReference( VariableReference variable ) {
				try { return merge( dstNode, frame().findNode( variable ) ) ; }
				catch ( EngineException ex ) { return caughtError(ex) ; }
			}
			
		}) ;
	}

	private ExecutionResult merge( Node dstNode, Node srcNode ) {
		dstNode.merge( srcNode ) ;
		return ExecutionResult.CONTINUE ;
	}
	
}
