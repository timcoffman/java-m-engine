package edu.vanderbilt.clinicalsystems.m.lang.model;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class Routine {
	
	private final MultilineBlock m_block = new MultilineBlock();
	
	public MultilineBlock block() { return m_block ; }
	
	public void appendElement( RoutineElement element ) { m_block.appendElement(element) ; }
	public void appendElements( MultilineBlock block ) { m_block.appendElements(block) ; }
	
	public void write( RoutineWriter writer ) throws RoutineWriterException { writer.write(this); }
	
}
