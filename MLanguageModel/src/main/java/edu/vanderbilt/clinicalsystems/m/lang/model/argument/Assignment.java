package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.vanderbilt.clinicalsystems.m.lang.model.Element;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;


public class Assignment implements Element {

	private static final long serialVersionUID = 1L;
	private final List<Destination<?>> m_destinations = new ArrayList<Destination<?>>() ;
	private final Expression m_source ;
	
	public Assignment( Destination<?> destination, Expression source ) {
		this( Arrays.asList(destination), source ) ;
	}
	
	public Assignment( List<? extends Destination<?>> destinations, Expression source ) {
		Objects.requireNonNull(destinations) ;
		destinations.forEach( Objects::requireNonNull );
		Objects.requireNonNull(source) ;
		m_destinations.addAll( destinations ) ;
		m_source = source ;
	}

	public List<Destination<?>> destinations() { return m_destinations ; }
	public Expression source() { return m_source ; }
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	} ;	

	@Override
	public String toString() {
		if ( m_destinations.size() == 1 )
			return m_destinations.get(0) + " := " + m_source ;
		else
			return m_destinations.stream().map(Object::toString).collect(Collectors.joining(", ", "( ", " )")) + " := " + m_source ;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) return true ;
		if ( null == obj ) return false ;
		if ( !(obj instanceof Assignment) ) return false ;
		Assignment assignment = (Assignment)obj ;
		return
			m_destinations.equals( assignment.m_destinations )
			&& m_source.equals( assignment.m_source )
			;
	}

}
