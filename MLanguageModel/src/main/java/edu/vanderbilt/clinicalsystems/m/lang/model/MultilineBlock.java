package edu.vanderbilt.clinicalsystems.m.lang.model;

import java.util.Arrays;
import java.util.Collection;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class MultilineBlock extends Block {

	public MultilineBlock() {
		super() ;
	}
	
	@Override
	public Object hint(String hintName) { return null; }
	
	public MultilineBlock(RoutineElement ... elements) {
		this( Arrays.asList(elements) ) ;
	}
	
	public MultilineBlock( Collection<RoutineElement> elements ) {
		super( elements ) ;
	}
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this) ;
	}
	
}
