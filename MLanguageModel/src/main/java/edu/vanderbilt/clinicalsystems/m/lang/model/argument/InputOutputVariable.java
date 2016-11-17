package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import java.util.Objects;

import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class InputOutputVariable extends InputOutput {

	private final VariableReference m_variable ;
	
	public InputOutputVariable( VariableReference variable ) {
		Objects.requireNonNull(variable) ;
		m_variable = variable ;
	}
	
	public Expression variable() { return m_variable ; }
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	}

	@Override public String toString() { return m_variable.toString() ; } 
	

}
