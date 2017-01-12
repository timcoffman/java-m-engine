package edu.vanderbilt.clinicalsystems.m.engine.virtual.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.ErrorCode;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.CommandHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.CompiledRoutine;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.CompiledTag;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.EvaluationResult;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.ExecutionFrame;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Nothing;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCallList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;

public class CallHandler extends CommandHandler {

	public CallHandler( ExecutionFrame frame ) {
		super(frame) ;
	}
	
	@Override protected ExecutionResult handle( Nothing nothing, Block block ) throws EngineException {
		try ( ExecutionFrame frame = frame().createChildFrame() ) {
			return executeElementsIn( block.elements().iterator(), frame) ;
		}
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
		
		CompiledRoutine compiledRoutine = frame().globalContext().compiledRoutine( routineName ) ;
		if ( null == compiledRoutine )
			throw new EngineException(ErrorCode.MISSING_ROUTINE,"routine",routineName) ;
		
		Long argumentCount = StreamSupport.stream(taggedRoutineCall.arguments().spliterator(),false).collect(Collectors.counting());
		CompiledTag compiledTag = compiledRoutine.compiledTag(tagName, argumentCount.intValue() );
		if ( null == compiledTag )
			throw new EngineException(ErrorCode.MISSING_TAG,"tag",tagName,"routine",routineName) ;

		try ( ExecutionFrame frame = frame().createChildFrame() ) {
			List<EvaluationResult> arguments = new ArrayList<EvaluationResult>() ;
			for ( Expression expression : taggedRoutineCall.arguments() )
				arguments.add( evaluate(expression) ) ;
			
			ExecutionResult result = compiledTag.execute( frame, arguments ) ;
			switch ( result ) {
			case QUIT:
				producedResult(frame.result()) ;
				return ExecutionResult.CONTINUE ;
			case ERROR:
				return caughtError(frame.error());
			default:
				return result ;
			}
		}
	}

}
