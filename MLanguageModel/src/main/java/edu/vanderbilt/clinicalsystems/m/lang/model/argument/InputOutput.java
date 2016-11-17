package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import edu.vanderbilt.clinicalsystems.m.lang.model.Element;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;

public abstract class InputOutput implements Element {

	public static InputOutput wrap( VariableReference variable ) { return new InputOutputVariable(variable) ; }
	public static InputOutput wrap( Expression expression ) { return new OutputExpression(expression) ; }
	
}
