package edu.vanderbilt.clinicalsystems.m.lang.text;

import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.NATIVE;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;

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
		final RoutineJavaMethodBuilder methodBuilder = new RoutineJavaMethodBuilder( classSymbolUsage, context().classContext(className) ) ;
		
		routine.tagNames().forEach( (tagName)->classSymbolUsage.declaredAs( methodNameForTag(routine, tagName) ) );
		
		Map<String,Builder<JMethod>> methodBuilders = new LinkedHashMap<String, RoutineJavaBuilder.Builder<JMethod>>() ; 
		routine.tagNames().forEach( (tagName)->{
			
			String methodName = methodNameForTag(routine, tagName);
			Builder<JMethod> builder = methodBuilder.analyze( routine, tagName, methodName ) ;
			methodBuilders.put( tagName, builder ) ;
			
		} );
		
		return (c)->build( classSymbolUsage, methodBuilders, routine, c )  ;
	}

	private void build(SymbolUsage classSymbolUsage, Map<String,Builder<JMethod>> methodBuilders, Routine routine, JDefinedClass definedClass) {
		System.out.println("") ;
		for ( String symbol : classSymbolUsage.symbols() ) {
			System.out.println( "\"" + symbol + "\": " + classSymbolUsage.describe(symbol) ) ;
		}
		
		Set<String> symbols = new HashSet<String>( classSymbolUsage.symbols() );
		methodBuilders.keySet().forEach( (tagName)->symbols.remove( methodNameForTag(routine, tagName) ) );
		
		for ( String symbol : symbols ) {
			
			if ( methodBuilders.containsKey(symbol) ) continue ; // they'll be handled from methodBuilders
			
			// Representation repr = classSymbolUsage.impliedRepresentation(symbol).get().orElseThrow( ()->new IllegalStateException("unresolvable field symbol") ) ;
			Representation repr = classSymbolUsage.impliedRepresentation(symbol).get().orElse( NATIVE ) ;
			definedClass.field( JMod.PUBLIC + JMod.STATIC, context().typeFor(repr), symbol ) ;
		}

		methodBuilders.forEach( (tagName,methodBuilder)->{
			String methodName = methodNameForTag(routine, tagName) ;
			
//			Representation repr = classSymbolUsage.impliedRepresentation(methodName).get().orElseThrow( ()->new IllegalStateException("unresolvable method symbol") ) ;
			Representation repr = classSymbolUsage.impliedRepresentation(methodName).get().orElse( NATIVE ) ;
			JType returnType = context().typeFor( repr );
			
			JMethod method ;
			switch ( m_methodContents ) {
			case STUB:
				method = definedClass.method( JMod.PUBLIC + JMod.STATIC + JMod.NATIVE, returnType, methodName ) ;
				break;
			
			case EXECUTABLE:
			default:
				method = definedClass.method( JMod.PUBLIC + JMod.STATIC, returnType, methodName ) ;
				
				methodBuilder.build( method ) ;
				break ;
			}
			context().forEachListener( (el)->el.createdMethod(definedClass, method, tagName ) ) ;

		}) ;

	}
	
}