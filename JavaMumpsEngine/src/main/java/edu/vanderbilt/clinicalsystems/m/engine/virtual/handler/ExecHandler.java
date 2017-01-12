package edu.vanderbilt.clinicalsystems.m.engine.virtual.handler;

import java.util.List;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.CommandHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.ExecutionFrame;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;

public class ExecHandler extends CommandHandler {

	public ExecHandler( ExecutionFrame frame ) {
		super(frame) ;
	}
	
	@Override protected ExecutionResult handle( ExpressionList expressionList, Block block ) throws EngineException {
		for ( Expression expression : expressionList.elements() )
			interpretAndExecute( evaluate( expression ).toConstant().value() ) ;
		return ExecutionResult.CONTINUE ;
	}

	private ExecutionResult interpretAndExecute( String unparsedCode ) throws EngineException {
		return interpretAndExecute( frame().interpretCommands( unparsedCode ) ) ;
	}
	
	private ExecutionResult interpretAndExecute( List<? extends Command> commands ) throws EngineException {
		try ( ExecutionFrame frame = frame().createChildFrame() ) {
		
			ExecutionResult result = ExecutionResult.CONTINUE ;
			for ( Command command : commands ) {
				result = delegateExecutionTo( command, frame ) ;
			}
			
			return result ;
		}
	}

}
