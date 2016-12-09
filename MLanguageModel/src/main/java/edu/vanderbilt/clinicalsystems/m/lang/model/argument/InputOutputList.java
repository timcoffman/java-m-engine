package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import java.util.Arrays;
import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class InputOutputList extends ElementListArgument<InputOutput> {
	
	private static final long serialVersionUID = 1L;

	public InputOutputList( InputOutput ... inputOutputs ) { super( Arrays.asList(inputOutputs) ) ; }

	public InputOutputList( List<? extends InputOutput> inputOutputs ) { super(inputOutputs) ; }

	@Override
	public <R> R visit( Visitor<R> visitor ) {
		return visitor.visitInputOutputList(this) ;
	}

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	} ;

}
