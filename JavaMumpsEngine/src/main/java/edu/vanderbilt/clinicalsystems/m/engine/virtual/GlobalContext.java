package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;

public interface GlobalContext {

	Routine compiledRoutine( String routineName ) ;
	
}
