package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;

public abstract class BuiltinVariableReferenceBase extends VariableReference {
	
	private static final long serialVersionUID = 1L;

	public BuiltinVariableReferenceBase(ReferenceStyle referenceStyle) {
		super(referenceStyle);
	}

	public BuiltinVariableReferenceBase(ReferenceStyle referenceStyle, List<Expression> keys) {
		super(referenceStyle, keys);
	}

	public abstract Scope scope() ;

}