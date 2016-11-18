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
	
	public interface Visitor<R> extends VariableReference.Visitor<R> {
		R visitExpression( Expression expression ) ;
		default R visitConstant         ( Constant          constant ) { return visitExpression(constant) ; }
		@Override
		default R visitVariableReference( VariableReference variable ) { return visitExpression(variable) ; }
	}
	
	public <R> R visit( Visitor<R> visitor ) { return visitor.visitExpression(this) ; }
}
