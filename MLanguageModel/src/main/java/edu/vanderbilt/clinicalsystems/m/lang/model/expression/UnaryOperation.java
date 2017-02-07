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

	@Override
	public Expression simplified() {
		switch ( operator() ) {
		case NOT:
			return simplifiedLogicalInversion() ;
		default:
			return super.simplified() ;
		}
	}

	private Expression simplifiedLogicalInversion() {
		Expression invertedOperand = m_operand.inverted() ;
		if ( invertedOperand instanceof UnaryOperation && ((UnaryOperation)invertedOperand).operand() == m_operand ) {
			return this ; /* no effect */
		} else {
			return invertedOperand ;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) return true ;
		if ( null == obj ) return false ;
		if ( !(obj instanceof UnaryOperation) ) return false ;
		UnaryOperation operation = (UnaryOperation)obj ;
		return
			super.equals( operation ) &&
			m_operand.equals( operation.m_operand )
			;
	}
	
}
