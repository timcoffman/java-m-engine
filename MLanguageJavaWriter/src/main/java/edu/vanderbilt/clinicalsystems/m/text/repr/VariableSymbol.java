package edu.vanderbilt.clinicalsystems.m.text.repr;

public interface VariableSymbol extends Symbol {
	
	VariableSymbol isAssigned(RepresentationNode source);
	
}
