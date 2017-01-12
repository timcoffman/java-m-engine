package edu.vanderbilt.clinicalsystems.epic.annotation;

import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit ;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag ;

/**
 *  Conditional Block Sample
 */
@RoutineUnit
public class IfThenElse {
	
	@RoutineTag public static void loopBody(int i) { }
	@RoutineTag public static boolean sometimes() { return true ; }
	
	@RoutineTag
	public static String ifThenElseAssign( int a ) {
		String z ;
		if ( a>7 ) z = ">7" ;
		else z = "<=7" ;
		return z ;
	}

	@RoutineTag
	public static String ifThenElseReturn( int a ) {
		if ( a>7 ) return ">7" ;
		else return "<=7" ;
	}


}
