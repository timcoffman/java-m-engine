package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import java.util.Objects;

import edu.vanderbilt.clinicalsystems.m.lang.model.Element;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public abstract class Destination<T extends Element> implements Element {
	
	private static final long serialVersionUID = 1L;
	protected final T m_element ;
	
	protected Destination( T element ) {
		Objects.requireNonNull(element) ;
		m_element = element ;
	}
	
	public static Destination<VariableReference> wrap( VariableReference variableReference ) {
		return new VariableDesintation(variableReference) ;
	}

	public static Destination<BuiltinFunctionCall> wrap( BuiltinFunctionCall builtinFunctionCall ) {
		return new BuiltinFunctionCallDestination(builtinFunctionCall) ;
	}
	
	private static final class BuiltinFunctionCallDestination extends Destination<BuiltinFunctionCall> {
		private static final long serialVersionUID = 1L;
		private BuiltinFunctionCallDestination(BuiltinFunctionCall element) { super(element); }
		@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitBuiltinFunctionCall(m_element); }
	}

	private static final class VariableDesintation extends Destination<VariableReference> {
		private static final long serialVersionUID = 1L;
		private VariableDesintation(VariableReference element) { super(element); }
		@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitVariableReference(m_element); }
	}

	public interface Visitor<R> {
		R visitElement( Element element ) ;
		default R visitVariableReference( VariableReference variable ) { return visitElement(variable) ; }
		default R visitBuiltinFunctionCall( BuiltinFunctionCall builtinFunctionCall ) { return visitElement(builtinFunctionCall) ; }
	}
	
	public <R> R visit( Visitor<R> visitor ) { return visitor.visitElement(m_element); }
	
	@Override public void write(RoutineWriter writer) throws RoutineWriterException { m_element.write(writer); }

	@Override public String toString() { return m_element.toString() ; }
	
	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) return true ;
		if ( null == obj ) return false ;
		if ( !(obj instanceof Destination) ) return false ;
		Destination<?> destination = (Destination<?>)obj ;
		return
			m_element.equals( destination.m_element )
			;
	}

}
