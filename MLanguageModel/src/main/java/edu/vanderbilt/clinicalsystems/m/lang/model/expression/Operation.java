package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.Objects;

import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;

public abstract class Operation extends Expression {
	private final OperatorType m_operator ;
	
	public Operation( OperatorType operator ) {
		Objects.requireNonNull(operator) ;
		m_operator = operator ;
	}

	public OperatorType operator() { return m_operator; }
}
