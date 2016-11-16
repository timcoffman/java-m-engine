package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.IOException;
import java.io.Writer;

/*
 * Manages writing formatted M code
 */
public class RoutineNativeFormatter extends RoutineCallProducingFormatter {

	public interface Options {
		boolean getWriteAbbreviatedCommandSymbols() ;
		void setWriteAbbreviatedCommandSymbols( boolean abbreviateCommands ) ;
		
		boolean getWriteAbbreviatedBuiltinFunctionSymbols() ;
		void setWriteAbbreviatedBuiltinFunctionSymbols( boolean abbreviateBuiltinFunctions ) ;
		
		boolean getUseTabsForIndentation() ;
		void setUseTabsForIndentation( boolean indentWithTabs ) ;
		
		int getNumberOfSpacesForIndentation() ;
		void setNumberOfSpacesForIndentation( int indentWithSpacesCount ) ;
		
		int getNumberOfSpacesForBlockIndentation() ;
		void setNumberOfSpacesForBlockIndentation( int indentBlockWithSpacesCount ) ;
		
		Integer getCommandsPerLineLimit() ;
		void setCommandsPerLineLimit( Integer commandsPerLineLimit ) ;
		
		Integer getCommentsPerLineLimit() ;
		void setCommentsPerLineLimit( Integer commentsPerLineLimit ) ;
	}

	private final RoutineNativeImmediateFormatter m_immediateFormatter ;
	private final RoutineDeferringFormatter m_deferringFormatter ;
	
	public RoutineNativeFormatter() {
		m_immediateFormatter = new RoutineNativeImmediateFormatter() ;
		m_deferringFormatter = new RoutineDeferringFormatter() ; 
	}
	
	public Options options() { return m_immediateFormatter.options() ; }
	
	private boolean deferring() { return m_inlineBlockDepth != 0 && m_capturingMultilineBlock != 0 ; }
	
	@Override
	protected void produce(FormattingCall formattingCall) throws IOException {
		formattingCall.executeOn( deferring() ? m_deferringFormatter : m_immediateFormatter ) ;
	}

	private int m_inlineBlockDepth = 0;
	private int m_capturingMultilineBlock = 0 ;
	
	@Override
	public void openInlineBlock(Writer writer) throws IOException {
		++m_inlineBlockDepth ;
//		System.out.println( "[" + m_inlineBlockDepth + "]" );
		super.openInlineBlock(writer);
	}

	@Override
	public void closeInlineBlock(Writer writer) throws IOException {
		super.closeInlineBlock(writer);
		--m_inlineBlockDepth ;
//		System.out.println( "[" + m_inlineBlockDepth + "]" );
		if ( m_inlineBlockDepth == 0 )
			m_deferringFormatter.playback( this );
	}
	
	@Override
	public void openMultilineBlock(Writer writer) throws IOException {
		if ( m_inlineBlockDepth != 0 ) {
			++m_capturingMultilineBlock  ;
		}
		super.openMultilineBlock(writer);
	}

	@Override
	public void closeMultilineBlock(Writer writer) throws IOException {
		super.closeMultilineBlock(writer);
		if ( m_inlineBlockDepth != 0 ) {
			--m_capturingMultilineBlock ;
		}
	}
	
}
