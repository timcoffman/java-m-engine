package edu.vanderbilt.clinicalsystems.m.lang;

import java.util.Arrays;
import java.util.Optional;

public enum BuiltinFunction {
	ASCII("ASCII","A"),
	CHAR("CHAR","C"),
	DATA("DATA","D"),
	EXTRACT("EXTRACT","E"),
	FIND("FIND","F"),
	FNUMBER("FNUMBER","F"),
	GET("GET","G"),
	JUSTIFY("JUSTIFY","J"),
	LENGTH("LENGTH","L"),
	NAME("NAME","N"),
	ORDER("ORDER","O"),
	PIECE("PIECE","P"),
	QLENGTH("QLENGTH","Q"),
	QSUBSCRIPT("QSUBSCRIPT","Q"),
	QUERY("QUERY","Q"),
	RANDOM("RANDOM","R"),
	REVERSE("REVERSE","R"),
	SELECT("SELECT","S"),
	STACK("STACK","S"),
	TEXT("TEXT","T"),
	TRANSLATE("TRANSLATE","T"),
	VIEW("VIEW","V");
	
	BuiltinFunction( String canonicalSymbol, String canoncialAbbreviation ) {
		m_canonicalSymbol = canonicalSymbol ;
		m_canoncialAbbreviation = canoncialAbbreviation ;
	}
	private final String m_canonicalSymbol ;
	private final String m_canoncialAbbreviation ;
	public String canonicalSymbol() { return m_canonicalSymbol ; }
	public String canoncialAbbreviation() { return m_canoncialAbbreviation ; }

	public static BuiltinFunction valueOfSymbol(String symbolOrAbbreviation) {
		Optional<BuiltinFunction> matchingSymbol =
			Arrays.stream(BuiltinFunction.values())
			.filter( ct->ct.m_canonicalSymbol.equalsIgnoreCase(symbolOrAbbreviation) )
			.findFirst()
			;
		Optional<BuiltinFunction> matchingAbbreviation =
			Arrays.stream(BuiltinFunction.values())
			.filter( ct->ct.m_canoncialAbbreviation.equalsIgnoreCase(symbolOrAbbreviation) )
			.findFirst()
			;
		
		return matchingSymbol
				.orElse(matchingAbbreviation
						.orElseThrow( ()->new IllegalArgumentException("\"" + symbolOrAbbreviation + "\" not recognized as a command type") )
						);
	}
}
