package edu.vanderbilt.clinicalsystems.m.text.repr;

import edu.vanderbilt.clinicalsystems.m.lang.text.Representation;

public interface Symbol extends EntityNode {
	SymbolScope getScope() ;
	String getName() ;
	
	void declaredAs( Representation representarion ) ;
}
