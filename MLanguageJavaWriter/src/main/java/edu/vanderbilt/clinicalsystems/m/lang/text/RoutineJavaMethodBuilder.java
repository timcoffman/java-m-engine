package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.Writer;
import java.util.Iterator;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

import edu.vanderbilt.clinicalsystems.m.lang.model.ParameterName;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.Tag;
import edu.vanderbilt.clinicalsystems.m.text.repr.MethodSymbol;
import edu.vanderbilt.clinicalsystems.m.text.repr.SymbolScope;
import edu.vanderbilt.clinicalsystems.m.text.repr.VariableSymbol;

public class RoutineJavaMethodBuilder extends RoutineJavaBuilder<RoutineJavaBuilderClassContext> {
	
	private final JavaMethodContents m_methodContents ;
	private final SymbolScope m_outerSymbolScope ;
	
	public RoutineJavaMethodBuilder( SymbolScope outerSymbolScope, RoutineJavaBuilderClassContext builderContext, JavaMethodContents methodContents ) {
		super(builderContext) ;
		m_outerSymbolScope = outerSymbolScope ;
		m_methodContents = methodContents ;
	}
	
	private void buildComments( final JDocComment javadoc, Iterator<RoutineElement> elementIterator ) {
		RoutineNativeFormatter routineFormatter = new RoutineNativeFormatter();
		routineFormatter.options().setCommandsPerLineLimit(1);
		routineFormatter.options().setCommentsPerLineLimit(1);
		routineFormatter.options().setNumberOfSpacesForBlockIndentation(2);
		routineFormatter.options().setWriteAbbreviatedBuiltinFunctionSymbols(true);
		routineFormatter.options().setWriteAbbreviatedBuiltinVariableSymbols(true);
		routineFormatter.options().setWriteAbbreviatedCommandSymbols(true);
		
		try ( Writer docWriter = new JavaDocRoutineWriter(javadoc) ) {
			RoutineWriter writer = new RoutineLinearWriter(docWriter, routineFormatter ) ;
			
			while ( elementIterator.hasNext() ) {
				RoutineElement element = elementIterator.next() ;
				element.write(writer);
				if ( endOfMethod(element) )
					break ;
			}
		} catch ( Throwable ex ) {
			javadoc.append( ex.getClass().getSimpleName() ) ;
			javadoc.append( ": " ) ;
			javadoc.append( ex.getMessage() ) ;
		}
	}
	
	public static String symbolForMethodParameterPosition( String methodName, int position ) {
		return methodName + "|" + String.format("%04d",position) ;
	}
	
	public MethodSymbol declare(Routine routine, String tagName, String methodName) {
		MethodSymbol methodSymbol = env().representationInference().createMethodSymbol(m_outerSymbolScope, methodName) ;
		
		Iterator<RoutineElement> elementIterator = routine.findTagByName(tagName) ;
		Tag tag = ((Tag)elementIterator.next()) ;
		
		int position = 0 ;
		for ( ParameterName parameterName : tag.parameterNames() ) {
			methodSymbol.createParameter( position++, context().symbolForIdentifier( parameterName.name() ) ) ;
		}
		return methodSymbol ;
	}
	
	public Builder<JMethod> analyze(Routine routine, String tagName, String methodName) {
		MethodSymbol methodSymbol =
			m_outerSymbolScope.methodSymbolFor(methodName, -1 )
			.orElseThrow( ()->new IllegalStateException("method symbol \"" + methodName + "\" not found; use declare(...) prior to calling analyze(...)") )
			;
		
		RoutineJavaBlockBuilder blockBuilder = new RoutineJavaBlockBuilder( context(), methodSymbol.getBodyScope() ) ;
		
		Iterator<RoutineElement> elementIterator = routine.findTagByName(tagName) ;
		elementIterator.next() ;
		
		Builder<JBlock> bodyBuilder = blockBuilder.analyze( elementIterator ) ;

		return (m)->build( methodSymbol, routine, tagName, bodyBuilder, m ) ;
	}
	
	private void build( MethodSymbol methodSymbol, Routine routine, String tagName, Builder<JBlock> bodyBuilder, JMethod method ) {
		SymbolScope methodSymbolScope = methodSymbol.getBodyScope() ;
		buildComments( method.javadoc(), routine.findTagByName(tagName) ) ;
		
		Iterator<RoutineElement> elementIterator = routine.findTagByName(tagName) ;
		Tag tag = ((Tag)elementIterator.next()) ;

		method.type( context().typeFor( methodSymbol.representation() ) );
		for ( ParameterName parameterName : tag.parameterNames() ) {
			VariableSymbol symbol = methodSymbolScope.variableSymbolFor(context().symbolForIdentifier( parameterName.name() ) ).get() ;
			method.param( JMod.FINAL, context().typeFor( symbol.representation() ), symbol.getName() ) ;
		}
		
		switch ( m_methodContents ) {
		case STUB:
			/* nothing */
			break ;
		case EXECUTABLE:
		case IMPLEMENTATION:
		default:
			elementIterator = routine.findTagByName(tagName) ;
			elementIterator.next() ; // skip tag (second pass)
			bodyBuilder.build( method.body() ) ;
			break ;
		}
	}
}