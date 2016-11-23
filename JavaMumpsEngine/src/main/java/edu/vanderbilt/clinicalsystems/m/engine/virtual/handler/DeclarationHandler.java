package edu.vanderbilt.clinicalsystems.m.engine.virtual.handler;

import edu.vanderbilt.clinicalsystems.m.engine.virtual.CommandHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.ExecutionFrame;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.DeclarationList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;

public class DeclarationHandler extends CommandHandler {

	public DeclarationHandler( ExecutionFrame frame ) {
		super(frame) ;
	}
	
	@Override protected ExecutionResult handle( DeclarationList declarationList, Block block ) {
		return apply( declarationList ) ;
	}

	private ExecutionResult apply( DeclarationList declarations ) {
		ExecutionResult result = ExecutionResult.CONTINUE ; 
		for (DirectVariableReference variable : declarations.elements())
			if ( (result=apply( variable )) != ExecutionResult.CONTINUE )
				break ;
		return result ;
	}
	
	private ExecutionResult apply( DirectVariableReference variable ) {
		frame().createNode( variable ) ;
		return ExecutionResult.CONTINUE ;
	}
	

}
