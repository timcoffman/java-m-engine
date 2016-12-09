package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class InvalidExpression extends Expression {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final InvalidExpression INSTANCE = new InvalidExpression("ERROR") ;
	
	public static InvalidExpression instance() { return INSTANCE ; }
	public static InvalidExpression instance( String reason ) { return new InvalidExpression(reason) ; }
	
	private final String m_reason ;
	public InvalidExpression(String reason) { m_reason = reason ; }
	
	public String reason() { return m_reason ; }
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write( this ) ;
	}

	@Override
	protected String unformattedRepresentation() { return m_reason ; }

}
