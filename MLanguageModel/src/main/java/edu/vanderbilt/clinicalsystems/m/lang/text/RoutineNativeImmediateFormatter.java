package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.IOException;
import java.io.Writer;

import edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinSystemVariable;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinVariable;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;
import edu.vanderbilt.clinicalsystems.m.lang.ParameterPassMethod;
import edu.vanderbilt.clinicalsystems.m.lang.RoutineAccess;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;

/*
 * Writes formatted M code
 */
class RoutineNativeImmediateFormatter implements RoutineFormatter {
	private enum LineState {
		NONE,
		WRITING_TAGS,
		WRITING_COMMANDS,
		WRITING_COMMENTS
	}
	
	private abstract class LineFormatter {
		abstract LineState formatsLineState() ;
		abstract void open( LineState formerLineState, Writer writer ) throws IOException ;
		abstract void close( Writer writer ) throws IOException ;
	}
	
	private static final int BLOCK_DEPTH_OUTSIDE_ROUTINE = -1 ;
	
	private HeaderFormatter m_headerFormatter = new HeaderFormatter() ;
	private FooterFormatter m_footerFormatter = new FooterFormatter() ;
	private TagFormatter m_tagFormatter = new TagFormatter() ;
	private CommandFormatter m_commandFormatter = new CommandFormatter() ;
	private CommentFormatter m_commentFormatter = new CommentFormatter() ;
	private LineFormatter m_currentFormatter = m_headerFormatter ;
	private int m_blockDepth = BLOCK_DEPTH_OUTSIDE_ROUTINE ;
	private int m_blockDepthAtStartOfLine = BLOCK_DEPTH_OUTSIDE_ROUTINE + 1;

	public static class OptionsImpl implements RoutineNativeFormatter.Options {
		private boolean m_abbreviateCommands = false ;
		private boolean m_abbreviateBuiltinFunctions = false ;
		private boolean m_abbreviateBuiltinVariables = false ;
		private boolean m_indentWithTabs = false ;
		private String m_spaceIndentation = "  " ;
		private String m_blockSpaceIndentation = " " ;
		private Integer m_commandsPerLineLimit = null ;
		private Integer m_commentsPerLineLimit = null ;

		public OptionsImpl() { }

		@Override public boolean getWriteAbbreviatedCommandSymbols() { return m_abbreviateCommands ; }
		@Override public void setWriteAbbreviatedCommandSymbols( boolean abbreviateCommands ) { m_abbreviateCommands = abbreviateCommands ; }
		
		@Override public boolean getWriteAbbreviatedBuiltinFunctionSymbols() { return m_abbreviateBuiltinFunctions ; }
		@Override public void setWriteAbbreviatedBuiltinFunctionSymbols( boolean abbreviateBuiltinFunctions ) { m_abbreviateBuiltinFunctions = abbreviateBuiltinFunctions ; }
		
		@Override public boolean getWriteAbbreviatedBuiltinVariableSymbols() { return m_abbreviateBuiltinVariables ; }
		@Override public void setWriteAbbreviatedBuiltinVariableSymbols( boolean abbreviateBuiltinVariables ) { m_abbreviateBuiltinVariables = abbreviateBuiltinVariables ; }
		
		@Override public boolean getUseTabsForIndentation() { return m_indentWithTabs ; }
		@Override public void setUseTabsForIndentation( boolean indentWithTabs ) { m_indentWithTabs = indentWithTabs ; }
		
		@Override public int getNumberOfSpacesForIndentation() { return m_spaceIndentation.length() ; }
		@Override public void setNumberOfSpacesForIndentation( int indentWithSpacesCount ) { m_spaceIndentation = "" ; for ( int i = 0 ; i < indentWithSpacesCount ; ++i ) m_spaceIndentation = m_spaceIndentation + " " ; }
		
		@Override public int getNumberOfSpacesForBlockIndentation() { return m_blockSpaceIndentation.length() ; }
		@Override public void setNumberOfSpacesForBlockIndentation( int indentBlockWithSpacesCount ) { m_blockSpaceIndentation = "" ; for ( int i = 0 ; i < indentBlockWithSpacesCount ; ++i ) m_blockSpaceIndentation = m_blockSpaceIndentation + " " ; }
		
		@Override public Integer getCommandsPerLineLimit() { return m_commandsPerLineLimit ; }
		@Override public void setCommandsPerLineLimit( Integer commandsPerLineLimit ) { m_commandsPerLineLimit = commandsPerLineLimit ; }
		
		@Override public Integer getCommentsPerLineLimit() { return m_commentsPerLineLimit ; }
		@Override public void setCommentsPerLineLimit( Integer commentsPerLineLimit ) { m_commentsPerLineLimit = commentsPerLineLimit ; }
	}

	private OptionsImpl m_options = new OptionsImpl();
	public RoutineNativeFormatter.Options options() { return m_options ; }
	
	private void startNewLine( Writer writer ) throws IOException {
		writer.write( "\n" ) ;
		writer.flush();
		m_blockDepthAtStartOfLine = m_blockDepth ;
//		System.out.println("starting a new line (" + m_blockDepthAtStartOfLine + " | " + m_blockDepth + ")") ;
	}
	
	private boolean isLineBlockActive() {
		if ( m_blockDepth < m_blockDepthAtStartOfLine )
			throw new IllegalStateException("block (depth:" + m_blockDepth + ") concluded without starting a new line") ;
		return m_blockDepth != m_blockDepthAtStartOfLine ;
	}
	
	private void indent( Writer writer ) throws IOException {
		if ( m_blockDepth == BLOCK_DEPTH_OUTSIDE_ROUTINE ) return ;
		if ( m_options.m_indentWithTabs )
			writer.write( "\t" ) ;
		else
			writer.write( m_options.m_spaceIndentation ) ;
	}
	
	private void indentBlock( Writer writer ) throws IOException {
		String indent = "." + m_options.m_blockSpaceIndentation ;
		for ( int i = 0 ; i < m_blockDepth ; ++i ) writer.append( indent ) ;
	}
	
	private <T extends LineFormatter> T useFormatter( T lineFormatter, Writer writer ) throws IOException {
		LineState preexistingLineState = m_currentFormatter.formatsLineState();
		if ( preexistingLineState != lineFormatter.formatsLineState() ) {
			m_currentFormatter.close(writer);
			m_currentFormatter = lineFormatter ;
			m_currentFormatter.open(preexistingLineState, writer) ;
		}
		return lineFormatter ;
	}

	@Override
	public void openRoutine( Writer writer ) throws IOException {
		/* only allowed at start of writing */
		m_blockDepth = BLOCK_DEPTH_OUTSIDE_ROUTINE ;
	}
	@Override
	public void closeRoutine( Writer writer ) throws IOException {
		if ( BLOCK_DEPTH_OUTSIDE_ROUTINE != m_blockDepth )
			throw new IllegalStateException("attempted to close routine while block (depth:" + m_blockDepth + ") still open") ;
		useFormatter(m_footerFormatter, writer);
	}
	
	private class HeaderFormatter extends LineFormatter {
		@Override public LineState formatsLineState() { return LineState.NONE ; }
		
		@Override public void open( LineState formerLineState, Writer writer ) throws IOException {
			throw new IllegalStateException("cannot open header formatter");
		}
		
		@Override
		public void close( Writer writer ) throws IOException {
			/* OK */
		}
	}
	
	private class FooterFormatter extends LineFormatter {
		@Override public LineState formatsLineState() { return LineState.NONE ; }
		
		@Override public void open( LineState formerLineState, Writer writer ) throws IOException {
			startNewLine(writer);
		}
		
		@Override
		public void close( Writer writer ) throws IOException {
			throw new IllegalStateException("cannot close footer formatter");
		}
	}
	
	private enum TagLineState {
		INITIAL,
		WRITING_TAG,
		WRITING_PARAMETERS,
		CLOSED
	}
	private class TagFormatter extends LineFormatter {
		@Override public LineState formatsLineState() { return LineState.WRITING_TAGS ; }
		
		private TagLineState m_tagLineState = TagLineState.INITIAL ;
		
		@Override public void open( LineState formerLineState, Writer writer ) throws IOException {
			switch ( formerLineState ) {
			case NONE:
				/* ready */
				break ;
			case WRITING_TAGS:
				throw new IllegalStateException("cannot re-open tag formatter");
			case WRITING_COMMANDS:
				startNewLine(writer);
				break;
			case WRITING_COMMENTS:
				startNewLine(writer);
				break;
			}
			
			m_tagLineState = TagLineState.INITIAL ;
		}
		@Override
		public void close( Writer writer ) throws IOException {
			switch ( m_tagLineState ) {
			case INITIAL:
				/* ready */
				break;
			case WRITING_TAG:
				/* ready */ ;
				break;
			case WRITING_PARAMETERS:
				writer.append( ")" ) ;
				break;
			case CLOSED:
				throw new IllegalStateException( "cannot close a tag twice") ;
			}
			m_tagLineState = TagLineState.CLOSED ; 
		}
		
		public void prepareToWriteTag(Writer writer) throws IOException {
			switch ( m_tagLineState ) {
			case INITIAL:
				/* OK */
				break;
			case WRITING_TAG:
				startNewLine(writer); ;
				break ;
			case WRITING_PARAMETERS:
				throw new IllegalStateException( "cannot start writing a tag while a tag's parameters list is unfinished") ;
			case CLOSED:
				startNewLine(writer);
				break ;
			}
			m_tagLineState = TagLineState.WRITING_TAG ; 
		}

		
		public void prepareToWriteParameter(Writer writer) throws IOException {
			switch ( m_tagLineState ) {
			case WRITING_TAG:
				writer.append("(") ;
				break ;
			case WRITING_PARAMETERS:
				writer.append(",") ;
				break ;
			case INITIAL:
			case CLOSED:
				throw new IllegalStateException( "cannot start writing parameters while without a tag") ;
			}
			m_tagLineState = TagLineState.WRITING_PARAMETERS ; 
		}
	}
	
	private enum CommandLineState {
		INITIAL,
		WRITING_COMMAND,
		WRITING_CONDITION,
		WRITING_ARGUMENT
	}
	
	private class CommandFormatter extends LineFormatter {
		@Override public LineState formatsLineState() { return LineState.WRITING_COMMANDS ; }
		
		private int m_commandCount = 0 ;
		
		private CommandLineState m_commandLineState = CommandLineState.INITIAL ;
		@Override public void open( LineState formerLineState, Writer writer ) throws IOException {
			switch ( formerLineState ) {
			case NONE:
			case WRITING_TAGS:
				indent(writer);
				indentBlock(writer);
				break;
			case WRITING_COMMANDS:
				throw new IllegalStateException("cannot re-open command formatter");
			case WRITING_COMMENTS:
				startNewLine(writer);
				indent(writer);
				indentBlock(writer);
				m_commandCount = 0 ;
//				System.out.println("reset command count (0)") ;
				break;
			}
			
			m_commandLineState = CommandLineState.INITIAL ;
		}
		
		@Override
		public void close( Writer writer ) {
			m_commandCount = 0 ;
//			System.out.println("reset command count (0)") ;
		}
		
		private void writeDelimiterBetweenCommands( Writer writer ) throws IOException {
			if ( null != m_options.m_commandsPerLineLimit && m_commandCount >= m_options.m_commandsPerLineLimit && !isLineBlockActive() ) {
//				System.out.println("new line between commands") ;
				startNewLine(writer);
				indent(writer);
				indentBlock(writer);
				m_commandCount = 1 ;
//				System.out.println("reset command count (1)") ;
			} else {
				writePlainDelimiter(writer);
			}
		}

		private void writeConditionDelimiter( Writer writer ) throws IOException {
			writer.append( ":" ) ;
		}

		private void writePlainDelimiter(Writer writer) throws IOException {
			writer.append( " " ) ;
		}
		
		public void prepareToWriteCommand(Writer writer) throws IOException {
//			System.out.println("command count (" + m_commandCount + " -> " + (m_commandCount+1) + ")") ;
			++m_commandCount ;
			switch ( m_commandLineState ) {
			case INITIAL:
				/* ready */
				break;
			case WRITING_COMMAND:
			case WRITING_CONDITION:
			case WRITING_ARGUMENT:
				writeDelimiterBetweenCommands( writer ) ;
				break;
			}
			m_commandLineState = CommandLineState.WRITING_COMMAND ; 
		}

		public void prepareToWriteCondition(Writer writer) throws IOException {
			switch ( m_commandLineState ) {
			case INITIAL:
				throw new IllegalStateException( "cannot start writing a condition without a command") ;
			case WRITING_COMMAND:
				writeConditionDelimiter( writer ) ;
				break;
			case WRITING_CONDITION:
				throw new IllegalStateException( "cannot start writing a second condition") ;
			case WRITING_ARGUMENT:
				throw new IllegalStateException( "cannot start writing a condition after an argument") ;
			}
			m_commandLineState = CommandLineState.WRITING_CONDITION ; 
		}

		public void prepareToWriteArgument(Writer writer) throws IOException {
			switch ( m_commandLineState ) {
			case INITIAL:
			case WRITING_ARGUMENT:
				throw new IllegalStateException( "cannot start writing an argument without a command") ;
			case WRITING_COMMAND:
			case WRITING_CONDITION:
				writePlainDelimiter(writer);
				break;
			}
			m_commandLineState = CommandLineState.WRITING_ARGUMENT ; 
		}
	}
	
	private enum CommentLineState {
		INITIAL,
		WRITING_COMMENT
	}
	private class CommentFormatter extends LineFormatter {
		@Override public LineState formatsLineState() { return LineState.WRITING_COMMENTS ; }
		
		private int m_commentCount = 0 ;
		
		private CommentLineState m_commentLineState = CommentLineState.INITIAL ;
		@Override
		public void open( LineState formerLineState, Writer writer ) throws IOException {
			switch ( formerLineState ) {
			case NONE:
			case WRITING_TAGS:
			case WRITING_COMMANDS:
				/* ready */
				break;
			case WRITING_COMMENTS:
				throw new IllegalStateException("cannot re-open comment formatter");
			}
			
			m_commentLineState = CommentLineState.INITIAL ;
		}
		@Override
		public void close( Writer writer ) {
			
		}

		private void writeDelimiterBetweenComments( Writer writer ) throws IOException {
			if ( null != m_options.m_commentsPerLineLimit && m_commentCount >= m_options.m_commentsPerLineLimit ) {
				startNewLine(writer);
				indent(writer);
				writer.append(";") ;
				m_commentCount = 0 ;
			} else {
				writePlainDelimiter(writer);
			}
		}

		private void writePlainDelimiter(Writer writer) throws IOException {
			writer.append( " " ) ;
		}
		
		public void prepareToWriteComment(Writer writer) throws IOException {
			++m_commentCount ;
			switch ( m_commentLineState ) {
			case INITIAL:
				writer.append(";") ;
				break;
			case WRITING_COMMENT:
				writeDelimiterBetweenComments(writer) ;
				break ;
			}
			m_commentLineState = CommentLineState.WRITING_COMMENT ; 
		}

	}
	
	@Override
	public void writeCommand(CommandType command, Writer writer) throws IOException {
		useFormatter( m_commandFormatter, writer ).prepareToWriteCommand( writer ) ;
		
//		System.out.println("> command (" + command + ")") ;
		if ( m_options.m_abbreviateCommands )
			writer.append( command.canonicalAbbreviation() ) ;
		else
			writer.append( command.canonicalSymbol() ) ;
	}
	
	@Override
	public void writeCommandDelimiter(Writer writer) throws IOException {
		useFormatter( m_commandFormatter, writer ).prepareToWriteArgument( writer ) ;
	}
	
	@Override
	public void writeConditionDelimiter(Writer writer) throws IOException {
		useFormatter( m_commandFormatter, writer ).prepareToWriteCondition( writer ) ;
	}
	
	@Override
	public void writeAssignmentDelimiter(Writer writer) throws IOException {
		writer.append( "," ) ;
	}
	
	@Override
	public void writeAssignmentOperator(Writer writer) throws IOException {
		writer.append( "=" ) ;
	}
	
	@Override
	public void writeExpressionDelimiter(Writer writer) throws IOException {
		writer.append( "," ) ;
	}
	
	@Override
	public void writeDeclarationDelimiter(Writer writer) throws IOException {
		writer.append( "," ) ;
	}
	
	@Override
	public void writeTaggedRoutine(String functionName, String routineName, RoutineAccess routineAccess, Writer writer) throws IOException {
		switch ( routineAccess ) {
		case EXPLICIT:
			if ( null != functionName ) {
				writer.append( functionName ) ;
				writer.append( "^" ) ;
			}
			writer.append( routineName ) ;
			break ;
		case IMPLICIT:
		case LOCAL:
		default:
			/* hide routine name */
			if ( null != functionName )
				writer.append( functionName ) ;
			else
				throw new IllegalStateException("function name cannot be omitted with " + routineAccess + " routine access") ; 
			break ;
		}
	}

	@Override
	public void writeFunction(String functionName, String routineName, RoutineAccess routineAccess, Writer writer) throws IOException {
		writer.append( "$$" ) ;
		writeTaggedRoutine(functionName, routineName, routineAccess, writer);
	}
	
	@Override
	public void writeBuiltinFunction(BuiltinFunction builtinFunction, Writer writer) throws IOException {
		writer.append( "$" ) ;
		if ( m_options.m_abbreviateBuiltinFunctions )
			writer.append( builtinFunction.canonicalAbbreviation() ) ;
		else
			writer.append( builtinFunction.canonicalSymbol() ) ;
	}
	
	@Override
	public void writeCommentText(String comment, Writer writer) throws IOException {
		useFormatter( m_commentFormatter, writer ).prepareToWriteComment( writer ) ;
//		System.out.println("# " + comment ) ;
		writer.append( comment ) ;
	}
	
	@Override
	public void openComment(Writer writer) throws IOException {
		useFormatter( m_commentFormatter, writer ) ;
	}
	@Override
	public void closeComment(Writer writer) throws IOException {
		useFormatter( m_commentFormatter, writer ) ;
		/* stay in COMMENTS state */
	}
	
	@Override
	public void openFunctionParameterList(Writer writer) throws IOException {
		writer.append( "(" ) ;
	}
	@Override
	public void writeFunctionParameterDelimiter(Writer writer) throws IOException {
		writer.append( "," ) ;
	}
	@Override
	public void closeFunctionParameterList(Writer writer) throws IOException {
		writer.append( ")" ) ;
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
	public void writeBuiltinVariable( BuiltinVariable builtinVariable, Writer writer ) throws IOException {
		writer.append( "$" ) ;
		if ( m_options.m_abbreviateBuiltinFunctions )
			writer.append( builtinVariable.canonicalAbbreviation() ) ;
		else
			writer.append( builtinVariable.canonicalSymbol() ) ;
	}
	
	@Override
	public void writeBuiltinSystemVariable( BuiltinSystemVariable builtinSystemVariable, Writer writer ) throws IOException {
		writer.append( "^" ) ;
		writer.append( "$" ) ;
		if ( m_options.m_abbreviateBuiltinFunctions )
			writer.append( builtinSystemVariable.canonicalAbbreviation() ) ;
		else
			writer.append( builtinSystemVariable.canonicalSymbol() ) ;
	}
	
	@Override
	public void openVariableKeys( Writer writer ) throws IOException {
		writer.append( "(" ) ;
	}
	
	@Override
	public void writeVariableKeysDelimiter( Writer writer ) throws IOException {
		writer.append( "," ) ;
	}
	
	@Override
	public void closeVariableKeys( Writer writer )  throws IOException {
		writer.append( ")" ) ;
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
		writer.append( "(" ) ;
	}
	@Override
	public void closeExpressionGroup( Writer writer ) throws IOException {
		writer.append( ")" ) ;
	}

	@Override
	public void openExpressionPrecondition( Writer writer ) throws IOException {
		/* nothing */
	}
	
	@Override
	public void closeExpressionPrecondition( Writer writer ) throws IOException {
		writer.append( ":" ) ;
	}
	

	@Override
	public void openInlineBlock(Writer writer) throws IOException {
//		System.out.println("inline block (" + m_blockDepthAtStartOfLine + " | " + m_blockDepth + " -> " + (m_blockDepth+1) + ")") ;
		++m_blockDepth ;
	}
	@Override
	public void closeInlineBlock(Writer writer) throws IOException {
//		System.out.println("inline block (" + m_blockDepthAtStartOfLine + " | " + m_blockDepth + " -> " + (m_blockDepth-1) + ")") ;
		--m_blockDepth  ;
	}
	
	@Override
	public void openMultilineBlock(Writer writer) throws IOException {
//		System.out.println("multiline block (" + m_blockDepthAtStartOfLine + " | " + m_blockDepth + " -> " + (m_blockDepth+1) + ")") ;
		++m_blockDepth ;
		useFormatter( m_tagFormatter, writer ) ;
	}
	@Override
	public void closeMultilineBlock(Writer writer) throws IOException {
		--m_blockDepth  ;
		useFormatter( m_tagFormatter, writer ) ;
//		System.out.println("multiline block (" + m_blockDepthAtStartOfLine + " | " + m_blockDepth + " -> " + (m_blockDepth-1) + ")") ;
	}
	
	@Override
	public void writeTagName(String name, Writer writer) throws IOException {
		useFormatter( m_tagFormatter, writer).prepareToWriteTag(writer);
//		System.out.println("\n> tag (" + name + ")") ;
		writer.append( name ) ;
	}
	@Override
	public void writeParameter( String parameterName, Writer writer ) throws IOException {
		writer.append( parameterName );
	}
	@Override
	public void openTagParameters( Writer writer ) throws IOException {
		useFormatter( m_tagFormatter, writer).prepareToWriteParameter(writer);
	}
	@Override
	public void writeTagParametersDelimiter( Writer writer ) throws IOException {
		useFormatter( m_tagFormatter, writer).prepareToWriteParameter(writer);
	}
	@Override
	public void closeTagParameters( Writer writer ) throws IOException {
		useFormatter( m_tagFormatter, writer);
	}
	
	@Override
	public void writeNoArgument(Writer writer) throws IOException {
		/* leave blank */
	}

	@Override public void writeInvalidExpression( String reason, Writer writer) throws IOException {
		throw new UnsupportedOperationException( "formatter cannot represent invalid expression: " + reason ) ;
	}

	@Override
	public void writeNoExpression(Writer writer) throws IOException {
		/* leave blank */
	}
	
	@Override
	public void writeNumberConstant(String value, Writer writer) throws IOException {
		writer.append( value ) ;
	}
	
	@Override
	public void writeStringConstant(String value, Writer writer) throws IOException {
		writer.append( "\"" ) ;
		writer.append( value.replaceAll("(\")", "\\\\$1") ) ;
		writer.append( "\"" ) ;
	}

}
