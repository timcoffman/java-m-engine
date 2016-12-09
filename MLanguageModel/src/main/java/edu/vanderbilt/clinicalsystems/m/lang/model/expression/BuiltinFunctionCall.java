package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class BuiltinFunctionCall extends FunctionCall {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final BuiltinFunction m_builtinFunction ;
	
	public BuiltinFunctionCall( BuiltinFunction function, List<Expression> arguments ) {
		super(Returning.NO_VALUE,arguments) ;
		m_builtinFunction = function ;
	}

	public BuiltinFunction builtinFunction() { return m_builtinFunction ; }
	
	@Override
	protected String unformattedFunctionName() { return "$" + m_builtinFunction.canonicalSymbol() ; }

	@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitBuiltinFunctionCall(this) ; }

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write( this ) ;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) return true ;
		if ( !super.equals(obj) ) return false ;
		if ( !(obj instanceof BuiltinFunctionCall) ) return false ;
		BuiltinFunctionCall buildtinFunctionCall = (BuiltinFunctionCall)obj ;
		return
			m_builtinFunction.equals( buildtinFunctionCall.m_builtinFunction )
			;
	}
}
