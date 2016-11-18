package edu.vanderbilt.clinicalsystems.m.lang;

import java.util.EnumSet;

public enum OperatorType implements BuiltinSymbol {
	/* String */
	CONCAT      ("_"),
	CONTAINS    ("]"),
	SORTS_AFTER ("]]"),
	MATCH       ("?"),
	/* Math */
	ADD         ("+",true),
	SUBTRACT    ("-"),
	MULTIPLY    ("*",true),
	DIVIDE      ("/"),
	DIVIDE_INT  ("\\"),
	MODULO      ("#"),
	POWER       ("**"),
	/* Comparisons */
	EQUALS      ("="),
	LESS_THAN   ("<"),
	GREATER_THAN(">"),
	FOLLOWS     ("]"),
	NOT_EQUALS      ("'="),
	NOT_LESS_THAN   ("'<"),
	NOT_GREATER_THAN("'>"),
	NOT_FOLLOWS     ("']"),
	/* Logic */
	NOT         ("'"),
	AND         ("&"),
	OR          ("!"),
	/* Reference */
	INDIRECTION ("@")
	;
	
	OperatorType( String canonicalSymbol ) { this(canonicalSymbol,false) ; }
	OperatorType( String canonicalSymbol, boolean commutative ) { m_canonicalSymbol = canonicalSymbol ; m_commutative = commutative ; }
	private final String m_canonicalSymbol ;
	private final boolean m_commutative ;
	
	@Override public String canonicalSymbol() { return m_canonicalSymbol ; }
	@Override public String canonicalAbbreviation() { return m_canonicalSymbol ; }
	@Override public EnumSet<Compatibility> compatibility() { return EnumSet.of(Compatibility.ANSI_1995_X11_1) ; }
	
	public boolean commutative() { return m_commutative ; }

	public static OperatorType valueOfSymbol(String symbolOrAbbreviation, Compatibility ... additionalCompatibilities) {
		return BuiltinSymbolSupport.valueOfSymbol(OperatorType.class, symbolOrAbbreviation, additionalCompatibilities) ;
	}
}
