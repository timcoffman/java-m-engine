package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;



public abstract class Generator<E,T> extends Builder {

	protected Generator( RoutineTools builderTools ) { super(builderTools) ; }
	
	public interface Listener {
		enum Location { BEFORE_EXPRESSION, AFTER_EXPRESSION } ; 
		void generateSideEffect( Location location, RoutineElement element ) ;
	}
	
	public abstract E generate( T source, Listener listener ) ;
}
