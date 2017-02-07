package edu.vanderbilt.clinicalsystems.m.lang.text;

import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.NATIVE;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;

import edu.vanderbilt.clinicalsystems.m.core.annotation.InjectRoutine;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;

public class RoutineJavaClassBuilder extends RoutineJavaBuilder<RoutineJavaBuilderContext> {
	
	private JavaMethodContents m_methodContents ; 
	
	public RoutineJavaClassBuilder( RoutineJavaBuilderContext builderContext, JavaMethodContents methodContents ) {
		super(builderContext) ;
		m_methodContents = methodContents ;
	}
	
	private String methodNameForTag(Routine routine, String tagName) {
		return tagName.equals( routine.name() ) ? context().mainMethodName() : context().symbolForIdentifier( tagName );
	}
	
	public Builder<JDefinedClass> analyze(Routine routine, String className) {
		SymbolUsage classSymbolUsage = SymbolUsage.createRoot() ;
		final RoutineJavaMethodBuilder methodBuilder = new RoutineJavaMethodBuilder( classSymbolUsage, context().classContext(className), m_methodContents ) ;
		
		routine.tagNames().forEach( (tagName)->classSymbolUsage.declaredAs( methodNameForTag(routine, tagName) ) );
		
		Map<String,Builder<JMethod>> methodBuilders = new LinkedHashMap<String, RoutineJavaBuilder.Builder<JMethod>>() ; 
		routine.tagNames().forEach( (tagName)->{
			
			String methodName = methodNameForTag(routine, tagName);
			Builder<JMethod> builder = methodBuilder.analyze( routine, tagName, methodName ) ;
			methodBuilders.put( tagName, builder ) ;
			
		} );
		
		return (c)->build( classSymbolUsage, methodBuilders, routine, c )  ;
	}

	private static final java.util.regex.Pattern NON_SYMBOL_CHARACTER_PATTERN
		= java.util.regex.Pattern.compile("[^$a-zA-Z0-9_]")
		;
	
	private void build(SymbolUsage classSymbolUsage, Map<String,Builder<JMethod>> methodBuilders, Routine routine, JDefinedClass definedClass) {
		System.out.println("") ;
		for ( String symbol : classSymbolUsage.symbols() ) {
			System.out.println( "\"" + symbol + "\": " + classSymbolUsage.describe(symbol) ) ;
		}
		
		Set<String> symbols = new HashSet<String>( classSymbolUsage.symbols() );
		methodBuilders.keySet().forEach( (tagName)->symbols.remove( methodNameForTag(routine, tagName) ) );
		
		if ( m_methodContents != JavaMethodContents.STUB ) {
		
			for ( String symbol : symbols ) {
				
				if ( methodBuilders.containsKey(symbol) ) continue ; // they'll be handled from methodBuilders
				if ( methodBuilders.containsKey(symbol.split("[^$a-zA-Z0-9_]")[0] ) ) continue ; // they'll be handled from methodBuilders
				if ( NON_SYMBOL_CHARACTER_PATTERN.matcher(symbol).find() ) continue ; // not handled by methodBuilders, but is an "external" method
				
				// Representation repr = classSymbolUsage.impliedRepresentation(symbol).get().orElseThrow( ()->new IllegalStateException("unresolvable field symbol") ) ;
				Representation repr = classSymbolUsage.impliedRepresentation(symbol).get().orElse( NATIVE ) ;
				JFieldVar field = definedClass.field( JMod.PUBLIC, context().typeFor(repr), symbol ) ;
				field.annotate( InjectRoutine.class ) ;
			}
		}

		methodBuilders.forEach( (tagName,methodBuilder)->{
			String methodName = methodNameForTag(routine, tagName) ;
			
//			Representation repr = classSymbolUsage.impliedRepresentation(methodName).get().orElseThrow( ()->new IllegalStateException("unresolvable method symbol") ) ;
			Representation repr = classSymbolUsage.impliedRepresentation(methodName).get().orElse( NATIVE ) ;
			JType returnType = context().typeFor( repr );
			
			JMethod method ;
			switch ( m_methodContents ) {
			case IMPLEMENTATION:
				method = definedClass.method( JMod.PUBLIC, returnType, methodName ) ;
				method.annotate( Override.class ) ;
				break ;
			case EXECUTABLE:
				method = definedClass.method( JMod.PUBLIC, returnType, methodName ) ;
				break ;
			default:
			case STUB:
				method = definedClass.method( JMod.NONE, returnType, methodName ) ;
				break;
			}
			methodBuilder.build( method ) ;
			context().forEachListener( (el)->el.createdMethod(definedClass, method, tagName ) ) ;

		}) ;

	}
	
}