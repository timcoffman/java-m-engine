package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.Objects;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;


/**
 * permitted as an argument passed to the $SELECT builtin function
 */
public class ConditionalExpression extends Expression {

	private static final long serialVersionUID = 1L;
	private final Expression m_condition ;
	private final Expression m_expression ;
	
	public ConditionalExpression(Expression condition, Expression expression) {
		Objects.requireNonNull(condition) ;
		Objects.requireNonNull(expression) ;
		m_condition = condition;
		m_expression = expression;
	}

	public Expression condition() { return m_condition ; }
	public Expression expression() { return m_expression ; }
	
	@Override public String unformattedRepresentation() { return "(? " + m_condition.unformattedRepresentation() + " ?) " + m_expression.unformattedRepresentation() ; }

	@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitConditional(this); }

	@Override
	public Expression inverted() { return new ConditionalExpression(m_condition, m_expression.inverted() ) ; }

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write( this ) ;
	}

	@Override
	public boolean equals(Object obj) {
		if ( null == obj ) return false ;
		if ( this == obj ) return true ;
		if ( !(obj instanceof ConditionalExpression) ) return false ;
		ConditionalExpression conditional = (ConditionalExpression)obj ;
		return
			m_condition.equals( conditional.m_condition )
			&& m_expression.equals( conditional.m_expression )
			;
	}

}
