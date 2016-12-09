package edu.vanderbilt.clinicalsystems.m.engine.virtual.handler;

import java.util.Iterator;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.ErrorCode;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.CommandHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.ExecutionFrame;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Nothing;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCallList;

public class CallHandler extends CommandHandler {

	public CallHandler( ExecutionFrame frame ) {
		super(frame) ;
	}
	
	@Override protected ExecutionResult handle( Nothing nothing, Block block ) throws EngineException {
		return execute( block.elements().iterator() ) ;
	}

	@Override protected ExecutionResult handle( TaggedRoutineCallList taggedRoutineCallList, Block block ) throws EngineException {
		ExecutionResult result = ExecutionResult.CONTINUE;
		for (TaggedRoutineCall taggedRoutineCall : taggedRoutineCallList.elements()) {
			result = execute( taggedRoutineCall ) ;
			if ( ExecutionResult.CONTINUE != result )
				break ;
		}
		return result ;
	}

	private ExecutionResult execute( TaggedRoutineCall taggedRoutineCall ) throws EngineException {
		String tagName = taggedRoutineCall.tagReference().tagName();
		String routineName = taggedRoutineCall.tagReference().routineName();
		
		Routine routine = frame().globalContext().compiledRoutine( routineName ) ;
		if ( null == routine )
			throw new EngineException(ErrorCode.MISSING_ROUTINE,"routine",routineName) ;
		
		Iterator<RoutineElement> elementIterator = routine.findTagByName( tagName == null ? routineName : tagName ) ;
		if ( !elementIterator.hasNext() )
			throw new EngineException(ErrorCode.MISSING_TAG,"tag",tagName,"routine",routineName) ;

		return execute( elementIterator ) ;
	}

	private ExecutionResult execute( Iterator<RoutineElement> elementIterator ) throws EngineException {
		try ( ExecutionFrame frame = frame().createChildFrame() ) {
			return executeElementsIn(elementIterator,frame) ;
		}
	}

}
