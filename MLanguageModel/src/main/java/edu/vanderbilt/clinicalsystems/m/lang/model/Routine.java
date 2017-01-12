package edu.vanderbilt.clinicalsystems.m.lang.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class Routine implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final MultilineBlock m_block = new MultilineBlock();
	
	public MultilineBlock root() { return m_block ; }
	
	public List<String> tagNames() { return m_block.tagNames() ; }
	
	public Iterator<RoutineElement> findTagByName( String name ) { return m_block.findTagByName( name ) ; }
	
	public String name() {
		Iterator<RoutineElement> i = m_block.elements().iterator();
		if ( !i.hasNext() )
			throw new IllegalStateException( "routine does not have a name tag yet" ) ;
		
		RoutineElement element = i.next() ;
		if ( !(element instanceof Tag) )
			throw new IllegalStateException( "first element of routine is not a tag (it's a \"" + element.getClass().getSimpleName() + "\")" ) ;
			
		Tag tag = (Tag)element;
		return tag.name() ;
	}
	
	public void appendElement( RoutineElement element ) { m_block.appendElement(element) ; }
	public void appendElements( MultilineBlock block ) { m_block.appendElements(block) ; }
	
	public void write( RoutineWriter writer ) throws RoutineWriterException { writer.write(this); }
	
	@Override public String toString() { return "Routine "  + (m_block.isEmpty() ? "<unnamed>" : "\"" + name() + "\"") + " " + m_block.toString() ; } 
}
