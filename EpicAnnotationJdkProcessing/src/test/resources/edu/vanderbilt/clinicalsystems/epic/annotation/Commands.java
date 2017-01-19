package edu.vanderbilt.clinicalsystems.epic.annotation;

import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;
import edu.vanderbilt.clinicalsystems.m.core.lib.ReadWrite;

/**
 *  Commands Sample
 */
@RoutineUnit
public class Commands {
	
	/**
	 * Expect W "abc"
	 */
	@RoutineTag
	public static void readWriteString() {
		ReadWrite.write( "abc" ) ;
		ReadWrite.read( "abc" ) ;
	}

	/**
	 * Expect W "abc",!
	 */
	@RoutineTag
	public static void readWriteStringWithCarriageReturn() {
		ReadWrite.write( "abc", ReadWrite.newline() ) ;
		ReadWrite.read( "abc", ReadWrite.newline() ) ;
	}
	
	/**
	 * Expect W #,"abc"
	 */
	@RoutineTag
	public static void readWriteStringWithPageFeed() {
		ReadWrite.write( ReadWrite.clear(), "abc" ) ;
		ReadWrite.read( ReadWrite.clear(), "abc" ) ;
	}
	
	/**
	 * Expect W x
	 */
	@RoutineTag
	public static void readWriteVariable( String x ) {
		ReadWrite.write( x ) ;
		ReadWrite.read( x ) ;
	}
	
	/**
	 * Expect W x
	 */
	@RoutineTag
	public static void readWriteGeneralExpression( String x ) {
		ReadWrite.write( x+7 ) ;
		ReadWrite.read( x+7 ) ;
	}
	
}
