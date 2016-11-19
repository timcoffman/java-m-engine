package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import java.util.Objects;

import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class InputOutputVariable extends InputOutput {

	private final VariableReference m_variable ;
	
	public InputOutputVariable( VariableReference variable ) {
		Objects.requireNonNull(variable) ;
		m_variable = variable ;
	}
	
	public VariableReference variable() { return m_variable ; }
	
	@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitInputOutputVariable(this) ; }

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	}

	@Override public String toString() { return m_variable.toString() ; } 
	
	@Override public boolean equals( Object obj ) {
		if ( null == obj ) return false ;
		if ( this == obj ) return true ;
		if ( !(obj instanceof InputOutputVariable) ) return false ;
		InputOutputVariable inputOutputVariable = (InputOutputVariable)obj ;
		return m_variable.equals( inputOutputVariable.m_variable ) ;
	}
}
