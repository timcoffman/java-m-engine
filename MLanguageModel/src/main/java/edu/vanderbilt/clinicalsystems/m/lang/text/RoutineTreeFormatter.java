package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.IOException;
import java.io.Writer;

import edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinSystemVariable;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinVariable;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;
import edu.vanderbilt.clinicalsystems.m.lang.ParameterPassMethod;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;

public class RoutineTreeFormatter implements RoutineFormatter {
	
	private static final int BLOCK_DEPTH_OUTSIDE_ROUTINE = -1 ;
	
	private int m_blockDepth = BLOCK_DEPTH_OUTSIDE_ROUTINE ;
	
	private void startNewLine( Writer writer ) throws IOException {
		writer.write( "\n" ) ;
	}
	
	private void indent( Writer writer ) throws IOException {
		for ( int i = 0 ; i < m_blockDepth ; ++i ) writer.append( "  " ) ;
	}

	@Override
	public void openRoutine( Writer writer ) throws IOException {
		m_blockDepth = BLOCK_DEPTH_OUTSIDE_ROUTINE ;
	}
	
	@Override
	public void closeRoutine( Writer writer ) throws IOException {
		if ( BLOCK_DEPTH_OUTSIDE_ROUTINE != m_blockDepth )
			throw new IllegalStateException("attempted to close routine while block (depth:" + m_blockDepth + ") still open") ;
	}
	
	@Override
	public void writeCommand(CommandType command, Writer writer) throws IOException {
		startNewLine(writer);
		indent(writer) ;
		writer.append( "CMD: ") ;
		writer.append( command.canonicalSymbol() ) ;
	}
	
	@Override
	public void writeCommandDelimiter(Writer writer) throws IOException {
		writer.append(" | ") ;
	}
	
	@Override
	public void writeConditionDelimiter(Writer writer) throws IOException {
		writer.append(" : ") ;
	}
	
	@Override
	public void writeAssignmentDelimiter(Writer writer) throws IOException {
		writer.append( ", " ) ;
	}
	
	@Override
	public void writeAssignmentOperator(Writer writer) throws IOException {
		writer.append( " := " ) ;
	}
	
	@Override
	public void writeExpressionDelimiter(Writer writer) throws IOException {
		writer.append( ", " ) ;
	}
	
	@Override
	public void writeDeclarationDelimiter(Writer writer) throws IOException {
		writer.append( ", " ) ;
	}
	
	@Override
	public void writeTaggedRoutine(String functionName, String routineName, Writer writer) throws IOException {
		if ( null != functionName ) {
			writer.append( functionName ) ;
		}
		if ( null != routineName ) {
			writer.append( "^" ) ;
			writer.append( routineName ) ;
		}
	}

	@Override
	public void writeFunction(String functionName, String routineName, Writer writer) throws IOException {
		writer.append( "$$" ) ;
		writeTaggedRoutine(functionName, routineName, writer);
	}
	
	@Override
	public void writeBuiltinFunction(BuiltinFunction builtinFunction, Writer writer) throws IOException {
		writer.append( "$" ) ;
		writer.append( builtinFunction.canonicalSymbol() );
	}
	
	@Override
	public void writeCommentText(String comment, Writer writer) throws IOException {
		startNewLine(writer);
		indent(writer);
		writer.append( "###: " ) ;
		writer.append( comment ) ;
	}
	
	@Override
	public void openComment(Writer writer) throws IOException {
	}
	@Override
	public void closeComment(Writer writer) throws IOException {
	}
	
	@Override
	public void openFunctionParameterList(Writer writer) throws IOException {
		writer.append( "( " ) ;
	}
	@Override
	public void writeFunctionParameterDelimiter(Writer writer) throws IOException {
		writer.append( ", " ) ;
	}
	@Override
	public void closeFunctionParameterList(Writer writer) throws IOException {
		writer.append( " )" ) ;
	}

	public void writeScopeMarker( Scope scope, Writer writer ) throws IOException {
		switch ( scope ) {
		case GLOBAL:
			writer.append( "^" ) ;
			break;
		case LOCAL:
			break;
		}
	}

	@Override
	public void writeVariablePassedByReference( Scope scope, String variableName, Writer writer ) throws IOException {
		writer.append( "." ) ;
		writeScopeMarker( scope, writer ) ;
		writer.append( variableName ) ;
	}
	
	@Override
	public void writeIndirectionOperator( Writer writer ) throws IOException {
		writer.append( "@" ) ;
	}
	
	@Override
	public void writeDirectVariable( Scope scope, String variableName, Writer writer ) throws IOException {
		writeScopeMarker( scope, writer ) ;
		writer.append( variableName ) ;
	}
	
	@Override
	public void writeBuiltinVariable( BuiltinVariable variable, Writer writer ) throws IOException {
		writer.append( "$" ) ;
		writer.append( variable.canonicalSymbol() ) ;
	}
	
	@Override
	public void writeBuiltinSystemVariable( BuiltinSystemVariable variable, Writer writer ) throws IOException {
		writer.append( "^" ) ;
		writer.append( "$" ) ;
		writer.append( variable.canonicalSymbol() ) ;
	}
	
	@Override
	public void openVariableKeys( Writer writer ) throws IOException {
		writer.append( "( " ) ;
	}
	
	@Override
	public void writeVariableKeysDelimiter( Writer writer ) throws IOException {
		writer.append( ", " ) ;
	}
	
	@Override
	public void closeVariableKeys( Writer writer )  throws IOException {
		writer.append( " )" ) ;
	}
	
	public void writeVariable(Scope scope, String variableName, ParameterPassMethod parameterPassMethod, Writer writer) throws IOException {
		writer.append( "." ) ;
		writer.append( "^" ) ;
		writer.append( "(" ) ;
	}
	
	@Override
	public void writeOperator(OperatorType operator, Writer writer) throws IOException {
		writer.append( operator.canonicalSymbol() ) ;
	}

	@Override
	public void writeLoopDefinitionDelimiter( Writer writer ) throws IOException {
		writer.append( ":" ) ;
	}

	@Override
	public void openExpressionGroup( Writer writer ) throws IOException {
		writer.append( "( " ) ;
	}
	@Override
	public void closeExpressionGroup( Writer writer ) throws IOException {
		writer.append( " )" ) ;
	}

	@Override
	public void openExpressionPrecondition( Writer writer ) throws IOException {
		writer.append( "( " ) ;
	}
	@Override
	public void closeExpressionPrecondition( Writer writer ) throws IOException {
		writer.append( " )? " ) ;
	}
	
	
	@Override
	public void openInlineBlock(Writer writer) throws IOException {
		writer.append(" {") ;
		++m_blockDepth ;
	}
	@Override
	public void closeInlineBlock(Writer writer) throws IOException {
		--m_blockDepth  ;
		startNewLine(writer);
		indent(writer);
		writer.append("}") ;
	}

	@Override
	public void openMultilineBlock(Writer writer) throws IOException {
		startNewLine(writer);
		indent(writer);
		writer.append("{") ;
		++m_blockDepth ;
	}
	@Override
	public void closeMultilineBlock(Writer writer) throws IOException {
		--m_blockDepth  ;
		startNewLine(writer);
		indent(writer);
		writer.append("}") ;
	}
	
	@Override
	public void writeTagName(String name, Writer writer) throws IOException {
		startNewLine(writer);
		indent(writer);
		writer.append( "TAG: " ) ;
		writer.append( name ) ;
	}
	@Override
	public void writeParameter( String parameterName, Writer writer ) throws IOException {
		writer.append( parameterName );
	}
	@Override
	public void openTagParameters( Writer writer ) throws IOException {
		writer.append( "( " ) ;
	}
	@Override
	public void writeTagParametersDelimiter( Writer writer ) throws IOException {
		writer.append( ", " ) ;
	}
	@Override
	public void closeTagParameters( Writer writer ) throws IOException {
		writer.append( " )" ) ;
	}
	
	@Override
	public void writeNoArgument(Writer writer) throws IOException {
		writer.append( "(-)" ) ;
	}
	
	@Override public void writeInvalidExpression( String reason, Writer writer) throws IOException {
		openExpressionGroup(writer);
		writer.append( "! " + reason + " !") ;
		closeExpressionGroup(writer);
	}

	@Override
	public void writeNoExpression(Writer writer) throws IOException {
		writer.append( "(-)" ) ;
	}
	
	@Override
	public void writeNumberConstant(String value, Writer writer) throws IOException {
		writer.append( value ) ;
	}
	
	@Override
	public void writeStringConstant(String value, Writer writer) throws IOException {
		writer.append( "\"" ) ;
		writer.append( value.replaceAll("(\")", "\\\\\\1") ) ;
		writer.append( "\"" ) ;
	}

}
