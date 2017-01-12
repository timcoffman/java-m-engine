package edu.vanderbilt.clinicalsystems.m.engine.virtual;


public interface GlobalContext {

	NodeMap root() ;
	
	CompiledRoutine compiledRoutine( String routineName ) ;

}
