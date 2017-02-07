package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.EnumSet;

import edu.vanderbilt.clinicalsystems.m.lang.BuiltinSymbol;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinSymbolSupport;
import edu.vanderbilt.clinicalsystems.m.lang.Compatibility;

public enum PatternMatchCode implements BuiltinSymbol {
	ALPHABETIC( "A", union( range('A','Z'), range('a','z') ) ),
	CONTROL(    "C", union( range(0,31), range(127,255) ) ),
	EVERYTHING( "E", union( range('A','Z'), range('a','z') ) ),
	LOWERCASE(  "L", range('a','z') ),
	NUMERIC(    "N", range('0','9') ),
	PUNCTUATION("P", union( range(32,47), range(58,64), range(91,96), range(123,126) ) ),
	UPPERCASE(  "U", range('A','Z') ) ;
	
	PatternMatchCode( String canonicalSymbol, PatternMatchCode.MatchDefinition matchDefinition ) {
		m_canonicalSymbol = canonicalSymbol ;
		m_matchDefinition = matchDefinition ;
	}
	
	private final String m_canonicalSymbol ;
	private final PatternMatchCode.MatchDefinition m_matchDefinition ;
	
	@Override public String canonicalSymbol() { return m_canonicalSymbol ; }
	@Override public String canonicalAbbreviation() { return m_canonicalSymbol ; }
	@Override public EnumSet<Compatibility> compatibility() { return EnumSet.of(Compatibility.ANSI_1995_X11_1) ; }
	
	public PatternMatchCode.MatchDefinition matchDefinition() { return m_matchDefinition ; }

	@Override public boolean matchesSymbol( String symbolOrAbbreviation ) {
		return BuiltinSymbolSupport.matchesSymbol( this, symbolOrAbbreviation ) ;
	}
	
	public static PatternMatchCode valueOfSymbol(String symbolOrAbbreviation, Compatibility ... additionalCompatibilities) {
		return BuiltinSymbolSupport.valueOfSymbol(PatternMatchCode.class, symbolOrAbbreviation, additionalCompatibilities) ;
	}

	private static PatternMatchCode.MatchDefinition union( PatternMatchCode.MatchDefinition ... matchDefinitions ) {
		return new MatchDefinition() ;
	}
	
	private static PatternMatchCode.MatchDefinition range( char min, char max ) {
		return new MatchDefinition() ;
	}
	
	private static PatternMatchCode.MatchDefinition range( int min, int max ) {
		return new MatchDefinition() ;
	}
	
	public static class MatchDefinition {
		
	}
	
}