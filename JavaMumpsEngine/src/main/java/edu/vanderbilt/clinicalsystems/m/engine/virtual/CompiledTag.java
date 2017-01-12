package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.util.List;

import edu.vanderbilt.clinicalsystems.m.engine.virtual.Executor.ExecutionResult;

public interface CompiledTag {
	
	CompiledRoutine compiledRoutine();
	
	String name() ;
	List<String> parameterNames();
	
	ExecutionResult execute( ExecutionFrame frame, List<EvaluationResult> arguments ) ;
}
