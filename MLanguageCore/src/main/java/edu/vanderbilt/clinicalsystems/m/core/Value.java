package edu.vanderbilt.clinicalsystems.m.core;

import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeCommandType.VALUE_ASSIGN;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeCommandType.VALUE_CLEAR;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeCommandType.VALUE_MERGE;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeFunctionType.IMPLICIT_CAST;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeFunctionType.NEXT_KEY;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeFunctionType.PREVIOUS_KEY;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeFunctionType.VALUE_INDEX;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeValueTypes.INITIAL_VALUE;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeCommand;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeFunction;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeType;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeValue;
@NativeType
public abstract class Value {
	
	@NativeValue(INITIAL_VALUE) public static native Value nullValue() ;
	public native boolean isNull() ;

	@NativeCommand(VALUE_CLEAR) public native void clear() ;
	
	@NativeCommand(VALUE_ASSIGN) public native void set( Value  value ) ;
	@NativeCommand(VALUE_ASSIGN) public native void set( String value ) ;
	@NativeCommand(VALUE_ASSIGN) public native void set( int    value ) ;
	@NativeCommand(VALUE_ASSIGN) public native void set( double value ) ;

	@Override
	@NativeFunction(IMPLICIT_CAST) public native String toString() ;
	@NativeFunction(IMPLICIT_CAST) public native int    toInt() ;
	@NativeFunction(IMPLICIT_CAST) public native double toDouble() ;
	
	@NativeFunction(NEXT_KEY) public native String nextKey( Value afterKey ) ;
	
	@NativeFunction(PREVIOUS_KEY) public native String prevKey( Value beforeKey ) ;
	
	@NativeFunction(VALUE_INDEX) public native Value get( Value  key ) ;
	@NativeFunction(VALUE_INDEX) public native Value get( String key ) ;
	@NativeFunction(VALUE_INDEX) public native Value get( int    key ) ;
	@NativeFunction(VALUE_INDEX) public native Value get( double key ) ;
	
	@NativeCommand(VALUE_MERGE) public native Value merge( Value value ) ;
	
}
