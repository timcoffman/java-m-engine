package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;

public class RoutineManager extends RoutineElementsManager {

	private final Routine m_routine ;
	
	public RoutineManager( Routine routine, Generator.Listener delegate ) {
		super(delegate) ;
		m_routine = routine ;
	}
	
	@Override
	protected void applyElement(RoutineElement element) {
		m_routine.appendElement(element);
	}

}
