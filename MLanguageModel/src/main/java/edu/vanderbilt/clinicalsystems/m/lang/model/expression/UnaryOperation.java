package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.Objects;

import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class UnaryOperation extends Operation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Expression m_operand ;
	
	public UnaryOperation( OperatorType operator, Expression operand ) {
		super(operator) ;
		Objects.requireNonNull(operand) ;
		m_operand = operand ;
	}
	
	public Expression operand() { return m_operand; }

	@Override
	protected String unformattedRepresentation() { return "( " + operator().canonicalSymbol() + " " + m_operand.unformattedRepresentation() + " )" ; }
	
	@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitUnaryOperation(this) ; }

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write( this ) ;
	}
	
	@Override
	public Expression inverted() {
		if ( operator() == OperatorType.NOT )
			return m_operand ;
		else
			return super.inverted() ;
	}
	
}
