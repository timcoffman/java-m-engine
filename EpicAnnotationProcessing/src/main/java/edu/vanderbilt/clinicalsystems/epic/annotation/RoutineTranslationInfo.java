package edu.vanderbilt.clinicalsystems.epic.annotation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.RoutineDependency;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;

public class RoutineTranslationInfo {
	private final Routine m_routine;
	private final Set<RoutineDependency> m_dependencies = new HashSet<RoutineDependency>();
	
	public RoutineTranslationInfo(Routine routine, Collection<RoutineDependency> dependencies) {
		m_routine = routine;
		m_dependencies.addAll( dependencies ) ;
	}
	public String routineName() { return m_routine.name() ; }
	public Collection<String> routineTagNames() { return m_routine.tagNames() ; }
	@Deprecated public Routine routine() { return m_routine ; }
	public Set<RoutineDependency> dependencies() { return m_dependencies ; }
}