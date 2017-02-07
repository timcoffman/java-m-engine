package edu.vanderbilt.clinicalsystems.epic.annotation;

import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;
/**
 *  expressions
 */
@RoutineUnit
public class Expressions {
		
	/**
	 * main entry point, expecting "Q"
	 */
	@RoutineTag
	public static void main( String x, long y ) {
		/* nothing */
	}

	/**
	 * helper function, expecting "Q s_s"
	 */
	@RoutineTag
	public static String helper( String s ) {
		return s + s ;
	}
	
	/**
	 * helper2 function, expecting "Q b*c+a"
	 */
	@RoutineTag
	public static double helper2( double a, double b, double c ) {
		return a + b * c ;
	}
	
	/**
	 * helper3 function, expecting "N z S z=$$helper2(1.0,2.0,3.0) Q b*z+a"
	 */
	@RoutineTag
	public static Float helper3( Float a, Float b, Float c ) {
		float z = (float)helper2(1.0,2.0,3.0), k = 99 ; 
		long t = System.currentTimeMillis() ;
		return a + b * z ;
	}

}
