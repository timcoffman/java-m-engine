package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.BuiltinVariable;
import edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class BuiltinVariableReference extends BuiltinVariableReferenceBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final BuiltinVariable m_builtinVariable ;

	public BuiltinVariableReference( BuiltinVariable builtinVariable ) {
		super( ReferenceStyle.DIRECT ) ;
		m_builtinVariable = builtinVariable ;
	}

	public BuiltinVariableReference( BuiltinVariable builtinVariable, List<Expression> keys ) {
		super( ReferenceStyle.DIRECT, keys ) ;
		m_builtinVariable = builtinVariable ;
	}

	@Override
	protected BuiltinVariableReference copyWithKeys( List<Expression> keys ) {
		return new BuiltinVariableReference( m_builtinVariable, keys ) ;
	}
	
	@Override public Scope scope() { return Scope.TRANSIENT ; }
	public BuiltinVariable builtinVariable() { return m_builtinVariable; }

	@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitBuiltinVariableReference(this); }

	@Override protected String unformattedVariableNameRepresentation() {
		return Scope.TRANSIENT.unformattedRepresentation() + m_builtinVariable.canonicalSymbol() ;
	}

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write( this ) ;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) return true ;
		if ( !super.equals(this)) return false ;
		if ( !(obj instanceof BuiltinVariableReference) ) return false ;
		BuiltinVariableReference variable = (BuiltinVariableReference)obj ;
		return
			m_builtinVariable.equals( variable.m_builtinVariable )
			;
	}

}
