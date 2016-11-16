package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.Objects;

import edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle;
import edu.vanderbilt.clinicalsystems.m.lang.RoutineAccess;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class TagReference extends Expression {
	private final ReferenceStyle m_referenceStyle ;
	private final Scope m_scope ;
	private final String m_tagName ;
	private final RoutineAccess m_routineAccess ;
	private final String m_routineName ;
	
	public TagReference( Scope scope, ReferenceStyle referenceStyle, String tagName, String routineName, RoutineAccess routineAccess ) {
		Objects.requireNonNull(routineName, "missing routine name") ;
		Objects.requireNonNull(routineAccess, "missing routine access specifier") ;
		m_scope = scope ;
		m_referenceStyle = referenceStyle ;
		m_tagName = tagName ;
		m_routineName = routineName ;
		m_routineAccess = routineAccess ;
	}
	
	public Scope scope() { return m_scope; }
	public ReferenceStyle referenceStyle() { return m_referenceStyle; }
	public String tagName() { return m_tagName; }
	public RoutineAccess routineAccess() { return m_routineAccess ; }
	public String routineName() { return m_routineName ; }
	@Override
	protected String unformattedRepresentation() {
		return
				m_scope.unformattedRepresentation()
				+
				m_referenceStyle.unformattedRepresentation()
				+
				m_tagName
				+
				(null != m_routineName ? "^" + m_routineName : "") 
				+
				m_referenceStyle.unformattedRepresentation()
				;
	}
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write( this ) ;
	}
}
