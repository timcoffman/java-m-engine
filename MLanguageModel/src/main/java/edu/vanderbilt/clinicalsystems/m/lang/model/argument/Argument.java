package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import edu.vanderbilt.clinicalsystems.m.lang.model.Element;

public abstract class Argument implements Element {

	public static final Argument NOTHING = Nothing.INSTANCE ;

	public interface Visitor<R> {
		R visitArgument ( Argument argument ) ;
		default R visitNothing          ( Nothing           nothing           ) { return visitArgument(nothing          ) ; }
		default R visitLoopDefinition   ( LoopDefinition    loopDefinition    ) { return visitArgument(loopDefinition   ) ; }
		default R visitTaggedRoutineCall( TaggedRoutineCall taggedRoutineCall ) { return visitArgument(taggedRoutineCall) ; }
		default R visitAssignmentList   ( AssignmentList    assignmentList    ) { return visitArgument(assignmentList   ) ; }
		default R visitDeclarationList  ( DeclarationList   declarationList   ) { return visitArgument(declarationList  ) ; }
		default R visitVariableList     ( VariableList      variableList      ) { return visitArgument(variableList     ) ; }
		default R visitExpressionList   ( ExpressionList    expressionList    ) { return visitArgument(expressionList   ) ; }
		default R visitInputOutputList  ( InputOutputList   inputOutputList   ) { return visitArgument(inputOutputList  ) ; }
	}

	public <R> R visit( Visitor<R> visitor ) {
		return visitor.visitArgument(this) ;
	}

	@Override
	public String toString() {
		return unformattedRepresentation();
	}

	protected abstract String unformattedRepresentation() ;
}
