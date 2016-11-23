package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.Objects;

import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class BinaryOperation extends Operation {
	private final Expression m_leftHandSide ;
	private final Expression m_rightHandSide ;
	
	public BinaryOperation( Expression leftHandSide, OperatorType operator, Expression rightHandSide ) {
		super(operator) ;
		Objects.requireNonNull(leftHandSide) ;
		Objects.requireNonNull(rightHandSide) ;
		m_leftHandSide = leftHandSide ;
		m_rightHandSide = rightHandSide ;
	}
	
	public Expression leftHandSide() { return m_leftHandSide; }
	public Expression rightHandSide() { return m_rightHandSide; }

	@Override
	protected String unformattedRepresentation() { return "( " + m_leftHandSide.unformattedRepresentation() + " " + operator().canonicalSymbol() + " " + m_rightHandSide.unformattedRepresentation() + " )" ; }

	@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitBinaryOperation(this) ; }

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write( this ) ;
	}
	
	@Override
	public Expression inverted() {
		if ( operator() == OperatorType.OR ) {
			return new BinaryOperation(m_leftHandSide.inverted(), OperatorType.AND, m_rightHandSide.inverted()) ;
		} else if ( operator() == OperatorType.AND ) {
			return new BinaryOperation(m_leftHandSide.inverted(), OperatorType.OR, m_rightHandSide.inverted()) ;
		} else {
			return super.inverted() ;
		}
	}
}
