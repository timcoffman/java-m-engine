package edu.vanderbilt.clinicalsystems.m.engine.virtual.handler;

import java.util.Iterator;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.ErrorCode;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.CommandHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.ExecutionFrame;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Nothing;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCall;

public class CallHandler extends CommandHandler {

	public CallHandler( ExecutionFrame frame ) {
		super(frame) ;
	}
	
	@Override protected ExecutionResult handle( Nothing nothing, Block block ) throws EngineException {
		return execute( block.elements().iterator() ) ;
	}

	@Override protected ExecutionResult handle( TaggedRoutineCall taggedRoutineCall, Block block ) throws EngineException {
		return execute( taggedRoutineCall ) ;
	}

	private ExecutionResult execute( TaggedRoutineCall taggedRoutineCall ) throws EngineException {
		String tagName = taggedRoutineCall.tagName();
		String routineName = taggedRoutineCall.routineName();
		
		Routine routine = frame().globalContext().compiledRoutine( routineName ) ;
		if ( null == routine )
			throw new EngineException(ErrorCode.MISSING_ROUTINE,"routine",routineName) ;
		
		Iterator<RoutineElement> elementIterator = routine.findTagByName( tagName == null ? routineName : tagName ) ;
		if ( !elementIterator.hasNext() )
			throw new EngineException(ErrorCode.MISSING_TAG,"tag",tagName,"routine",routineName) ;

		return execute( elementIterator ) ;
	}

	private ExecutionResult execute( Iterator<RoutineElement> elementIterator ) throws EngineException {
		try ( ExecutionFrame frame = frame().createFrame() ) {
		
			ExecutionResult result = ExecutionResult.CONTINUE ;
			while ( ExecutionResult.CONTINUE == result && elementIterator.hasNext() ) {
				RoutineElement element = elementIterator.next() ;
				if ( element instanceof Command )
					result = delegateExecutionTo( (Command)element, frame ) ;
			}
			
			return result ;
		}
	}

}
