package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;

public interface GlobalContext {

	NodeMap root() ;
	
	Routine compiledRoutine( String routineName ) ;

}
