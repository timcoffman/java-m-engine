package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.Arrays;
import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Element;

public abstract class Expression implements Element {

	public static List<Expression> list( Expression ... expressions ) { return Arrays.asList(expressions) ; } 
	
	@Override public String toString() {
		return unformattedRepresentation() ;
	}
	
	protected abstract String unformattedRepresentation() ;

	public Expression inverted() { return new UnaryOperation( OperatorType.NOT, this ) ; }

	public Expression simplified() { return this ; } // by default, this expression is as simple as possible
}