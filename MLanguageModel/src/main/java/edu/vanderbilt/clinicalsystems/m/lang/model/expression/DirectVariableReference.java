package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.ParameterPassMethod;
import edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class DirectVariableReference extends VariableReference {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ParameterPassMethod m_parameterPassMethod ;
	private final Scope m_scope ;
	private final String m_variableName ;

	public DirectVariableReference( Scope scope, String variableName ) {
		this(ParameterPassMethod.BY_VALUE, scope, variableName ) ;
	}
	
	public DirectVariableReference( ParameterPassMethod parameterPassMethod, Scope scope, String variableName ) {
		super( ReferenceStyle.DIRECT ) ;
		m_parameterPassMethod = parameterPassMethod ;
		m_scope = scope ;
		m_variableName = variableName ;
	}
	
	public DirectVariableReference( Scope scope, String variableName, List<Expression> keys ) {
		this( ParameterPassMethod.BY_VALUE, scope, variableName, keys ) ;
	}
	
	public DirectVariableReference( ParameterPassMethod parameterPassMethod, Scope scope, String variableName, List<Expression> keys ) {
		super( ReferenceStyle.DIRECT, keys ) ;
		m_parameterPassMethod = parameterPassMethod ;
		m_scope = scope ;
		m_variableName = variableName ;
	}

	@Override
	protected DirectVariableReference copyWithKeys( List<Expression> keys ) {
		return new DirectVariableReference( m_parameterPassMethod, m_scope, m_variableName, keys ) ;
	}
	
	public ParameterPassMethod parameterPassMethod() { return m_parameterPassMethod; }
	public Scope scope() { return m_scope; }
	public String variableName() { return m_variableName; }

	@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitDirectVariableReference(this); }

	@Override protected String unformattedVariableNameRepresentation() {
		return
				m_parameterPassMethod.unformattedRepresentation()
				+
				m_scope.unformattedRepresentation()
				+
				m_variableName 
				;
	}

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write( this ) ;
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) return true ;
		if ( !super.equals(this)) return false ;
		if ( !(obj instanceof DirectVariableReference) ) return false ;
		DirectVariableReference variable = (DirectVariableReference)obj ;
		return
			m_variableName.equals( variable.m_variableName )
			&& m_parameterPassMethod.equals( variable.m_parameterPassMethod )
			&& m_scope.equals( variable.m_scope )
			;
	}

}
