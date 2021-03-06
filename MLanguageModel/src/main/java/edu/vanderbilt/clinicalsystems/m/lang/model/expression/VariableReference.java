package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;

public abstract class VariableReference extends Expression {

	private static final long serialVersionUID = 1L;

	public static final DirectVariableReference DEFAULT_TEMP_VARIABLE
		= new DirectVariableReference(Scope.TRANSIENT, "%");

	private final ReferenceStyle m_referenceStyle ;
	private final List<Expression> m_keys = new ArrayList<Expression>();

	public VariableReference( ReferenceStyle referenceStyle ) {
		m_referenceStyle = referenceStyle ;
	}
	
	public VariableReference( ReferenceStyle referenceStyle, List<Expression> keys ) {
		m_referenceStyle = referenceStyle ;
		m_keys.addAll( keys ) ;
	}
	
	public Optional<Expression> finalKey() {
		if ( m_keys.isEmpty() )
//			throw new IllegalArgumentException( "cannot make parent variable of variable that has no keys" ) ;
			return Optional.empty() ;
		return Optional.of(m_keys.get( m_keys.size()-1 )) ;
	}
	
	public Optional<VariableReference> parent() {
		if ( m_keys.isEmpty() )
//			throw new IllegalArgumentException( "cannot make parent variable of variable that has no keys" ) ;
			return Optional.empty() ;
		List<Expression> parentKeys = m_keys.subList(0, m_keys.size()-1 );
		return Optional.of(copyWithKeys( parentKeys )) ;
	}
	
	public VariableReference child( Expression key ) {
		List<Expression> childKeys = new ArrayList<Expression>( m_keys );
		childKeys.add( key ) ;
		return copyWithKeys( childKeys ) ;
	}
	
	protected abstract VariableReference copyWithKeys( List<Expression> keys ) ;
	
	public ReferenceStyle referenceStyle() { return m_referenceStyle; }
	public Iterable<Expression> keys() { return m_keys; }

	@Override public <R> R visit( Expression.Visitor<R> visitor ) { return visit( (Visitor<R>)visitor ); }
	public <R> R visit( Visitor<R> visitor ) { return visitor.visitVariableReference(this); }
	
	public interface Visitor<R> {
		R visitVariableReference ( VariableReference variable ) ;
		default R visitDirectVariableReference       ( DirectVariableReference        variable ) { return visitVariableReference(variable); }
		default R visitIndirectVariableReference     ( IndirectVariableReference      variable ) { return visitVariableReference(variable); }
		default R visitBuiltinVariableReference      ( BuiltinVariableReference       variable ) { return visitVariableReference(variable); }
		default R visitBuiltinSystemVariableReference( BuiltinSystemVariableReference variable ) { return visitVariableReference(variable); }
	}
	
	protected abstract String unformattedVariableNameRepresentation() ;

	@Override
	protected String unformattedRepresentation() {
		String s = m_referenceStyle.unformattedRepresentation()
			+
			unformattedVariableNameRepresentation()
			;
		if ( !m_keys.isEmpty() ) {
			s += m_referenceStyle.unformattedRepresentation()
				+ m_keys.stream().map(e->e.unformattedRepresentation()).collect(Collectors.joining(", ","(",")"))
				;
		}
		return s ;
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) return true ;
		if ( null == obj ) return false ;
		if ( !(obj instanceof VariableReference) ) return false ;
		VariableReference variable = (VariableReference)obj ;
		return
			m_referenceStyle.equals( variable.m_referenceStyle )
			&& m_keys.equals( variable.m_keys )
			;
	}
	
	
}
