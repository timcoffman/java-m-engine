package edu.vanderbilt.clinicalsystems.m.lang.model;

import java.util.HashMap;
import java.util.Map;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class Command implements RoutineElement {

	public static String HINT_FORMAT_KEEP_WITH_NEXT = "keep-with-next";
	
	private Map<String,Object> m_hints = null ;

	private final Expression m_condition ;
	private final CommandType m_commandType ;
	private final Argument m_argument ;
	private final Block m_block ;
	
	public Command( CommandType commandType ) {
		this( commandType, Argument.NOTHING ) ;
	}
	
	public Command( CommandType commandType, Argument argument ) {
		this( null, commandType, argument, null ) ;
	}

	public Command( Expression condition, CommandType commandType, Argument argument ) {
		this( condition, commandType, argument, null ) ;
	}

	public Command( CommandType commandType, Argument argument, Block block ) {
		this( null, commandType, argument, block ) ;
	}

	public Command( Expression condition, CommandType commandType, Argument argument, Block block ) {
		m_condition = condition ;
		m_commandType = commandType ;
		m_argument = argument ;
		m_block = block ;
		if ( null != m_condition && !commandType.allowsCondition() )
			throw new IllegalArgumentException( "command type \"" + commandType.canonicalSymbol() + "\" does not support a condition") ;
		if ( null != m_block && !commandType.allowsBlock() )
			throw new IllegalArgumentException( "command type \"" + commandType.canonicalSymbol() + "\" does not support a block") ;
	}

	@Override
	public Object hint(String hintName) { return null == m_hints ? null : m_hints.get(hintName) ;  }

	public Object applyHint( String hintName, Object value ) {
		if ( null == m_hints )
			m_hints = new HashMap<String,Object>() ;
		return m_hints.put( hintName, value ) ;
	}
	
	public Expression condition() { return m_condition ; }
	public CommandType commandType() { return m_commandType ; }
	public Argument argument() { return m_argument ; }
	public Block block() { return m_block ; }

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this) ;
	}

	@Override
	public String toString() {
		return m_commandType.canonicalSymbol()
				+ ( m_condition != null ? " : " + m_condition : "" )
				+ " | " + m_argument
				+ ( m_block != null ? " " + m_block : "" )
				;
	}
}
