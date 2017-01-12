package edu.vanderbilt.clinicalsystems.epic.annotation;

import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;
/**
 *  sample service
 */
@RoutineUnit
public class SampleOne {
		
	@RoutineTag
	public static void sample() {
		int k ;
		for ( int x = 0 ; x < 10 ; ++x )
			k = 0 ;
	}
	
}
