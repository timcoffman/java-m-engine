package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import java.util.Arrays;
import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;


public class AssignmentList extends ElementListArgument<Assignment> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AssignmentList( Assignment ... assignments ) { super( Arrays.asList(assignments) ) ; }

	public AssignmentList( List<? extends Assignment> assignments ) { super(assignments) ; }

	@Override
	public <R> R visit( Visitor<R> visitor ) {
		return visitor.visitAssignmentList(this) ;
	}

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	} ;	
}
