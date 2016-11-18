package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import java.util.Arrays;
import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class VariableList extends ElementListArgument<VariableReference> {
	
	public VariableList( VariableReference ... variables ) { super( Arrays.asList(variables) ) ; }

	public VariableList( List<? extends VariableReference> variables ) { super(variables) ; }
	
	@Override
	public <R> R visit( Visitor<R> visitor ) {
		return visitor.visitVariableList(this) ;
	}

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	} ;

}
