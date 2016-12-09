package edu.vanderbilt.clinicalsystems.m.lang;

import java.util.Arrays;
import java.util.EnumSet;

public enum BuiltinSystemVariable implements BuiltinSymbol {
	GLOBAL("GLOBAL","G"),
	JOB("JOB","J"),
	LOCK("LOCK","L"),
	ROUTINE("ROUTINE","R"),
	SYSTEM("SYSTEM","S");
	
	BuiltinSystemVariable( String canonicalSymbol, String canoncialAbbreviation ) {
		this( canonicalSymbol, canoncialAbbreviation, Compatibility.ANSI_1995_X11_1 );
	}
	
	BuiltinSystemVariable( String canonicalSymbol, String canoncialAbbreviation, Compatibility ... compatibility ) {
		this( canonicalSymbol, canoncialAbbreviation, EnumSet.copyOf( Arrays.asList(compatibility) ) ) ;
	}
	
	BuiltinSystemVariable( String canonicalSymbol, String canoncialAbbreviation, EnumSet<Compatibility> compatibility ) {
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

	@Override public boolean matchesSymbol( String symbolOrAbbreviation ) {
		return BuiltinSymbolSupport.matchesSymbol( this, symbolOrAbbreviation ) ;
	}
	
	public static BuiltinSystemVariable valueOfSymbol(String symbolOrAbbreviation, Compatibility ... additionalCompatibilities) {
		return BuiltinSymbolSupport.valueOfSymbol(BuiltinSystemVariable.class, symbolOrAbbreviation, additionalCompatibilities) ;
	}
}
