package edu.vanderbilt.clinicalsystems.m.lang.text;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpression;

public interface RoutineJavaBuilderContext {
	JCodeModel codeModel() ;

	RoutineJavaBuilderEnvironment env();
	String mainMethodName();

	String symbolForIdentifier(String variable);

	boolean isValueType(JavaExpression<?> source);
	boolean isValueType(JExpression source);

}