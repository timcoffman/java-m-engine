package edu.vanderbilt.clinicalsystems.m.lang.model.argument;


public class CarriageReturnCommand extends FormatCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CarriageReturnCommand() { }
	
	@Override public String text() { return "!" ; }
	
	@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitCarriageReturnCommand(this) ; }
}
