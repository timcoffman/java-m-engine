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
	
	@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitOutputExpression(this) ; }

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	}

	@Override public String toString() { return m_expression.toString() ; } 
	
	@Override public boolean equals( Object obj ) {
		if ( null == obj ) return false ;
		if ( this == obj ) return true ;
		if ( !(obj instanceof OutputExpression) ) return false ;
		OutputExpression outputExpression = (OutputExpression)obj ;
		return m_expression.equals( outputExpression.m_expression ) ;
	}

}
