package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import java.util.Arrays;
import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class DeclarationList extends ElementListArgument<DirectVariableReference> {
	
	private static final long serialVersionUID = 1L;

	public DeclarationList( DirectVariableReference ... variables ) { super( Arrays.asList(variables) ) ; }

	public DeclarationList( List<? extends DirectVariableReference> declarations ) { super(declarations) ; }


	@Override
	protected void check(DirectVariableReference variable) throws IllegalArgumentException {
		super.check(variable);
		if ( variable.scope() != Scope.TRANSIENT )
			throw new IllegalArgumentException("variable reference provided for declaration must have local scope") ;
		if ( variable.referenceStyle() != ReferenceStyle.DIRECT )
			throw new IllegalArgumentException("variable reference provided for declaration must be a plain (not indirect) reference") ;
	}

	@Override
	public <R> R visit( Visitor<R> visitor ) {
		return visitor.visitDeclarationList(this) ;
	}
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	} ;

}
