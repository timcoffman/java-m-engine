package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import java.util.Arrays;
import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class TaggedRoutineCallList extends ElementListArgument<TaggedRoutineCall> {
	
	private static final long serialVersionUID = 1L;

	public TaggedRoutineCallList( TaggedRoutineCall ... taggedRoutineCalls ) { super( Arrays.asList(taggedRoutineCalls) ) ; }

	public TaggedRoutineCallList( List<? extends TaggedRoutineCall> taggedRoutineCalls ) { super(taggedRoutineCalls) ; }


	@Override
	protected void check(TaggedRoutineCall taggedRoutineCall) throws IllegalArgumentException {
		super.check(taggedRoutineCall);
	}

	@Override
	public <R> R visit( Visitor<R> visitor ) {
		return visitor.visitTaggedRoutineCallList(this) ;
	}
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	} ;

}
