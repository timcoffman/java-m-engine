package edu.vanderbilt.clinicalsystems.m.core;

import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeType;

@NativeType
public final class ByRef<T> {
	
	public native T dereference() ;
	
}
