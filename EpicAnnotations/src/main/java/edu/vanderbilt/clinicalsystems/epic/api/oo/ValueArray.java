package edu.vanderbilt.clinicalsystems.epic.api.oo;

import static edu.vanderbilt.clinicalsystems.m.core.annotation.NativeCommandTypes.EXTENSION;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.NativeValueTypes.RESULT;
import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeCommand;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeValue;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeWrapperType;

/**
 * Provides a facade over a Value.
 * <p>
 * Usage:
 * <pre>
 * Value somePatients ;
 * 
 * ValueArray lines = new ValueArray( somePatients ) ;
 * lines.addLine( patientOne ) ;
 * lines.addLine( patientTwo) ;
 * lines.addLine( patientThree ) ;
 * 
 * for ( int lineNum = lines.firstLine() ; lineNum <= lines.lastLine() ; ++lineNum ) {
 *   Value patient = lines.getLine( lineNum ) ;
 * }
 * <pre>
 */
@NativeWrapperType
public class ValueArray {

	private final Value m_delegate ;
	public ValueArray( Value delegate ) { m_delegate = delegate ; }
	
	// NativeValueType.EXTENSION causes the method return value to be treated as an expression
	// produces tools().expression().generate( "1" ) ;
	@NativeValue(RESULT)
	public int firstLine() { return 1 ; }
	
	// NativeValueType.RESULT causes the return value to be treated as an expression
	// can only reference final fields
	// produces tools().expression().generate( "m_delegate.get(0).toInt()" ) ;
	// "m_delegate" has type Value, from "private final Value m_delegate"
	// "m_delegate" is local variable assigned to "private final Value m_delegate"
	// local variable assigned to "private final Value m_delegate" is the argument to "new ValueArray( ??? )"
	@NativeValue(RESULT)
	public int lastLine() { return m_delegate.get(0).toInt() ; }
	
	// NativeComandType.EXTENSION causes the method statements to be treated as commands
	@NativeCommand(EXTENSION)
	public void addLine( Value value ) {
		int nextLine = lastLine() + 1 ;
		m_delegate.put( 0, nextLine ) ;
		m_delegate.put( nextLine, value) ;
	}
	
	@NativeValue(RESULT)
	public Value getLine( int line ) { return m_delegate.get( line ) ; }
	
}
