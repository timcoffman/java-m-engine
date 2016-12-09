package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import java.util.Objects;

import edu.vanderbilt.clinicalsystems.m.lang.model.Element;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;


public class Assignment implements Element {

	private static final long serialVersionUID = 1L;
	private final Destination<?> m_destination ;
	private final Expression m_source ;
	
	public Assignment( Destination<?> destination, Expression source ) {
		Objects.requireNonNull(destination) ;
		Objects.requireNonNull(source) ;
		m_destination = destination ;
		m_source = source ;
	}

	public Destination<?> destination() { return m_destination ; }
	public Expression source() { return m_source ; }
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	} ;	

	@Override
	public String toString() { return m_destination + " := " + m_source ; }

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) return true ;
		if ( null == obj ) return false ;
		if ( !(obj instanceof Assignment) ) return false ;
		Assignment assignment = (Assignment)obj ;
		return
			m_destination.equals( assignment.m_destination )
			&& m_source.equals( assignment.m_source )
			;
	}

}
