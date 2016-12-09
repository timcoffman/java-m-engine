package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.Arrays;
import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Element;

public abstract class Expression implements Element {

	private static final long serialVersionUID = 1L;

	public static List<Expression> list( Expression ... expressions ) { return Arrays.asList(expressions) ; } 
	
	@Override public String toString() {
		return unformattedRepresentation() ;
	}
	
	protected abstract String unformattedRepresentation() ;

	public Expression inverted() { return new UnaryOperation( OperatorType.NOT, this ) ; }

	public Expression simplified() { return this ; } // by default, this expression is as simple as possible
	
	public interface Visitor<R> extends VariableReference.Visitor<R>, FunctionCall.Visitor<R> {
		R visitExpression( Expression expression ) ;
		@Override default R visitVariableReference ( VariableReference     variable ) { return visitExpression(variable    ) ; }
		@Override default R visitFunctionCall      ( FunctionCall      functionCall ) { return visitExpression(functionCall) ; }
		default R visitConstant           ( Constant                 constant ) { return visitExpression(constant    ) ; }
		default R visitBinaryOperation    ( BinaryOperation         operation ) { return visitExpression(operation   ) ; }
		default R visitUnaryOperation     ( UnaryOperation          operation ) { return visitExpression(operation   ) ; }
		default R visitConditional        ( ConditionalExpression conditional ) { return visitExpression(conditional ) ; }
	}
	
	public <R> R visit( Visitor<R> visitor ) { return visitor.visitExpression(this) ; }
}
