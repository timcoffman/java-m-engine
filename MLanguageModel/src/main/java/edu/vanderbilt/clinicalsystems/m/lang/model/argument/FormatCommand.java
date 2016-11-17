package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import java.util.Objects;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class FormatCommand extends InputOutput {

	private static final FormatCommand CARRIAGE_RETURN = new FormatCommand("!") ;
	private static final FormatCommand PAGE_FEED = new FormatCommand("#") ;
	
	private final String m_text ;
	
	protected FormatCommand( String text ) {
		Objects.requireNonNull(text);
		m_text = text ;
	}
	
	public static FormatCommand carriageReturn() { return CARRIAGE_RETURN ; }
	public static FormatCommand pageFeed() { return PAGE_FEED ; }
	public static FormatCommand column( int col ) { return new FormatCommand( "?" + Integer.toString(col) ) ; }
	
	public String text() { return m_text ; }
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	}

	@Override public String toString() { return text() ; }
	
}
