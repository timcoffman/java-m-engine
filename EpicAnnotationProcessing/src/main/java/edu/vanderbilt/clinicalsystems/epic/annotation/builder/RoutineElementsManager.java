package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.RoutineDependency;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;

public abstract class RoutineElementsManager implements Generator.Listener, AutoCloseable {

	protected final List<RoutineElement> m_sideEffectsBefore = new ArrayList<RoutineElement>();
	protected final List<RoutineElement> m_elements = new ArrayList<RoutineElement>();
	protected final List<RoutineElement> m_sideEffectsAfter = new ArrayList<RoutineElement>();
	
	private final Generator.Listener m_delegate ;
	
	public RoutineElementsManager(Generator.Listener delegate) {
		Objects.requireNonNull(delegate) ;
		m_delegate = delegate ;
	}

	@Override
	public void publishDependency(RoutineDependency dependency) {
		m_delegate.publishDependency(dependency);
	}

	@Override
	public void generateSideEffect(Location location, RoutineElement element) {
		switch ( location ) {
		case BEFORE_EXPRESSION:
			m_sideEffectsBefore.add( element ) ;
			break ;
		case AFTER_EXPRESSION:
			m_sideEffectsAfter.add( element ) ;
			break ;
		}
	}
	
	public void prependElement(RoutineElement element) { m_elements.add(0,element) ; }
	public void prependElements(Block block) { int index = 0 ; for ( RoutineElement element : block.elements() ) m_elements.add( index++, element ) ; }

	public void appendElement(RoutineElement element) { m_elements.add(element) ; }
	public void appendElements(Block block) { for ( RoutineElement element : block.elements() ) m_elements.add( element ) ; }

	@Override
	public void close() {
		for ( RoutineElement element : m_sideEffectsBefore )
			applyElement(element);
		for ( RoutineElement element : m_elements )
			applyElement(element);
		for ( RoutineElement element : m_sideEffectsAfter )
			applyElement(element);
	}
	
	protected abstract void applyElement(RoutineElement element) ;

}