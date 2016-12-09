package edu.vanderbilt.clinicalsystems.m.lang.model;

import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.FunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.TagReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class RoutineFunctionCall extends FunctionCall {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final TagReference m_tagReference ;
	
	public RoutineFunctionCall( TagReference tagReference, Returning returning, List<Expression> arguments ) {
		super(returning,arguments) ;
		m_tagReference = tagReference ;
	}
	
	public TagReference tagReference() { return m_tagReference ; }

	@Override
	protected String unformattedFunctionName() { return "$$" + m_tagReference.tagName() + (null != m_tagReference.routineName() ? ("^" + m_tagReference.routineName()) : "" ) ; }

	@Override public <R> R visit(Visitor<R> visitor) { return visitor.visitRoutineFunctionCall(this) ;}

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write( this ) ;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) return true ;
		if ( !super.equals(obj) ) return false ;
		if ( !(obj instanceof RoutineFunctionCall) ) return false ;
		RoutineFunctionCall routineFunctionCall = (RoutineFunctionCall)obj ;
		return
				m_tagReference.equals( routineFunctionCall.m_tagReference )
			;
	}
}
