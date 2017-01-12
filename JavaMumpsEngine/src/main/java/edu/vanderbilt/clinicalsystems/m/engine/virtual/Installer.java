package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public interface Installer {

	void install( Routine routine ) throws RoutineWriterException ;
	void install( Class<?> type, String routineName ) throws RoutineWriterException ;
	CompiledRoutine lookup( String routineName );

}
