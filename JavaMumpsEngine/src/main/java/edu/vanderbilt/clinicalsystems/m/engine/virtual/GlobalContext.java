package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import edu.vanderbilt.clinicalsystems.m.lang.Scope;


public interface GlobalContext {

	NodeMap root( Scope scope ) ;
	
	CompiledRoutine compiledRoutine( String routineName ) ;


}
