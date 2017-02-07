package edu.vanderbilt.clinicalsystems.m.lang;

import edu.vanderbilt.clinicalsystems.m.lang.text.Representation;

public class VariableAssignment {
	private final String m_variableName ;
	private final Representation m_assignedValueOfType ;
	public VariableAssignment( String variableName, Representation assignedValueOfType ) {
		m_variableName = variableName ;
		m_assignedValueOfType = assignedValueOfType ;
	}
	
	public String getVariableName() { return m_variableName; }
	public Representation getAssignedValueOfType() { return m_assignedValueOfType; }
}