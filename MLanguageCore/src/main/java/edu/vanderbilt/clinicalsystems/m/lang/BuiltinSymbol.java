package edu.vanderbilt.clinicalsystems.m.lang;

import java.util.EnumSet;

public interface BuiltinSymbol {

	String canonicalSymbol() ;
	String canonicalAbbreviation() ;

	EnumSet<Compatibility> compatibility() ;

}
