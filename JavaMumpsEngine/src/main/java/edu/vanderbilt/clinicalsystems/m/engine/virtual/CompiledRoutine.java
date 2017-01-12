package edu.vanderbilt.clinicalsystems.m.engine.virtual;

public interface CompiledRoutine {
	
	String name() ;
	String compiledRepresentation() ;
	
	CompiledTag compiledTag( String tagName, int parameterCount ) ;
}
