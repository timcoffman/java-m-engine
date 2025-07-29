package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.util.LinkedHashMap;
import java.util.Map;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;

import edu.vanderbilt.clinicalsystems.m.core.annotation.InjectRoutine;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.text.repr.ClassSymbol;
import edu.vanderbilt.clinicalsystems.m.text.repr.MethodSymbol;
import edu.vanderbilt.clinicalsystems.m.text.repr.Symbol;
import edu.vanderbilt.clinicalsystems.m.text.repr.SymbolScope;
import edu.vanderbilt.clinicalsystems.m.text.repr.VariableSymbol;

public class RoutineJavaClassBuilder extends RoutineJavaBuilder<RoutineJavaBuilderContext> {
	
	private final JavaMethodContents m_methodContents ;
	private final SymbolScope m_outerSymbolScope ;
	
	public RoutineJavaClassBuilder( RoutineJavaBuilderContext builderContext, SymbolScope outerSymbolScope, JavaMethodContents methodContents ) {
		super(builderContext) ;
		m_methodContents = methodContents ;
		m_outerSymbolScope = outerSymbolScope ;
	}
	
	private String methodNameForTag(Routine routine, String tagName) {
		return tagName.equals( routine.name() ) ? context().mainMethodName() : context().symbolForIdentifier( tagName );
	}
	
	public Builder<JDefinedClass> analyze(Routine routine, String className) {
		ClassSymbol classSymbol = env().representationInference().rootScope().createClass( className ) ;
		SymbolScope classSymbolScope = classSymbol.getDeclarationScope() ;
		
		final RoutineJavaMethodBuilder methodBuilder = new RoutineJavaMethodBuilder( classSymbolScope, context().classContext(className), m_methodContents ) ;
		
		routine.tagNames().forEach( (tagName)->{
				methodBuilder.declare( routine, tagName, methodNameForTag(routine, tagName) ) ;
		} );
		
		Map<String,Builder<JMethod>> methodBuilders = new LinkedHashMap<String, RoutineJavaBuilder.Builder<JMethod>>() ; 
		routine.tagNames().forEach( (tagName)->{
			
			String methodName = methodNameForTag(routine, tagName);
			Builder<JMethod> builder = methodBuilder.analyze( routine, tagName, methodName ) ;
			methodBuilders.put( tagName, builder ) ;
			
		} );
		
		return (c)->build( classSymbolScope, methodBuilders, routine, c )  ;
	}
		
	private void build(SymbolScope classSymbolScope, Map<String,Builder<JMethod>> methodBuilders, Routine routine, JDefinedClass definedClass) {
		System.out.println("") ;
//		env().representationInference().print( classSymbolScope, System.out);
		env().representationInference().print( System.out);
		
		if ( m_methodContents != JavaMethodContents.STUB ) {
		
			for ( Symbol symbol : classSymbolScope.allSymbols() ) {
				
				if ( symbol instanceof VariableSymbol ) {
				
					Representation repr = env().representationInference().representationFor(symbol) ;
					JFieldVar field = definedClass.field( JMod.PUBLIC, context().typeFor(repr), symbol.getName() ) ;
					field.annotate( InjectRoutine.class ) ;
					
				}
			}
		}

		methodBuilders.forEach( (tagName,methodBuilder)->{
			String methodName = methodNameForTag(routine, tagName) ;
			
			MethodSymbol symbol = classSymbolScope.methodSymbolFor( methodName, -1 ).get() ;
			Representation repr = symbol.representation() ;
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