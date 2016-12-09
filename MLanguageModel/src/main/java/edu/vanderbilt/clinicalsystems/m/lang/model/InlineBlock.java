package edu.vanderbilt.clinicalsystems.m.lang.model;

import java.util.Arrays;
import java.util.Collection;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class InlineBlock extends Block {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InlineBlock() {
		super() ;
	}
	
	@Override
	public Object hint(String hintName) { return null; }
	
	public InlineBlock(RoutineElement ... elements) {
		this( Arrays.asList(elements) ) ;
	}
	
	public InlineBlock( Collection<RoutineElement> elements ) {
		super( elements ) ;
	}
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this) ;
	}
	
}
