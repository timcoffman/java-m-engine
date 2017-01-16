package edu.vanderbilt.clinicalsystems.m.engine.virtual.handler;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.ErrorCode;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.CommandHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.ExecutionFrame;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.Node;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.LoopDefinition;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Nothing;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;

public class LoopHandler extends CommandHandler {

	public LoopHandler( ExecutionFrame frame ) {
		super(frame) ;
	}
	
	@Override protected ExecutionResult handle( LoopDefinition loopDefinition, Block block ) throws EngineException {
		final Constant start = evaluate(loopDefinition.start()).toConstant() ;
		
		Node loopNode = frame().findNode( loopDefinition.destination() ) ;
		loopNode.assign( start.value() );
		
		if ( null == loopDefinition.step() ) {
			
			/* perform once */
			executeElementsIn(block.elements().iterator(), frame()) ; /* ignore return */
			return ExecutionResult.CONTINUE ;
		}
		
		final double loopVarIncrement = evaluate(loopDefinition.step()).toConstant().toDouble() ;
		double loopVarValue = Constant.from(loopNode.value()).toDouble();
			
		if ( null == loopDefinition.stop() ) {
				/* perform with variable updates */
				validateBlock(block);
				
				ExecutionResult result = ExecutionResult.CONTINUE ;
				while ( ExecutionResult.CONTINUE == result ) {
					result = executeElementsIn(block.elements().iterator(), frame()) ;
					if ( ExecutionResult.CONTINUE == result ) {
						loopVarValue += loopVarIncrement;
						loopNode.assign( Constant.from( loopVarValue ).value() );
					}
				}

				return ExecutionResult.CONTINUE ;
		}
		
		final double loopVarLimit = evaluate(loopDefinition.stop()).toConstant().toDouble() ;
			
		/* perform with variable upates and exit test */

		ExecutionResult result = ExecutionResult.CONTINUE ;
		while ( ExecutionResult.CONTINUE == result && (loopVarIncrement > 0 ? loopVarValue <= loopVarLimit : loopVarValue >= loopVarLimit ) ) {
			result = executeElementsIn(block.elements().iterator(), frame()) ;
			if ( ExecutionResult.CONTINUE == result ) {
				loopVarValue += loopVarIncrement ;
				loopNode.assign( Constant.from( loopVarValue ).value() );
			}
		}

		return ExecutionResult.CONTINUE ;
	}

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
