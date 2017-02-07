package edu.vanderbilt.clinicalsystems.m.core.lib;

import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Command;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Function;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Variable;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinVariable;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;

@RoutineUnit
public class Reflect {
	
	@Function(BuiltinFunction.NAME) public static native String name( Value value ) ;
	@Function(BuiltinFunction.NAME) public static native String name( Value value, long depth ) ;
	
	@Function(BuiltinFunction.DATA) public static native String dataType( Value value ) ;

	public enum StackInfoType { ECODE, MCODE, PLACE }
	@Function(BuiltinFunction.STACK) public static native String stackInfo( int depth ) ;
	@Function(BuiltinFunction.STACK) public static native String stackInfo( int depth, StackInfoType stackInfoType ) ;
	
	@Variable(BuiltinVariable.STACK) public static native long stackDepth() ;
	
	@Command(CommandType.EXECUTE) public static native void inline( String code ) ; 
	
	@Variable(BuiltinVariable.JOB) public static native long job() ;
}
