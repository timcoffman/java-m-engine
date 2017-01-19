package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

public class ColumnCommand extends FormatCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final long m_column ;
	
	public ColumnCommand(long column) { m_column = column ; }
	
	@Override public String text() { return "?" + Long.toString(m_column); }
	public long column() { return m_column ; }
	
	@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitColumnCommand(this) ; }
}
