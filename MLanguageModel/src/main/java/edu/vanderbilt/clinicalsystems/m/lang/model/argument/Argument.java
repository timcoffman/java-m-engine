package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import edu.vanderbilt.clinicalsystems.m.lang.model.Element;

public abstract class Argument implements Element {

	public static final Argument NOTHING = Nothing.INSTANCE ;
	
	@Override
	public String toString() {
		return unformattedRepresentation();
	}

	protected abstract String unformattedRepresentation() ;
}
