package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import java.util.Objects;

import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class OutputExpression extends InputOutput {

	private final Expression m_expression ;
	
	public OutputExpression( Expression expression ) {
		Objects.requireNonNull(expression) ;
		m_expression = expression ;
	}
	
	public Expression expression() { return m_expression ; }
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	}

	@Override public String toString() { return m_expression.toString() ; } 
	
}