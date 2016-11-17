package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import edu.vanderbilt.clinicalsystems.m.lang.model.Element;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class Destination<T extends Element> implements Element {

	private final T m_element ;
	
	protected Destination( T element ) { m_element = element ; }
	
	public static Destination<VariableReference> wrap( VariableReference variableReference ) {
		return new Destination<VariableReference>( variableReference ) ;
	}

	public static Destination<BuiltinFunctionCall> wrap( BuiltinFunctionCall builtinFunctionCall ) {
		return new Destination<BuiltinFunctionCall>( builtinFunctionCall ) ;
	}
	
	@Override public void write(RoutineWriter writer) throws RoutineWriterException { m_element.write(writer); }

	@Override public String toString() { return m_element.toString() ; }
	
}
