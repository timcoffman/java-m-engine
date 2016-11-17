package edu.vanderbilt.clinicalsystems.m.lang;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;

public enum BuiltinVariable {
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
	$X("X","X"),
	$Y("Y","Y");
	
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
	public String canonicalSymbol() { return m_canonicalSymbol ; }
	public String canoncialAbbreviation() { return m_canoncialAbbreviation ; }

	public static BuiltinVariable valueOfSymbol(String symbolOrAbbreviation, Compatibility ... additionalCompatibilities) {
		EnumSet<Compatibility> compatibility = EnumSet.of(Compatibility.ANSI_1995_X11_1);
		compatibility.addAll( Arrays.asList(additionalCompatibilities) ) ;
		
		Optional<BuiltinVariable> matchingSymbol =
			Arrays.stream(BuiltinVariable.values())
			.filter( bf->compatibility.containsAll( bf.m_compatibility ) )
			.filter( bf->bf.m_canonicalSymbol.equalsIgnoreCase(symbolOrAbbreviation) )
			.findFirst()
			;
		if ( matchingSymbol.isPresent() )
			return matchingSymbol.get() ;
		
		Optional<BuiltinVariable> matchingAbbreviation =
			Arrays.stream(BuiltinVariable.values())
			.filter( bf->compatibility.containsAll( bf.m_compatibility ) )
			.filter( bf->bf.m_canoncialAbbreviation.equalsIgnoreCase(symbolOrAbbreviation) )
			.findFirst()
			;
		
		return matchingAbbreviation
				.orElseThrow( ()->new IllegalArgumentException("\"" + symbolOrAbbreviation + "\" not recognized as a builtin variable") )
				;
	}
}
