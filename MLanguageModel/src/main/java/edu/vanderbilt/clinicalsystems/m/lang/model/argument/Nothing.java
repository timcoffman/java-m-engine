package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class Nothing extends Argument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final Nothing INSTANCE = new Nothing() ;
	
	private Nothing() { }

	@Override
	public <R> R visit( Visitor<R> visitor ) {
		return visitor.visitNothing(this) ;
	}
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	} ;	

	@Override
	protected String unformattedRepresentation() {
		return "(-)" ;
	}

}
