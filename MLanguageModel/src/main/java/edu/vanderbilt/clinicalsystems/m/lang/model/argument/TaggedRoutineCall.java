package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.vanderbilt.clinicalsystems.m.lang.RoutineAccess;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;


public class TaggedRoutineCall extends Argument {

	private final String m_tagName ;
	private final String m_routineName ;
	private final RoutineAccess m_routineAccess ;
	private List<Expression> m_arguments = new ArrayList<Expression>() ;
	
	public TaggedRoutineCall( String tagName, String routineName, RoutineAccess routineAccess ) {
		Objects.requireNonNull(routineName, "missing routine name") ;
		Objects.requireNonNull(routineAccess, "missing routine access specifier") ;
		m_tagName = tagName ;
		m_routineName = routineName ;
		m_routineAccess = routineAccess ;
	}

	public TaggedRoutineCall( String tagName, String routineName, RoutineAccess routineAccess, List<Expression> arguments ) {
		this(tagName,routineName,routineAccess) ;
		m_arguments.addAll( arguments ) ;
	}

	public String tagName() { return m_tagName ; }
	public RoutineAccess routineAccess() { return m_routineAccess ; }
	public String routineName() { return m_routineName ; }
	public Iterable<Expression> arguments() { return m_arguments ; }
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	} ;	
	
	@Override
	protected String unformattedRepresentation() {
		return (m_routineName != null ? m_routineName : "" )
				+ (m_tagName != null && m_routineName != null ? "." : "")
				+ (m_tagName != null ? m_tagName : "" )
				+ "(" + m_arguments.stream().map((e)->e.toString()).collect( Collectors.joining(", ") ) + ")";
	}

}
