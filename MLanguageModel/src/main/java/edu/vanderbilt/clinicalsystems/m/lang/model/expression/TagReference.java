package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class TagReference extends Expression {
	private static final long serialVersionUID = 1L;
	private final String m_tagName ;
	private final String m_routineName ;
	
	public TagReference( String tagName, String routineName ) {
		if ( null == tagName && null == routineName )
			throw new IllegalArgumentException("missing both routine name and tag name") ;
		m_tagName = tagName ;
		m_routineName = routineName ;
	}
	
	public String tagName() { return m_tagName; }
	public String routineName() { return m_routineName ; }
	@Override
	protected String unformattedRepresentation() {
		
		if ( null == m_tagName ) {
			
			return "^" + m_routineName ;
			
		} else {
			
			return
					m_tagName
					+ (null != m_routineName ? m_routineName + "^" : "") 
					;
		}
	}
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write( this ) ;
	}
	
	@Override
	public boolean equals( Object obj ) {
		if ( this == obj ) return true ;
		if ( null == obj ) return false ;
		if ( !(obj instanceof TagReference) ) return false ;
		TagReference tagReference = (TagReference)obj ;
		return
			(       m_tagName == null ? tagReference.m_tagName     == null :     m_tagName.equals( tagReference.m_tagName     ) )
			&& (m_routineName == null ? tagReference.m_routineName == null : m_routineName.equals( tagReference.m_routineName ) )
			;
	}
}
