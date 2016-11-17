package edu.vanderbilt.clinicalsystems.m.lang;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;

public enum BuiltinFunction {
	ASCII("ASCII","A"),
	CHAR("CHAR","C"),
	DATA("DATA","D"),
	EXTRACT("EXTRACT","E"),
	FIND("FIND","F"),
	FNUMBER("FNUMBER","F"),
	GET("GET","G"),
	INCREMENT("INCREMENT","I", Compatibility.EXTENSION),
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
	public String canonicalSymbol() { return m_canonicalSymbol ; }
	public String canoncialAbbreviation() { return m_canoncialAbbreviation ; }

	public static BuiltinFunction valueOfSymbol(String symbolOrAbbreviation, Compatibility ... additionalCompatibilities) {
		EnumSet<Compatibility> compatibility = EnumSet.of(Compatibility.ANSI_1995_X11_1);
		compatibility.addAll( Arrays.asList(additionalCompatibilities) ) ;
		
		Optional<BuiltinFunction> matchingSymbol =
			Arrays.stream(BuiltinFunction.values())
			.filter( bf->compatibility.containsAll( bf.m_compatibility ) )
			.filter( bf->bf.m_canonicalSymbol.equalsIgnoreCase(symbolOrAbbreviation) )
			.findFirst()
			;
		if ( matchingSymbol.isPresent() )
			return matchingSymbol.get() ;
		
		Optional<BuiltinFunction> matchingAbbreviation =
			Arrays.stream(BuiltinFunction.values())
			.filter( bf->compatibility.containsAll( bf.m_compatibility ) )
			.filter( bf->bf.m_canoncialAbbreviation.equalsIgnoreCase(symbolOrAbbreviation) )
			.findFirst()
			;
		
		return matchingAbbreviation
				.orElseThrow( ()->new IllegalArgumentException("\"" + symbolOrAbbreviation + "\" not recognized as a builtin function") )
				;
	}
}
