package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import edu.vanderbilt.clinicalsystems.m.lang.model.Element;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public abstract class Destination<T extends Element> implements Element {

	protected final T m_element ;
	
	protected Destination( T element ) { m_element = element ; }
	
	public static Destination<VariableReference> wrap( VariableReference variableReference ) {
		return new Destination<VariableReference>( variableReference ) {
			@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitVariableReference(m_element); }
		} ;
	}

	public static Destination<BuiltinFunctionCall> wrap( BuiltinFunctionCall builtinFunctionCall ) {
		return new Destination<BuiltinFunctionCall>( builtinFunctionCall ) {
			@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitBuiltinFunctionCall(m_element); }
		} ;
	}
	
	public interface Visitor<R> {
		R visitElement( Element element ) ;
		R visitVariableReference( VariableReference variable ) ;
		R visitBuiltinFunctionCall( BuiltinFunctionCall builtinFunctionCall ) ;
	}
	
	public <R> R visit( Visitor<R> visitor ) { return visitor.visitElement(m_element); }
	
	@Override public void write(RoutineWriter writer) throws RoutineWriterException { m_element.write(writer); }

	@Override public String toString() { return m_element.toString() ; }
	
}
