package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import java.util.Arrays;
import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class DeclarationList extends ElementListArgument<VariableReference> {
	
	public DeclarationList( VariableReference ... variables ) { super( Arrays.asList(variables) ) ; }

	public DeclarationList( List<? extends VariableReference> declarations ) { super(declarations) ; }

	@Override
	protected void check(VariableReference variable) throws IllegalArgumentException {
		super.check(variable);
		if ( variable.scope() != Scope.LOCAL )
			throw new IllegalArgumentException("variable reference provided for declaration must have local scope") ;
		if ( variable.referenceStyle() != ReferenceStyle.DIRECT )
			throw new IllegalArgumentException("variable reference provided for declaration must be a plain (not indirect) reference") ;
	}

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	} ;

}
