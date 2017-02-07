package edu.vanderbilt.clinicalsystems.m.engine.virtual.handler;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.CommandHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.EvaluationResult;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.ExecutionFrame;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Element;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Assignment;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Destination;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;

public class AssignmentHandler extends CommandHandler {

	public AssignmentHandler( ExecutionFrame frame ) {
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
		EvaluationResult source ;
		try { source = frame().evaluate( assignment.source() ) ; }
		catch ( EngineException ex ) { return caughtError(ex) ; }
		
		ExecutionResult result = ExecutionResult.CONTINUE ;
		for ( Destination<?> destination : assignment.destinations() ) {
			result = destination.visit( new Destination.Visitor<ExecutionResult>() {
				
				@Override public ExecutionResult visitElement(Element element) {
					throw new UnsupportedOperationException(element.getClass().getSimpleName() + " not supported for assignment") ;
				}
				
				@Override public ExecutionResult visitVariableReference(VariableReference variable) {
					try { frame().findNode( variable ).assign( source.toConstant().value() ) ; return ExecutionResult.CONTINUE ; }
					catch ( EngineException ex ) { return caughtError(ex) ; }
				}
	
				@Override public ExecutionResult visitBuiltinFunctionCall(BuiltinFunctionCall builtinFunctionCall) {
					throw new UnsupportedOperationException(builtinFunctionCall.getClass().getSimpleName() + " not supported for assignment") ;
				}
				
			});
			if ( result != ExecutionResult.CONTINUE )
				break ;
		}
		return result ;
		
	}
	

}
