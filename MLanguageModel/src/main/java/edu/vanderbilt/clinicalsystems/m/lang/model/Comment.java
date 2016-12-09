package edu.vanderbilt.clinicalsystems.m.lang.model;

import java.util.Objects;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class Comment implements RoutineElement {

	private static final long serialVersionUID = 1L;
	
	private final String m_text ;
	
	public Comment(String text) {
		Objects.requireNonNull(text) ;
		m_text = text;
	}

	@Override
	public Object hint( String hintName ) { return null ; }
	
	public String text() { return m_text ; }
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this) ;
	}

	@Override
	public String toString() { return "// " + m_text ; }
	
	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) return true ;
		if ( null == obj ) return false ;
		if ( !(obj instanceof Comment) ) return false ;
		Comment comment = (Comment)obj ;
		return
			m_text.equals( comment.m_text )
			;
	}
}
