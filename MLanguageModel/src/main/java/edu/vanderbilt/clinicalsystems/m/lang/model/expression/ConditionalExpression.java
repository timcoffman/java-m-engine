package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;


/**
 * permitted as an argument passed to the $SELECT builtin function
 */
public class ConditionalExpression extends Expression {

	private final Expression m_condition ;
	private final Expression m_expression ;
	
	public ConditionalExpression(Expression condition, Expression expression) {
		m_condition = condition;
		m_expression = expression;
	}

	public Expression condition() { return m_condition ; }
	public Expression expression() { return m_expression ; }
	
	@Override public String unformattedRepresentation() { return "<" + m_condition.unformattedRepresentation() + ">" + m_expression.unformattedRepresentation() ; }

	@Override
	public Expression inverted() { return new ConditionalExpression(m_condition, m_expression.inverted() ) ; }

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write( this ) ;
	}

}
