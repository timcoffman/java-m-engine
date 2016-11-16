package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import edu.vanderbilt.clinicalsystems.m.lang.model.Element;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;


public class Assignment implements Element {

	private final VariableReference m_destination ;
	private final Expression m_source ;
	
	public Assignment( VariableReference destination, Expression source ) {
		m_destination = destination ;
		m_source = source ;
	}

	public VariableReference destination() { return m_destination ; }
	public Expression source() { return m_source ; }
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	} ;	

	@Override
	public String toString() { return m_destination + " := " + m_source ; }

}
