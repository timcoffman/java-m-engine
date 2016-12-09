package edu.vanderbilt.clinicalsystems.m.core.lib;

import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Function;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Library;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction;

@Library
public class Reflect {
	
	@Function(BuiltinFunction.DATA) public static native String dataType( Value value ) ;

	public enum StackInfoType { ECODE, MCODE, PLACE }
	@Function(BuiltinFunction.STACK) public static native String stackInfo( int depth ) ;
	@Function(BuiltinFunction.STACK) public static native String stackInfo( int depth, StackInfoType stackInfoType ) ;
	
}
