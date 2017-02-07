package edu.vanderbilt.clinicalsystems.m.lang;

import java.util.Arrays;
import java.util.EnumSet;

public enum BuiltinFunction implements BuiltinSymbol {
	ASCII("ASCII","A"),
	CHAR("CHAR","C"),
	DATA("DATA","D"),
	EXTRACT("EXTRACT","E"),
	FIND("FIND","F"),
	FNUMBER("FNUMBER","FN"),
	GET("GET","G"),
	INCREMENT("INCREMENT","I", Compatibility.EXTENSION),
	JUSTIFY("JUSTIFY","J"),
	LENGTH("LENGTH","L"),
	NAME("NAME","NA"),
	ORDER("ORDER","O"),
	PIECE("PIECE","P"),
	QLENGTH("QLENGTH","QL"),
	QSUBSCRIPT("QSUBSCRIPT","QS"),
	QUERY("QUERY","Q"),
	RANDOM("RANDOM","R"),
	REVERSE("REVERSE","RE"),
	SELECT("SELECT","S"),
	STACK("STACK","ST"),
	TEXT("TEXT","T"),
	TRANSLATE("TRANSLATE","TR"),
	VIEW("VIEW","V");
	
	BuiltinFunction( String canonicalSymbol, String canoncialAbbreviation ) {
		this( canonicalSymbol, canoncialAbbreviation, Compatibility.ANSI_1995_X11_1 );
	}
	
	BuiltinFunction( String canonicalSymbol, String canoncialAbbreviation, Compatibility ... compatibility ) {
		this( canonicalSymbol, canoncialAbbreviation, EnumSet.copyOf( Arrays.asList(compatibility) ) ) ;
	}
	
	BuiltinFunction( String canonicalSymbol, String canoncialAbbreviation, EnumSet<Compatibility> compatibility ) {
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
	
	public static BuiltinFunction valueOfSymbol(String symbolOrAbbreviation, Compatibility ... additionalCompatibilities) {
		return BuiltinSymbolSupport.valueOfSymbol(BuiltinFunction.class, symbolOrAbbreviation, additionalCompatibilities) ;
	}
}
