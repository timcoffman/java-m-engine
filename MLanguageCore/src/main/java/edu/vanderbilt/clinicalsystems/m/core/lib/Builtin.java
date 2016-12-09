package edu.vanderbilt.clinicalsystems.m.core.lib;

import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Command;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Function;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Library;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Variable;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinVariable;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;

@Library
public class Builtin {
	
	@Function(BuiltinFunction.ORDER) public static native String followingKey( Value value ) ;
	@Function(BuiltinFunction.ORDER) public static native String followingKey( Value value, int direction ) ;

	@Command(CommandType.GOTO) public native static void jump( Runnable destination ) ;
	
	@Variable(BuiltinVariable.ECODE) public native static void ecode() ;
}
