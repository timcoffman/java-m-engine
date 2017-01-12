package edu.vanderbilt.clinicalsystems.epic.annotation;

import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit ;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag ;

/**
 *  DoLoop Sample
 */
@RoutineUnit
public class Literals {
	
	/**
	 * Expect
	 *  Q 1
	 */
	@RoutineTag
	public static boolean returnTrue() {
		return true ;
	}
	
	/**
	 * Expect
	 *  Q 0
	 */
	@RoutineTag
	public static boolean returnFalse() {
		return false ;
	}
	
}
