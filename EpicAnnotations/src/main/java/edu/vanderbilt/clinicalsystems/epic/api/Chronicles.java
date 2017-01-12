package edu.vanderbilt.clinicalsystems.epic.api;

import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag;

@RoutineUnit(value="EALIB")
public class Chronicles {

	@RoutineTag public static native Value znxIxID( Value ini, Value item, Value value, Value id ) ;
	@RoutineTag public static native Value znxIxID( String ini, int item, Value value, Value id ) ;
	
}
