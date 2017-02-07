package edu.vanderbilt.clinicalsystems.m.lang;

import edu.vanderbilt.clinicalsystems.m.lang.text.Representation;

/**
 * The given variable can hold and reproduce a value of the given type 
 */
public class VariableMustRepresentAtLeast {
	private final String m_variableName ;
	private final Representation m_representation ;
	public VariableMustRepresentAtLeast( String variableName, Representation representation ) {
		m_variableName = variableName ;
		m_representation = representation ;
	}
	
	public String getVariableName() { return m_variableName; }
	public Representation getRepresentation() { return m_representation; }
}