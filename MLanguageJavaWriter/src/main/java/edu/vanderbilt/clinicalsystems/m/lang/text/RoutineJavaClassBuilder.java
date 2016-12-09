package edu.vanderbilt.clinicalsystems.m.lang.text;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;

public class RoutineJavaClassBuilder extends RoutineJavaBuilder {
	private final JDefinedClass m_definedClass ;
	
	public RoutineJavaClassBuilder( RoutineJavaBuilderContext builderContext, JDefinedClass definedClass ) {
		super(builderContext) ;
		m_definedClass = definedClass ;
	}
	
	public void build(Routine routine) {
		
		SymbolUsage symbolUsage = new SymbolUsage() ;
		
		for ( String tagName : routine.tagNames() ) {

			JMethod method ;
			if ( tagName.equals( routine.name() ) ) {
				
				method = m_definedClass.method( JMod.PUBLIC, codeModel()._ref(valueClass()), context().mainMethodName() );
				
			} else {
				
				String methodName = context().symbolForIdentifier( tagName ) ;
				method = m_definedClass.method( JMod.PUBLIC, codeModel()._ref(valueClass()), methodName ) ;
				
			}
			
			RoutineJavaMethodBuilder methodBuilder = new RoutineJavaMethodBuilder( context(), method, m_definedClass ) ;
			methodBuilder.build( routine, tagName ) ;
			
			symbolUsage.importUndeclared( methodBuilder.symbolUsage() );
		}
		
		for ( String symbol : symbolUsage.symbols() ) {
			
			m_definedClass.field( JMod.PUBLIC, symbolUsage.impliedType(symbol, context() ), symbol ) ;
			
		}
	}
	
}