package edu.vanderbilt.clinicalsystems.m.core.lib;

import edu.vanderbilt.clinicalsystems.m.core.annotation.Function;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Library;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction;

@Library
public class Builtin {
	
	@Function(BuiltinFunction.ASCII) public static native int ascii( String characters ) ; 
	@Function(BuiltinFunction.ASCII) public static native int ascii( String characters, int position ) ; 

	@Function(BuiltinFunction.CHAR) public static native String character( int ... ascii ) ;
}
