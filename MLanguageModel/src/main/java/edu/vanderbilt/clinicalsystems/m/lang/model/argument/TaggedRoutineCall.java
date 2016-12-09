package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.vanderbilt.clinicalsystems.m.lang.model.Element;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.TagReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;


public class TaggedRoutineCall implements Element {

	private static final long serialVersionUID = 1L;
	
	private final TagReference m_tagReference ;
	private List<Expression> m_arguments = new ArrayList<Expression>() ;
	
	public TaggedRoutineCall( TagReference tagReference ) {
		Objects.requireNonNull(tagReference, "missing tag reference") ;
		m_tagReference = tagReference ;
	}

	public TaggedRoutineCall( TagReference tagReference, List<Expression> arguments ) {
		this(tagReference) ;
		m_arguments.addAll( arguments ) ;
	}

	public TagReference tagReference() { return m_tagReference ; }
	public Iterable<Expression> arguments() { return m_arguments ; }
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	} ;	
	
	@Override
	public String toString() {
		return m_tagReference.toString()
				+ "(" + m_arguments.stream().map((e)->e.toString()).collect( Collectors.joining(", ") ) + ")";
	}

	@Override
	public boolean equals( Object obj ) {
		if ( this == obj ) return true ;
		if ( null == obj ) return false ;
		if ( !(obj instanceof TaggedRoutineCall) ) return false ;
		TaggedRoutineCall taggedRoutineCall = (TaggedRoutineCall)obj ;
		return
			m_tagReference.equals( taggedRoutineCall.m_tagReference )
			&& m_arguments.equals( taggedRoutineCall.m_arguments )
			;
	}
}
