package edu.vanderbilt.clinicalsystems.m.lang;

public enum ReferenceStyle {
	DIRECT(""),
	INDIRECT("@");

	private final String m_unformattedRepresentation ;
	ReferenceStyle( String unformattedRepresentation ) { m_unformattedRepresentation = unformattedRepresentation ; }
	public String unformattedRepresentation() { return m_unformattedRepresentation ; }
}
