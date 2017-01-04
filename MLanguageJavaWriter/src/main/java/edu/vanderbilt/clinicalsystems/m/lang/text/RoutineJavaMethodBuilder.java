package edu.vanderbilt.clinicalsystems.m.lang.text;

import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.NATIVE;

import java.io.Writer;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Supplier;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;

import edu.vanderbilt.clinicalsystems.m.lang.model.ParameterName;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.Tag;

public class RoutineJavaMethodBuilder extends RoutineJavaBuilder<RoutineJavaBuilderClassContext> {
	
	private final SymbolUsage m_classSymbolUsage ;

	public RoutineJavaMethodBuilder( SymbolUsage classSymbolUsage, RoutineJavaBuilderClassContext builderContext ) {
		super(builderContext) ;
		m_classSymbolUsage = classSymbolUsage ;
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
	
	private String symbolForMethodParameterPosition( String methodName, int position ) {
		return methodName + "|" + String.format("%04d",position) ;
	}
	
	public Builder<JMethod> analyze(Routine routine, String tagName, String methodName) {
		SymbolUsage methodSymbolUsage = new SymbolUsage( m_classSymbolUsage ) {

			@Override public void scopeReturns(Supplier<Optional<Representation>> representation) {
				parent()
					.orElseThrow( ()->new IllegalStateException( "scope returned in root context" ) )
					.declaredAs( methodName, representation);
			}
			
			@Override public void scopeAccepts(int position, Supplier<Optional<Representation>> representation) {
				parent()
					.orElseThrow( ()->new IllegalStateException( "scope accepting in root context" ) )
					.declaredAs( symbolForMethodParameterPosition(methodName,position), representation);
			}
			
		};
		RoutineJavaBlockBuilder blockBuilder = new RoutineJavaBlockBuilder( context(), methodSymbolUsage ) ;
		
		Iterator<RoutineElement> elementIterator = routine.findTagByName(tagName) ;

		Tag tag = ((Tag)elementIterator.next()) ;
		
		for ( ParameterName parameterName : tag.parameterNames() ) {
			methodSymbolUsage.usedAsParameter( parameterName.name() ) ;
		}
		
		Builder<JBlock> bodyBuilder = blockBuilder.analyze( elementIterator ) ;

		return (m)->build( methodSymbolUsage, routine, tagName, bodyBuilder, m ) ;
	}
	
	private void build( SymbolUsage methodSymbolUsage, Routine routine, String tagName, Builder<JBlock> bodyBuilder, JMethod method ) {
		buildComments( method.javadoc(), routine.findTagByName(tagName) ) ;
		
		RoutineJavaBlockBuilder blockBuilder = new RoutineJavaBlockBuilder( context(), methodSymbolUsage ) ;
		
		Iterator<RoutineElement> elementIterator = routine.findTagByName(tagName) ;
		Tag tag = ((Tag)elementIterator.next()) ;
		blockBuilder.analyze( elementIterator ) ;
		
		for ( ParameterName parameterName : tag.parameterNames() ) {
			String symbol = context().symbolForIdentifier( parameterName.name() );
//			JType parameterType = context().typeFor( methodSymbolUsage.impliedRepresentation( symbol ).get().orElseThrow( ()->new IllegalStateException("unresolvable method symbol") ) ) ;
			JType parameterType = context().typeFor( methodSymbolUsage.impliedRepresentation( symbol ).get().orElse( NATIVE ) ) ;
			method.param( JMod.FINAL, parameterType, parameterName.name() ) ;
		}
		
		elementIterator = routine.findTagByName(tagName) ;
		elementIterator.next() ; // skip tag (second pass)
		bodyBuilder.build( method.body() ) ;
		
	}
}