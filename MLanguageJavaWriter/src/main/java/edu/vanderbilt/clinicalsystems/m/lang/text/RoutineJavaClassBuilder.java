package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.util.HashMap;
import java.util.Map;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;

import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;

public class RoutineJavaClassBuilder extends RoutineJavaBuilder<RoutineJavaBuilderContext> {
	public RoutineJavaClassBuilder( RoutineJavaBuilderContext builderContext ) {
		super(builderContext) ;
	}
	
	private String methodNameForTag(Routine routine, String tagName) {
		return tagName.equals( routine.name() ) ? context().mainMethodName() : context().symbolForIdentifier( tagName );
	}
	
	public Builder<JDefinedClass> analyze(Routine routine, String className) {
		SymbolUsage classSymbolUsage = SymbolUsage.createRoot() ;
		final RoutineJavaMethodBuilder methodBuilder = new RoutineJavaMethodBuilder( classSymbolUsage, context().classContext(className) ) ;
		
		routine.tagNames().forEach( (tagName)->classSymbolUsage.declaredAs( methodNameForTag(routine, tagName) ) );
		
		Map<String,Builder<JMethod>> methodBuilders = new HashMap<String, RoutineJavaBuilder.Builder<JMethod>>() ; 
		routine.tagNames().forEach( (tagName)->{
			
			String methodName = methodNameForTag(routine, tagName);
			Builder<JMethod> builder = methodBuilder.analyze( routine, tagName, methodName ) ;
			methodBuilders.put( methodName, builder ) ;
			
		} );
		
		return (c)->build( classSymbolUsage, methodBuilders, c )  ;
	}

	private void build(SymbolUsage classSymbolUsage, Map<String,Builder<JMethod>> methodBuilders, JDefinedClass definedClass) {
		System.out.println("") ;
		for ( String symbol : classSymbolUsage.symbols() ) {
			System.out.println( "\"" + symbol + "\": " + classSymbolUsage.describe(symbol) ) ;
		}
		
		for ( String symbol : classSymbolUsage.symbols() ) {
			
			if ( methodBuilders.containsKey(symbol) ) continue ; // they'll be handled from methodBuilders
			
			Representation repr = classSymbolUsage.impliedRepresentation(symbol).get().orElseThrow( ()->new IllegalStateException("unresolvable field symbol") ) ;
			definedClass.field( JMod.PUBLIC, context().typeFor(repr), symbol ) ;
			
		}

		methodBuilders.forEach( (methodName,methodBuilder)->{

			Representation repr = classSymbolUsage.impliedRepresentation(methodName).get().orElseThrow( ()->new IllegalStateException("unresolvable method symbol") ) ;
			JType returnType = context().typeFor( repr );
			JMethod method = definedClass.method( JMod.PUBLIC, returnType, methodName ) ;
			
			methodBuilder.build( method ) ;

		}) ;

	}
	
}