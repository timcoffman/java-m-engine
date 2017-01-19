package edu.vanderbilt.clinicalsystems.m.engine.virtual;

public interface Node extends NodeMap {

	String value() ;
	void assign( String newValue ) ;
	
	Object toObject(Class<?> ofType);
	
}
