package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import java.util.Arrays;
import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class InputOutputList extends ElementListArgument<InputOutput> {
	
	public InputOutputList( InputOutput ... inputOutputs ) { super( Arrays.asList(inputOutputs) ) ; }

	public InputOutputList( List<? extends InputOutput> inputOutputs ) { super(inputOutputs) ; }
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	} ;

}
