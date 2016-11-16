package edu.vanderbilt.clinicalsystems.m.core;

import static edu.vanderbilt.clinicalsystems.m.core.annotation.NativeCommandTypes.VALUE_ASSIGN;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.NativeCommandTypes.VALUE_INDEX_ASSIGN;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.NativeFunctionTypes.IMPLICIT_CAST;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.NativeFunctionTypes.NEXT_KEY;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.NativeFunctionTypes.PREVIOUS_KEY;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.NativeFunctionTypes.VALUE_INDEX;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.NativeValueTypes.INITIAL_VALUE;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeCommand;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeFunction;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeType;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeValue;
@NativeType
public abstract class Value {
	
	public native <T> ByRef<T> asReference() ;

	@NativeValue(INITIAL_VALUE) public static native Value nullValue() ;
	public native boolean isNull() ;

	@NativeCommand(VALUE_ASSIGN) public native void set( Value  value ) ;
	@NativeCommand(VALUE_ASSIGN) public native void set( String value ) ;
	@NativeCommand(VALUE_ASSIGN) public native void set( int    value ) ;
	@NativeCommand(VALUE_ASSIGN) public native void set( double v ) ;

	@Override
	@NativeFunction(IMPLICIT_CAST) public native String toString() ;
	@NativeFunction(IMPLICIT_CAST) public native int    toInt() ;
	@NativeFunction(IMPLICIT_CAST) public native double toDouble() ;
	
	@NativeFunction(NEXT_KEY) public native String nextKey( Value  afterKey ) ;
	
	@NativeFunction(PREVIOUS_KEY) public native String prevKey( Value  beforeKey ) ;
	
	@NativeFunction(VALUE_INDEX) public native Value get( Value  ... keys ) ;
	@NativeFunction(VALUE_INDEX) public native Value get( String ... keys ) ;
	@NativeFunction(VALUE_INDEX) public native Value get( int    ... keys ) ;
	@NativeFunction(VALUE_INDEX) public native Value get( double ... keys ) ;
	
	public native Value merge( Value value, double ... keys ) ;
	
	@NativeCommand(VALUE_INDEX_ASSIGN) public native Value put( Value  value, Value  ... keys ) ;
	@NativeCommand(VALUE_INDEX_ASSIGN) public native Value put( Value  value, String ... keys ) ;
	@NativeCommand(VALUE_INDEX_ASSIGN) public native Value put( Value  value, int    ... keys ) ;
	@NativeCommand(VALUE_INDEX_ASSIGN) public native Value put( Value  value, double ... keys ) ;
	
	@NativeCommand(VALUE_INDEX_ASSIGN) public native Value put( String value, Value  ... keys ) ;
	@NativeCommand(VALUE_INDEX_ASSIGN) public native Value put( String value, String ... keys ) ;
	@NativeCommand(VALUE_INDEX_ASSIGN) public native Value put( String value, int    ... keys ) ;
	@NativeCommand(VALUE_INDEX_ASSIGN) public native Value put( String value, double ... keys ) ;
	
	@NativeCommand(VALUE_INDEX_ASSIGN) public native Value put( int    value, Value  ... keys ) ;
	@NativeCommand(VALUE_INDEX_ASSIGN) public native Value put( int    value, String ... keys ) ;
	@NativeCommand(VALUE_INDEX_ASSIGN) public native Value put( int    value, int    ... keys ) ;
	@NativeCommand(VALUE_INDEX_ASSIGN) public native Value put( int    value, double ... keys ) ;
	
	@NativeCommand(VALUE_INDEX_ASSIGN) public native Value put( double value, Value  ... keys ) ;
	@NativeCommand(VALUE_INDEX_ASSIGN) public native Value put( double value, String ... keys ) ;
	@NativeCommand(VALUE_INDEX_ASSIGN) public native Value put( double value, int    ... keys ) ;
	@NativeCommand(VALUE_INDEX_ASSIGN) public native Value put( double value, double ... keys ) ;
	
}
