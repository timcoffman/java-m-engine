package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import java.util.Objects;

import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;


public class LoopDefinition extends Argument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final DirectVariableReference m_destination ;
	private final Expression m_start ;
	private final Expression m_step ;
	private final Expression m_stop ;
	
	public LoopDefinition( DirectVariableReference destination, Expression start, Expression stop, Expression step ) {
		Objects.requireNonNull( destination ) ;
		Objects.requireNonNull( start ) ;
		Objects.requireNonNull( step ) ;
		m_destination = destination ;
		m_start = start ;
		m_stop = stop ;
		m_step = step ;
	}

	public DirectVariableReference destination() { return m_destination ; }
	public Expression start() { return m_start ; }
	public Expression step() { return m_step ; }
	public Expression stop() { return m_stop ; }

	@Override
	public <R> R visit( Visitor<R> visitor ) {
		return visitor.visitLoopDefinition(this) ;
	}

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	} ;	

	@Override
	protected String unformattedRepresentation() {
		return "from " + m_start + " by " + m_step + " to " + (null == m_stop ? "inf" : m_stop);
	}

}
