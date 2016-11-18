package edu.vanderbilt.clinicalsystems.m.engine.virtual.handler;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.CommandHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.ExecutionFrame;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Nothing;

public class ReturnHandler extends CommandHandler {

	public ReturnHandler( ExecutionFrame frame ) {
		super(frame) ;
	}
	
	@Override protected ExecutionResult handle( Nothing nothing, Block block ) {
		return ExecutionResult.QUIT ;
	}
	
	@Override protected ExecutionResult handle( ExpressionList expressionList, Block block ) throws EngineException {
		return produceResult( evaluate( expressionList.elements() ) ) ;
	}
}
