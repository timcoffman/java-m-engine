package edu.vanderbilt.clinicalsystems.m.lang;

public enum Scope {
	LOCAL(""),
	GLOBAL("^");

	private final String m_unformattedRepresentation ;
	Scope( String unformattedRepresentation ) { m_unformattedRepresentation = unformattedRepresentation ; }
	public String unformattedRepresentation() { return m_unformattedRepresentation ; }
}
