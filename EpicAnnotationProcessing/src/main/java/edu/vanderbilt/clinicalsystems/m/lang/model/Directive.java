package edu.vanderbilt.clinicalsystems.m.lang.model;

public class Directive extends Comment {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DIRECTIVE_FORMAT = ";#%1$s#%2$s" ; // e.g. ;#lglob#
	
	public Directive(String directiveText) {
		this(directiveText, "") ;
	}

	public Directive(String directiveText, String argument ) {
		super( String.format(DIRECTIVE_FORMAT, directiveText, argument));
	}
	
	private static final Directive END_OF_TAG = new Directive("eof") ;
	private static final Directive END_OF_ROUTINE = new Directive("eor") ;

	public static Directive memberOfLibrary(String libraryName) { return new Directive("lglob",libraryName) ; }
	public static Directive endOfTag() { return END_OF_TAG ; }
	public static Directive endOfRoutine() { return END_OF_ROUTINE ; }
	
}
