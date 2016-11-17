package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;

public abstract class VariableReference extends Expression {

	public static final DirectVariableReference DEFAULT_TEMP_VARIABLE
		= new DirectVariableReference(Scope.LOCAL, "%");

	private final ReferenceStyle m_referenceStyle ;
	private final List<Expression> m_keys = new ArrayList<Expression>();

	public VariableReference( ReferenceStyle referenceStyle ) {
		m_referenceStyle = referenceStyle ;
	}
	
	public VariableReference( ReferenceStyle referenceStyle, List<Expression> keys ) {
		m_referenceStyle = referenceStyle ;
		m_keys.addAll( keys ) ;
	}
	
	public ReferenceStyle referenceStyle() { return m_referenceStyle; }
	public Iterable<Expression> keys() { return m_keys; }
	
	protected abstract String unformattedVariableNameRepresentation() ;

	@Override
	protected String unformattedRepresentation() {
		return
				m_referenceStyle.unformattedRepresentation()
				+
				unformattedVariableNameRepresentation()
				+
				m_referenceStyle.unformattedRepresentation()
				+
				"(" + StreamSupport.stream(m_keys.spliterator(), false).map(e->e.unformattedRepresentation()).collect(Collectors.joining(", ")) + ")"
				;
	}
	
}
