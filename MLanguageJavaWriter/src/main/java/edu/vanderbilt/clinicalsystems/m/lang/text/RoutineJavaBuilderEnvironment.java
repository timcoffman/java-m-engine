package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.lang.reflect.Method;

import edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeCommandType;
import edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeFunctionType;
import edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeValueTypes;
import edu.vanderbilt.clinicalsystems.m.core.annotation.support.ReadWriteCodeType;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinVariable;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.Compatibility;
import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;

public interface RoutineJavaBuilderEnvironment {

	public interface Resolver {
		String namedLibrary( Class<?> environmentClass ) ;
		String namedMethod( Method method ) ;
	}
	
	Class<?> valueClass() ;
	Method methodFor(NativeValueTypes nativeValueType);
	Method methodFor(NativeFunctionType nativeFunctionType);
	Method methodFor(NativeCommandType nativeCommandType);
	
	Method methodFor(BuiltinFunction builtinFunction, Integer numberOfParameters, boolean forAssignment);
	Method methodFor(BuiltinVariable builtinVariable);
	Method methodFor(OperatorType operatorType);
	Method methodFor(CommandType commandType);
	
	Method methodFor(ReadWriteCodeType readWriteCodeType);
	
	Class<?> typeFor(Representation representation);
	
	Class<?> classForRoutine( String routineName ) ;
	Method methodFor( String routineName, String tagName ) ;

	RoutineJavaBuilderEnvironment additionalCompatibility(Compatibility additionalCompatibility);
	RoutineJavaBuilderEnvironment use( Resolver resolver ) ;
	RoutineJavaBuilderEnvironment use( Class<?> environmentClass ) ;

}
