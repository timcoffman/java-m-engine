package edu.vanderbilt.clinicalsystems.m.lang.text;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;

public interface RoutineJavaBuilderContext {
	
	JCodeModel codeModel() ;
	
	JType typeFor( Representation representation ) ;
	JExpression initialValueFor(Representation representation);

	RoutineJavaBuilderEnvironment env();
	String mainMethodName();

	RoutineJavaBuilderClassContext classContext( String outerClassName ) ;
	
	String symbolForIdentifier(String variable);
	
	boolean isValueType(JavaExpression<?> source);
	boolean isValueType(JExpression source);

}