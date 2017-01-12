package edu.vanderbilt.clinicalsystems.epic.annotation;

import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;

/**
 *  JUnit Example
 */
@RoutineUnit
public class Example {
	
	/**
	 * Expect
	 *  NEW s
	 *  SET s=0
	 *  NEW i
	 *  FOR start:step:stop SET s=s+i
	 *  QUIT s
	 */
	@RoutineTag
	public static int summation(int start, int step, int stop) {
		int s = 0 ;
		for ( int i = start ; i <= stop ; i+=step )
			s += i ;
		return s ;
	}

}
