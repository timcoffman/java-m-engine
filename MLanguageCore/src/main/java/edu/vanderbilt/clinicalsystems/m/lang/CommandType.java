package edu.vanderbilt.clinicalsystems.m.lang;

import java.util.Arrays;
import java.util.Optional;

public enum CommandType {
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
	
	public String canonicalSymbol() { return m_canonicalSymbol ; }
	public String canoncialAbbreviation() { return m_canoncialAbbreviation ; }
	public boolean allowsCondition() { return m_allowsCondition ; }
	public boolean allowsBlock() { return m_allowsBlock ; }
	
	public static CommandType valueOfSymbol(String symbolOrAbbreviation) {
		Optional<CommandType> matchingSymbol =
			Arrays.stream(CommandType.values())
			.filter( ct->ct.m_canonicalSymbol.equalsIgnoreCase(symbolOrAbbreviation) )
			.findFirst()
			;
		if ( matchingSymbol.isPresent() )
			return matchingSymbol.get() ;
		
		Optional<CommandType> matchingAbbreviation =
			Arrays.stream(CommandType.values())
			.filter( ct->ct.m_canoncialAbbreviation.equalsIgnoreCase(symbolOrAbbreviation) )
			.findFirst()
			;
		
		return matchingAbbreviation
				.orElseThrow( ()->new IllegalArgumentException("\"" + symbolOrAbbreviation + "\" not recognized as a command type") )
				;
	}
}
