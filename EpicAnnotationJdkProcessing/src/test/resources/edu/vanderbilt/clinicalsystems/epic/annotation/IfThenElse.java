package edu.vanderbilt.clinicalsystems.epic.annotation;

import edu.vanderbilt.clinicalsystems.epic.annotation.EpicTag;

/**
 *  Conditional Block Sample
 */
@EpicRoutine
public class IfThenElse {
	
	@EpicTag public static void loopBody(int i) { }
	@EpicTag public static boolean sometimes() { return true ; }
	
	@EpicTag
	public static String ifThenElseAssign( int a ) {
		String z ;
		if ( a>7 ) z = ">7" ;
		else z = "<=7" ;
		return z ;
	}

	@EpicTag
	public static String ifThenElseReturn( int a ) {
		if ( a>7 ) return ">7" ;
		else return "<=7" ;
	}


}
