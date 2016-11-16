package edu.vanderbilt.clinicalsystems.m.lang;

public enum ParameterPassMethod {
	BY_VALUE(""),
	BY_REFERENCE(".");
	
	private final String m_unformattedRepresentation ;
	ParameterPassMethod( String unformattedRepresentation ) { m_unformattedRepresentation = unformattedRepresentation ; }
	public String unformattedRepresentation() { return m_unformattedRepresentation ; }
}
