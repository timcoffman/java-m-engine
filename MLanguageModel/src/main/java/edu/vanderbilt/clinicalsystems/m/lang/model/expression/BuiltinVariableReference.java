package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.BuiltinVariable;
import edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class BuiltinVariableReference extends VariableReference {

	private final BuiltinVariable m_builtinVariable ;

	public BuiltinVariableReference( BuiltinVariable builtinVariable ) {
		super( ReferenceStyle.DIRECT ) ;
		m_builtinVariable = builtinVariable ;
	}

	public BuiltinVariableReference( BuiltinVariable builtinVariable, List<Expression> keys ) {
		super( ReferenceStyle.DIRECT, keys ) ;
		m_builtinVariable = builtinVariable ;
	}
	
	public BuiltinVariable builtinVariable() { return m_builtinVariable; }

	@Override protected String unformattedVariableNameRepresentation() {
		return m_builtinVariable.canonicalSymbol() ;
	}

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write( this ) ;
	}
}
