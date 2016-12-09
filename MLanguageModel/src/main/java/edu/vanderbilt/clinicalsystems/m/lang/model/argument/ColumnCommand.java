package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

public class ColumnCommand extends FormatCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int m_column ;
	
	public ColumnCommand(int column) { m_column = column ; }
	
	@Override public String text() { return "?" + Integer.toString(m_column); }
	public int column() { return m_column ; }
	
	@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitColumnCommand(this) ; }
}
