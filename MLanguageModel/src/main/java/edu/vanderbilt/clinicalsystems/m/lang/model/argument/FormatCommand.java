package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public abstract class FormatCommand extends InputOutput {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final FormatCommand CARRIAGE_RETURN = new CarriageReturnCommand() ;
	private static final FormatCommand PAGE_FEED = new PageFeedCommand();
	
	protected FormatCommand() { }
	
	public static FormatCommand carriageReturn() { return CARRIAGE_RETURN ; }
	public static FormatCommand pageFeed() { return PAGE_FEED ; }
	public static FormatCommand column( long col ) { return new ColumnCommand( col ); }
	
	public abstract String text() ;
	
	@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitFormatCommand(this) ; }

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	}

	@Override public String toString() { return text() ; }
	
	@Override public boolean equals( Object obj ) {
		if ( null == obj ) return false ;
		if ( this == obj ) return true ;
		if ( !(obj instanceof FormatCommand) ) return false ;
		FormatCommand formatCommand = (FormatCommand)obj ;
		return text().equals( formatCommand.text() ) ;
	}
}
