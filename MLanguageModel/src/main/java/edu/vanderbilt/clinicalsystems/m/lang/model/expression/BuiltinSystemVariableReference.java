package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.BuiltinSystemVariable;
import edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class BuiltinSystemVariableReference extends BuiltinVariableReferenceBase {

	private static final long serialVersionUID = 1L;
	private final BuiltinSystemVariable m_builtinVariable ;

	public BuiltinSystemVariableReference( BuiltinSystemVariable builtinVariable ) {
		super( ReferenceStyle.DIRECT ) ;
		m_builtinVariable = builtinVariable ;
	}

	public BuiltinSystemVariableReference( BuiltinSystemVariable builtinVariable, List<Expression> keys ) {
		super( ReferenceStyle.DIRECT, keys ) ;
		m_builtinVariable = builtinVariable ;
	}
	
	@Override
	protected BuiltinSystemVariableReference copyWithKeys( List<Expression> keys ) {
		return new BuiltinSystemVariableReference( m_builtinVariable, keys ) ;
	}

	@Override public Scope scope() { return Scope.PERSISTENT ; }
	public BuiltinSystemVariable builtinSystemVariable() { return m_builtinVariable; }

	@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitBuiltinSystemVariableReference(this); }

	@Override protected String unformattedVariableNameRepresentation() {
		return m_builtinVariable.canonicalSymbol() ;
	}

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write( this ) ;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) return true ;
		if ( !super.equals(this)) return false ;
		if ( !(obj instanceof BuiltinSystemVariableReference) ) return false ;
		BuiltinSystemVariableReference variable = (BuiltinSystemVariableReference)obj ;
		return
			m_builtinVariable.equals( variable.m_builtinVariable )
			;
	}
}
