package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import edu.vanderbilt.clinicalsystems.m.lang.ParameterPassMethod;
import edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class VariableReference extends Expression {
	
	public static final VariableReference DEFAULT_TEMP_VARIABLE
		= new VariableReference(Scope.LOCAL, ReferenceStyle.DIRECT, "%");
	
	private final ReferenceStyle m_referenceStyle ;
	private final Scope m_scope ;
	private final ParameterPassMethod m_parameterPassMethod ;
	private final String m_variableName ;
	private final List<Expression> m_keys = new ArrayList<Expression>();
	
	public VariableReference( Scope scope, ReferenceStyle referenceStyle, String variableName ) {
		this( ParameterPassMethod.BY_VALUE, scope, referenceStyle, variableName ) ;
	}
	
	public VariableReference( ParameterPassMethod parameterPassMethod, Scope scope, ReferenceStyle referenceStyle, String variableName ) {
		m_parameterPassMethod = parameterPassMethod ;
		m_scope = scope ;
		m_referenceStyle = referenceStyle ;
		m_variableName = variableName ;
	}
	
	public VariableReference( Scope scope, ReferenceStyle referenceStyle, String variableName, List<Expression> keys ) {
		this( ParameterPassMethod.BY_VALUE, scope, referenceStyle, variableName, keys ) ;
	}
	
	public VariableReference( ParameterPassMethod parameterPassMethod, Scope scope, ReferenceStyle referenceStyle, String variableName, List<Expression> keys ) {
		m_parameterPassMethod = parameterPassMethod ;
		m_scope = scope ;
		m_referenceStyle = referenceStyle ;
		m_variableName = variableName ;
		m_keys.addAll( keys ) ;
	}
	
	public ParameterPassMethod parameterPassMethod() { return m_parameterPassMethod; }
	public Scope scope() { return m_scope; }
	public ReferenceStyle referenceStyle() { return m_referenceStyle; }
	public String variableName() { return m_variableName; }
	public Iterable<Expression> keys() { return m_keys; }

	@Override
	protected String unformattedRepresentation() {
		return
				m_parameterPassMethod.unformattedRepresentation()
				+
				m_scope.unformattedRepresentation()
				+
				m_referenceStyle.unformattedRepresentation()
				+
				m_variableName
				+
				m_referenceStyle.unformattedRepresentation()
				+
				"(" + StreamSupport.stream(m_keys.spliterator(), false).map(e->e.unformattedRepresentation()).collect(Collectors.joining(", ")) + ")"
				;
	}
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write( this ) ;
	}
}
