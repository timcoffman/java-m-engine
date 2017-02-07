package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.IOException;
import java.io.Writer;

import edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinSystemVariable;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinVariable;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.MatchPattern.MatchSequence;

/**
 * Translates {@link RoutineFormatter} methods calls into {@link FormattingCall}s
 */
public abstract class RoutineCallProducingFormatter implements RoutineFormatter {

	/**
	 * Represents a method call on a {@link RoutineFormatter}
	 */
	protected interface FormattingCall {
		/**
		 * Applies the method call to the given {@link RoutineFormatter} 
		 * @param routineFormatter receives the method call
		 * @throws IOException
		 */
		void executeOn(RoutineFormatter routineFormatter) throws IOException ;
	}
	
	public RoutineCallProducingFormatter() { }
	
	/**
	 * Receives {@link FormattingCall}s whenever a {@link RoutineFormatter} method is called
	 * 
	 * @param formattingCall
	 * @throws IOException
	 */
	protected abstract void produce(FormattingCall formattingCall ) throws IOException ;
	
	@Override public void openRoutine( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.openRoutine(writer) ) ; }

	@Override public void closeRoutine(Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.closeRoutine(writer) ) ; }

	@Override public void writeTagName(String name, Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeTagName(name,writer) ) ; }

	@Override public void openTagParameters(Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.openTagParameters(writer) ) ; }

	@Override public void writeParameter(String parameterName, Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeParameter(parameterName,writer) ) ; }

	@Override public void writeTagParametersDelimiter(Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeTagParametersDelimiter(writer) ) ; }

	@Override public void closeTagParameters(Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.closeTagParameters(writer) ) ; }

	@Override public void writeCommand(CommandType command, Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeCommand(command, writer) ) ; }

	@Override public void writeCommandDelimiter( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeCommandDelimiter(writer) ) ; }

	@Override public void writeConditionDelimiter( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeConditionDelimiter(writer) ) ; }

	@Override public void writeNoArgument( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeNoArgument(writer) ) ; }

	@Override public void writeAssignmentOperator( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeAssignmentOperator(writer) ) ; }
	
	@Override public void writeAssignmentDelimiter( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeAssignmentDelimiter(writer) ) ; }

	@Override public void writeExpressionDelimiter( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeExpressionDelimiter(writer) ) ; }

	@Override public void writeDeclarationDelimiter( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeDeclarationDelimiter(writer) ) ; }
	
	@Override public void writeTaggedRoutine(String functionName, String routineName, Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeTaggedRoutine(functionName, routineName, writer) ) ; }

	@Override public void writeFunction(String functionName, String routineName, Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeFunction(functionName, routineName, writer) ) ; }

	@Override public void writeBuiltinFunction(BuiltinFunction builtinFunction, Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeBuiltinFunction(builtinFunction, writer) ) ; }

	@Override public void openComment( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.openComment(writer) ) ; }

	@Override public void writeCommentText(String comment, Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeCommentText(comment, writer) ) ; }

	@Override public void closeComment( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.closeComment(writer) ) ; }

	@Override public void openFunctionParameterList( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.openFunctionParameterList(writer) ) ; }

	@Override public void writeFunctionParameterDelimiter( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeFunctionParameterDelimiter(writer) ) ; }

	@Override public void closeFunctionParameterList( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.closeFunctionParameterList(writer) ) ; }

	@Override public void writeInvalidExpression( String reason, Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeInvalidExpression(reason,writer) ) ; }
	
	@Override public void writeNoExpression( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeNoExpression(writer) ) ; }

	@Override public void writeNumberConstant(String value, Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeNumberConstant(value, writer) ) ; }

	@Override public void writeMatchSequence(MatchSequence matchSequence, Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeMatchSequence(matchSequence, writer) ) ; }
	
	@Override public void writeStringConstant(String value, Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeStringConstant(value, writer) ) ; }

	@Override public void writeVariablePassedByReference(Scope scope, String variableName, Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeVariablePassedByReference(scope, variableName, writer) ) ; }

	@Override public void writeIndirectionOperator(Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeIndirectionOperator(writer) ) ; }

	@Override public void writeDirectVariable(Scope scope, String variableName, Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeDirectVariable(scope, variableName, writer) ) ; }
	
	@Override public void writeBuiltinVariable(BuiltinVariable builtinVariable, Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeBuiltinVariable(builtinVariable, writer) ) ; }

	@Override public void writeBuiltinSystemVariable(BuiltinSystemVariable builtinSystemVariable, Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeBuiltinSystemVariable(builtinSystemVariable, writer) ) ; }
	
	@Override public void openVariableKeys( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.openVariableKeys(writer) ) ; }

	@Override public void writeVariableKeysDelimiter( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeVariableKeysDelimiter(writer) ) ; }

	@Override public void closeVariableKeys( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.closeVariableKeys(writer) ) ; }

	@Override public void openExpressionGroup( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.openExpressionGroup(writer) ) ; }

	@Override public void closeExpressionGroup( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.closeExpressionGroup(writer) ) ; }

	@Override public void openExpressionPrecondition( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.openExpressionPrecondition(writer) ) ; }
	
	@Override public void closeExpressionPrecondition( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.closeExpressionPrecondition(writer) ) ; }
	
	@Override public void writeOperator(OperatorType operator, Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeOperator(operator, writer) ) ; }

	@Override public void writeLoopDefinitionDelimiter(Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.writeLoopDefinitionDelimiter(writer) ) ; }

	@Override public void openInlineBlock(Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.openInlineBlock(writer) ) ; }

	@Override public void closeInlineBlock(Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.closeInlineBlock(writer) ) ; }

	@Override public void openMultilineBlock(Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.openMultilineBlock(writer) ) ; }

	@Override public void closeMultilineBlock( Writer writer) throws IOException { produce( (RoutineFormatter rf) -> rf.closeMultilineBlock(writer) ) ; }

}
