package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineFunctionCall;

public abstract class FunctionCall extends Expression {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum Returning {
		SOME_VALUE,
		NO_VALUE,
		UNKNOWN
	}
	
	private final Returning m_returning ;
	private final List<Expression> m_arguments = new ArrayList<Expression>();

	public FunctionCall(Returning returning) {
		m_returning = returning ;
	}
	
	public FunctionCall(Returning returning, List<Expression> arguments) {
		this(returning ) ;
		arguments.forEach(Objects::requireNonNull);
		m_arguments.addAll( arguments ) ;
	}

	public Iterable<Expression> arguments() { return m_arguments ; }
	public Returning returning() { return m_returning ; }

	public interface Visitor<R> {
		R visitFunctionCall( FunctionCall functionCall) ;
		default R visitBuiltinFunctionCall( BuiltinFunctionCall functionCall ) { return visitFunctionCall(functionCall) ; }
		default R visitRoutineFunctionCall( RoutineFunctionCall functionCall ) { return visitFunctionCall(functionCall) ; }
	}

	@Override public <R> R visit( Expression.Visitor<R> visitor ) { return visit( (Visitor<R>)visitor ); }
	public <R> R visit( Visitor<R> visitor ) { return visitor.visitFunctionCall(this); }

	@Override
	protected String unformattedRepresentation() {
		return
				unformattedFunctionName() +
				"(" +
				StreamSupport.stream(arguments().spliterator(), false).map(Expression::unformattedRepresentation).collect(Collectors.joining(", ")) +
				")"
				;
	}
	
	protected abstract String unformattedFunctionName() ;

	@Override
	public boolean equals(Object obj) {
		if ( null == obj ) return false ;
		if ( this == obj ) return true ;
		if ( !(obj instanceof FunctionCall) ) return false ;
		FunctionCall functionCall = (FunctionCall)obj ;
		return
			m_returning.equals( functionCall.m_returning )
			&& m_arguments.equals( functionCall.m_arguments )
			;
	}

}