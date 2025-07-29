package edu.vanderbilt.clinicalsystems.m.text.repr;


public interface MethodSymbol extends Symbol {
	SymbolScope getBodyScope();
	
	MethodParameter parameter(int position);
	MethodParameter parameter(String name);
	MethodParameter createParameter( int i, String name ) ;

	default void returns(RepresentationNode representationNode) { getBodyScope().returns(representationNode); }
}
