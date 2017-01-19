package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public interface Installer {

	public interface TargetInstanceResolver {
		Object resolve( Class<?> type, String routineName, ExecutionFrame frame ) ;
	}
	
	void install( Routine routine, TargetInstanceResolver targetInstanceResolver ) throws RoutineWriterException ;
	void install( Class<?> type, TargetInstanceResolver targetInstanceResolver, String routineName ) throws RoutineWriterException ;
	
	default void install( Routine routine ) throws RoutineWriterException { install( routine, (t,r,f)->null ) ; }
	default void install( Class<?> type, String routineName ) throws RoutineWriterException  { install( type, (t,r,f)->null, routineName ) ; }
	
	CompiledRoutine lookup( String routineName );

}
