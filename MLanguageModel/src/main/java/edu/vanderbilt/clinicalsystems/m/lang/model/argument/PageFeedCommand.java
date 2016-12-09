package edu.vanderbilt.clinicalsystems.m.lang.model.argument;


public class PageFeedCommand extends FormatCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PageFeedCommand() { }
	
	@Override public String text() { return "#" ; }
	
	@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitPageFeedCommand(this) ; }
	
}
