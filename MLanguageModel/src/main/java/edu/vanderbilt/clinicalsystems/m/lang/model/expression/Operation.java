package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.Objects;

import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;

public abstract class Operation extends Expression {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final OperatorType m_operator ;
	
	public Operation( OperatorType operator ) {
		Objects.requireNonNull(operator) ;
		m_operator = operator ;
	}

	public OperatorType operator() { return m_operator; }

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) return true ;
		if ( null == obj ) return false ;
		if ( !(obj instanceof Operation) ) return false ;
		Operation operation = (Operation)obj ;
		return
			m_operator.equals( operation.m_operator )
			;
	}
	
}
