package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;

public interface Executor {

	public enum ExecutionResult {
		CONTINUE,
		QUIT,
		HALT,
		ERROR
	}
	
	ExecutionResult execute( Command command ) ;
	
	EngineException error() ;
	Constant result();

}
