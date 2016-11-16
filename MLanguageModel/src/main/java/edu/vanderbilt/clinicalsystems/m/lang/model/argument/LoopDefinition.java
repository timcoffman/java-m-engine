package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;


public class LoopDefinition extends Argument {

	private final VariableReference m_destination ;
	private final Expression m_start ;
	private final Expression m_step ;
	private final Expression m_stop ;
	
	public LoopDefinition( VariableReference destination, Expression start, Expression stop, Expression step ) {
		m_destination = destination ;
		m_start = start ;
		m_stop = stop ;
		m_step = step ;
	}

	public VariableReference destination() { return m_destination ; }
	public Expression start() { return m_start ; }
	public Expression step() { return m_step ; }
	public Expression stop() { return m_stop ; }
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	} ;	

	@Override
	protected String unformattedRepresentation() {
		return "from " + m_start + " by " + m_step + " to " + (null == m_stop ? "inf" : m_stop);
	}

}
