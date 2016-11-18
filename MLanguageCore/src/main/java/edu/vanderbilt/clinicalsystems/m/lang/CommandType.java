package edu.vanderbilt.clinicalsystems.m.lang;

import java.util.EnumSet;

public enum CommandType implements BuiltinSymbol {
	BREAK("BREAK","B"),
	CLOSE("CLOSE","C"),
	DO("DO","D",true, true),
	ELSE("ELSE","E",false,true),
	FOR("FOR","F",false, true),
	GOTO("GOTO","G"),
	HALT("HALT","H"),
	HANG("HANG","H"),
	IF("IF","I",false,true),
	JOB("JOB","J"),
	KILL("KILL","K"),
	LOCK("LOCK","L"),
	MERGE("MERGE","M"),
	NEW("NEW","N"),
	OPEN("OPEN","O"),
	QUIT("QUIT","Q"),
	READ("READ","R"),
	SET("SET","S"),
	USE("USE","U"),
	VIEW("VIEW","V"),
	WRITE("WRITE","W"),
	EXECUTE("XECUTE","X");
	
	CommandType( String canonicalSymbol, String canoncialAbbreviation ) {
		this(canonicalSymbol, canoncialAbbreviation, true, false ) ;
	}
	CommandType( String canonicalSymbol, String canoncialAbbreviation, boolean allowsCondition, boolean allowsBlock ) {
		m_canonicalSymbol = canonicalSymbol ;
		m_canoncialAbbreviation = canoncialAbbreviation ;
		m_allowsCondition = allowsCondition ;
		m_allowsBlock = allowsBlock ;
	}
	
	private final String m_canonicalSymbol ;
	private final String m_canoncialAbbreviation ;
	private final boolean m_allowsCondition ;
	private final boolean m_allowsBlock ;
	
	@Override public String canonicalSymbol() { return m_canonicalSymbol ; }
	@Override public String canonicalAbbreviation() { return m_canoncialAbbreviation ; }
	@Override public EnumSet<Compatibility> compatibility() { return EnumSet.of(Compatibility.ANSI_1995_X11_1) ; }
	
	public boolean allowsCondition() { return m_allowsCondition ; }
	public boolean allowsBlock() { return m_allowsBlock ; }

	public static CommandType valueOfSymbol(String symbolOrAbbreviation, Compatibility ... additionalCompatibilities) {
		return BuiltinSymbolSupport.valueOfSymbol(CommandType.class, symbolOrAbbreviation, additionalCompatibilities) ;
	}

}
