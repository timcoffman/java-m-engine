package edu.vanderbilt.clinicalsystems.m.engine.virtual.handler;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.CommandHandler;
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
		return assignment.destination().visit( new Destination.Visitor<ExecutionResult>() {
			
			@Override public ExecutionResult visitElement(Element element) {
				throw new UnsupportedOperationException(element.getClass().getSimpleName() + " not supported for assignment") ;
			}
			
			@Override public ExecutionResult visitVariableReference(VariableReference variable) {
				try { frame().findNode( variable ).assign( frame().evaluate( assignment.source() ).value() ) ; return ExecutionResult.CONTINUE ; }
				catch ( EngineException ex ) { return caughtException(ex) ; }
			}

			@Override public ExecutionResult visitBuiltinFunctionCall(BuiltinFunctionCall builtinFunctionCall) {
				throw new UnsupportedOperationException(builtinFunctionCall.getClass().getSimpleName() + " not supported for assignment") ;
			}
			
		});
	}
	

}
