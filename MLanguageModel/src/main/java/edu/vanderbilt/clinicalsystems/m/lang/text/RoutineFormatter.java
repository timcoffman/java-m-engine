package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.IOException;
import java.io.Writer;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;
import edu.vanderbilt.clinicalsystems.m.lang.RoutineAccess;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;

public interface RoutineFormatter {
	
	void openRoutine( Writer writer ) throws IOException ;
	void closeRoutine( Writer writer ) throws IOException ;
	
	void writeTagName( String name, Writer writer ) throws IOException ;
	void openTagParameters( Writer writer ) throws IOException ;
	void writeParameter( String parameterName, Writer writer ) throws IOException ;
	void writeTagParametersDelimiter( Writer writer ) throws IOException ;
	void closeTagParameters( Writer writer ) throws IOException ;
	
	void writeCommand( CommandType command, Writer writer ) throws IOException ;
	void writeCommandDelimiter( Writer writer ) throws IOException ;
	void writeConditionDelimiter( Writer writer ) throws IOException ;

	void writeNoArgument( Writer writer ) throws IOException ;

	void writeAssignmentOperator( Writer writer ) throws IOException ;
	void writeAssignmentDelimiter( Writer writer ) throws IOException ;
	void writeExpressionDelimiter( Writer writer ) throws IOException ;
	void writeDeclarationDelimiter( Writer writer ) throws IOException ;

	void writeTaggedRoutine( String functionName, String routineName, RoutineAccess routineAccess, Writer writer ) throws IOException ;
	
	void writeFunction( String functionName, String routineName, RoutineAccess routineAccess, Writer writer ) throws IOException ;

	void openComment( Writer writer ) throws IOException ;
	void writeCommentText( String comment, Writer writer ) throws IOException ;
	void closeComment( Writer writer ) throws IOException ;

	void openFunctionParameterList( Writer writer) throws IOException ;
	void writeFunctionParameterDelimiter( Writer writer) throws IOException ;
	void closeFunctionParameterList( Writer writer) throws IOException ;

	void writeInvalidExpression( String reason, Writer writer ) throws IOException ;
	
	void writeNoExpression( Writer writer ) throws IOException ;
	void writeNumberConstant( String value, Writer writer ) throws IOException ;
	void writeStringConstant( String value, Writer writer ) throws IOException ;
	
	void writeVariablePassedByReference( Scope scope, String variableName, Writer writer ) throws IOException ;
	void writeIndirectVariable( Scope scope, String indirectVariableName, Writer writer ) throws IOException ;
	void writeDirectVariable( Scope scope, String variableName, Writer writer ) throws IOException ;
	void openVariableKeys( Writer writer ) throws IOException ;
	void writeVariableKeysDelimiter( Writer writer ) throws IOException ;
	void closeVariableKeys( Writer writer ) throws IOException ;

	void openExpressionGroup( Writer writer ) throws IOException ;
	void closeExpressionGroup( Writer writer ) throws IOException ;
	
	void writeOperator( OperatorType operator, Writer writer ) throws IOException ;
	
	void appendLoopDefinitionDelimiter( Writer writer ) throws IOException ;
	
	void openInlineBlock( Writer writer ) throws IOException ;
	void closeInlineBlock( Writer writer ) throws IOException ;

	void openMultilineBlock( Writer writer ) throws IOException ;
	void closeMultilineBlock( Writer writer ) throws IOException ;

}
