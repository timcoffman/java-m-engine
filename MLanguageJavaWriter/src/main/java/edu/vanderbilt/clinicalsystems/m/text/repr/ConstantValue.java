package edu.vanderbilt.clinicalsystems.m.text.repr;

import edu.vanderbilt.clinicalsystems.m.lang.text.Representation;

public interface ConstantValue extends EntityNode {
	Object getValue() ;
	Representation getRepresentation() ;
}
