package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;

import edu.vanderbilt.clinicalsystems.m.lang.model.ParameterName;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.Tag;

public class RoutineJavaMethodBuilder extends RoutineJavaBuilder {
	private JClass m_outerClass ;
	private JMethod m_method ;
	private final SymbolUsage m_symbolUsage ;

	public RoutineJavaMethodBuilder( RoutineJavaBuilderContext builderContext, JMethod method, JClass outerClass ) {
		super(builderContext) ;
		m_outerClass = outerClass ;
		m_method = method ;
		m_symbolUsage = new SymbolUsage() ;
	}
	
	public SymbolUsage symbolUsage() { return m_symbolUsage ; }
	
	private void buildComments( final JDocComment javadoc, Iterator<RoutineElement> elementIterator ) {
		Writer docWriter = new Writer() {
			private final StringBuffer m_lineBuffer = new StringBuffer();
			@Override public void write(char[] cbuf, int off, int len) throws IOException {
				for ( int i = 0; i < len ; ++i ) {
					char c = cbuf[off+i] ;
					m_lineBuffer.append(c) ;
					if ( c == '\n' )
						flush() ;
				}
			}
			@Override public void flush() throws IOException {
				if ( m_lineBuffer.length() != 0 ) {
					javadoc.append( m_lineBuffer.toString() ) ;
					m_lineBuffer.setLength(0);
				}
			}
			@Override public void close() throws IOException { flush() ; }
		} ;
		RoutineNativeFormatter routineFormatter = new RoutineNativeFormatter();
		routineFormatter.options().setCommandsPerLineLimit(1);
		routineFormatter.options().setCommentsPerLineLimit(1);
		routineFormatter.options().setNumberOfSpacesForBlockIndentation(2);
		routineFormatter.options().setWriteAbbreviatedBuiltinFunctionSymbols(true);
		routineFormatter.options().setWriteAbbreviatedBuiltinVariableSymbols(true);
		routineFormatter.options().setWriteAbbreviatedCommandSymbols(true);
		RoutineWriter writer = new RoutineLinearWriter(docWriter, routineFormatter ) ;
		
		try {
			while ( elementIterator.hasNext() ) {
				RoutineElement element = elementIterator.next() ;
				element.write(writer);
				if ( endOfMethod(element) )
					break ; ;
			}
		} catch ( Throwable ex ) {
			javadoc.append( ex.getClass().getSimpleName() ) ;
			javadoc.append( ": " ) ;
			javadoc.append( ex.getMessage() ) ;
		}
	}
	
	public void build( Routine routine, String tagName ) {
		buildComments( m_method.javadoc(), routine.findTagByName(tagName) ) ;
		
		Iterator<RoutineElement> elementIterator = routine.findTagByName(tagName) ;
		
		Tag tag = ((Tag)elementIterator.next()) ;
		
		for ( ParameterName parameterName : tag.parameterNames() ) {
			m_method.param( JMod.FINAL, codeModel()._ref(valueClass()), parameterName.name() ) ;
		}
		
		RoutineJavaBlockBuilder blockBuilder = new RoutineJavaBlockBuilder( context(), m_symbolUsage, m_method.body(), m_outerClass ) ;
		blockBuilder.build( elementIterator ) ;

		for ( ParameterName parameterName : tag.parameterNames() ) {
			String symbol = context().symbolForIdentifier( parameterName.name() );
			JType impliedType = blockBuilder.symbolUsage().impliedType( symbol, context() ) ;
			m_method.params().get(0).type( impliedType );
		}
		
	}
}