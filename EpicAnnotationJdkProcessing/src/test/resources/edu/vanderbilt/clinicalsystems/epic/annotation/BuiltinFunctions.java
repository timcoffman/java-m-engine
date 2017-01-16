package edu.vanderbilt.clinicalsystems.epic.annotation;

import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit ;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag ;
import edu.vanderbilt.clinicalsystems.m.core.lib.Text;

/**
 *  DoLoop Sample
 */
@RoutineUnit
public class BuiltinFunctions {
	
	/**
	 * Expect
	 *  Q $LENGTH(source,delimiter)
	 */
	@RoutineTag
	public static int doOccurrencesPlusOne(String source,String delimiter) {
		return Text.occurrencesPlusOne(source,delimiter) ;
	}
	


}
