package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.util.Optional;
import java.util.function.Supplier;

public enum Representation {
	NATIVE,
	STRING,
	BOOLEAN,
	NUMERIC,
	INTEGER,
	DECIMAL,
	VOID;
	
	private Integer precedence() {
		switch ( this ) {
		case NATIVE : return 6 ;
		case STRING : return 5 ;
		case NUMERIC: return 4 ;
		case DECIMAL: return 3 ;
		case INTEGER: return 2 ;
		case BOOLEAN: return 1 ;
		case VOID:    return 0 ;
		default:      return null ;
		}
	}
	
	private final Supplier<Optional<Representation>> m_supplier = ()->Optional.of(this);
	
	public Supplier<Optional<Representation>> supplier() { return m_supplier ; }
	
	public boolean canRepresent( Representation other ) {
		return precedence() > other.precedence() ;
	}
	
	public Representation commonRepresentation( Representation other ) {
		return canRepresent( other ) ? this : other ;
	}
	
}