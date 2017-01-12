package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;

public interface Executor {

	public enum ExecutionResult {
		CONTINUE,
		QUIT,
		HALT,
		ERROR
	}
	
	ExecutionResult execute( Command command ) ;
	
	EngineException error() ;
	EvaluationResult result();

	ExecutionResult caughtError(EngineException ex);
	ExecutionResult producedResult(EvaluationResult result);

}
