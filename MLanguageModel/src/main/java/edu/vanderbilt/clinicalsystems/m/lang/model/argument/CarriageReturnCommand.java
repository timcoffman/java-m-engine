package edu.vanderbilt.clinicalsystems.m.lang.model.argument;


public class CarriageReturnCommand extends FormatCommand {

	public CarriageReturnCommand() { }
	
	@Override public String text() { return "!" ; }
	
	@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitCarriageReturnCommand(this) ; }
}
