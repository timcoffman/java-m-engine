package edu.vanderbilt.clinicalsystems.m.lang;

import java.util.Arrays;
import java.util.EnumSet;

public enum BuiltinVariable implements BuiltinSymbol {
	DEVICE("DEVICE","D"),
	ECODE("ECODE","EC"),
	ESTACK("ESTACK","ES"),
	ETRAP("ETRAP","ET"),
	HOROLOG("HOROLOG","H"),
	IO("IO","I"),
	JOB("JOB","J"),
	KEY("KEY","K"),
	PRINCIPAL("PRINCIPAL","P"),
	QUIT("QUIT","Q"),
	REFERENCE("REFERENCE","R"),
	STACK("STACK","ST"),
	STORAGE("STORAGE","S"),
	SYSTEM("SYSTEM","SY"),
	TEST("TEST","T"),
	X("X","X"),
	Y("Y","Y");
	
	BuiltinVariable( String canonicalSymbol, String canoncialAbbreviation ) {
		this( canonicalSymbol, canoncialAbbreviation, Compatibility.ANSI_1995_X11_1 );
	}
	
	BuiltinVariable( String canonicalSymbol, String canoncialAbbreviation, Compatibility ... compatibility ) {
		this( canonicalSymbol, canoncialAbbreviation, EnumSet.copyOf( Arrays.asList(compatibility) ) ) ;
	}
	
	BuiltinVariable( String canonicalSymbol, String canoncialAbbreviation, EnumSet<Compatibility> compatibility ) {
		m_canonicalSymbol = canonicalSymbol ;
		m_canoncialAbbreviation = canoncialAbbreviation ;
		m_compatibility = EnumSet.copyOf( compatibility ) ;
	}
	
	private final String m_canonicalSymbol ;
	private final String m_canoncialAbbreviation ;
	private final EnumSet<Compatibility> m_compatibility ;
	
	@Override public String canonicalSymbol() { return m_canonicalSymbol ; }
	@Override public String canonicalAbbreviation() { return m_canoncialAbbreviation ; }
	@Override public EnumSet<Compatibility> compatibility() { return EnumSet.copyOf(m_compatibility) ; }

	public static BuiltinVariable valueOfSymbol(String symbolOrAbbreviation, Compatibility ... additionalCompatibilities) {
		return BuiltinSymbolSupport.valueOfSymbol(BuiltinVariable.class, symbolOrAbbreviation, additionalCompatibilities) ;
	}

}
