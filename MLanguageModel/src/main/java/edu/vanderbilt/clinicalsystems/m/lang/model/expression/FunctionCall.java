package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class FunctionCall extends Expression {

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
		m_arguments.addAll( arguments ) ;
	}

	public Iterable<Expression> arguments() { return m_arguments ; }
	public Returning returning() { return m_returning ; }

	@Override
	protected String unformattedRepresentation() {
		return
				(m_returning == Returning.SOME_VALUE ? "? = " : "") +
				unformattedFunctionName() +
				"(" +
				StreamSupport.stream(arguments().spliterator(), false).map(e->e.unformattedRepresentation()).collect(Collectors.joining(", ")) +
				")"
				;
	}
	
	protected abstract String unformattedFunctionName() ;

}