package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import edu.vanderbilt.clinicalsystems.m.lang.model.Element;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;

public abstract class InputOutput implements Element {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static InputOutput wrap( VariableReference variable ) { return new InputOutputVariable(variable) ; }
	public static InputOutput wrap( Expression expression ) { return new OutputExpression(expression) ; }
	
	public interface Visitor<R> {
		R visitInputOutput( InputOutput inputOutput ) ; 
		default R visitInputOutputVariable  ( InputOutputVariable inputOutputVariable     ) { return visitInputOutput  ( inputOutputVariable   ) ; } 
		default R visitOutputExpression     ( OutputExpression    outputExpression        ) { return visitInputOutput  ( outputExpression      ) ; } 
		default R visitFormatCommand        ( FormatCommand       formatCommand           ) { return visitInputOutput  ( formatCommand         ) ; } 
		default R visitCarriageReturnCommand( CarriageReturnCommand carriageReturnCommand ) { return visitFormatCommand( carriageReturnCommand ) ; } 
		default R visitPageFeedCommand      ( PageFeedCommand       pageFeedCommand       ) { return visitFormatCommand( pageFeedCommand       ) ; } 
		default R visitColumnCommand        ( ColumnCommand         columnCommand         ) { return visitFormatCommand( columnCommand         ) ; } 
	}

	public <R> R visit( Visitor<R> visitor ) { return visitor.visitInputOutput(this) ; }
	
}
