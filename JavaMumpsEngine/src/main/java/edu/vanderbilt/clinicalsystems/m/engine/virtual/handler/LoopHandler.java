package edu.vanderbilt.clinicalsystems.m.engine.virtual.handler;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.ErrorCode;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.CommandHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.ExecutionFrame;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Nothing;

public class LoopHandler extends CommandHandler {

	public LoopHandler( ExecutionFrame frame ) {
		super(frame) ;
	}
	
//	@Override protected ExecutionResult handle( LoopDefinition loopDefinition, Block block ) throws EngineException {
//		ExecutionResult result = executeElementsIn(block.elements().iterator(), frame()) ;
//		return ExecutionResult.CONTINUE ;
//	}

	@Override protected ExecutionResult handle( Nothing nothing, Block block ) throws EngineException {
		validateBlock( block ) ;
		ExecutionResult result = ExecutionResult.CONTINUE;
		while ( ExecutionResult.CONTINUE == result )
			result = executeElementsIn(block.elements().iterator(), frame()) ;
		if ( ExecutionResult.QUIT == result )
			return ExecutionResult.CONTINUE ;
		return result ;
	}

	private void validateBlock(Block block) throws EngineException {
		for ( RoutineElement element : block.elements() ) {
			if ( element instanceof Command )
				if ( ((Command)element).commandType() == CommandType.QUIT )
					return ;
		}
		throw new EngineException(ErrorCode.INFINITE_LOOP) ;
		
	}
	
}
