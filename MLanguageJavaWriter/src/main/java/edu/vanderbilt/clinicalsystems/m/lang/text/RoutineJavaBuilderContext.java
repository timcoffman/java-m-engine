package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.lang.ref.Reference;
import java.util.function.Consumer;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMethod;
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

	public interface EventListener {
		void createdClass( JDefinedClass definedClass, String routineName ) ;
		void createdMethod( JDefinedClass definedClass, JMethod method, String tagName ) ;
	}
	
	void listen( Reference<EventListener> listener );
	void remove( EventListener listener );
	void forEachListener( Consumer<EventListener> action ) ;
	
}