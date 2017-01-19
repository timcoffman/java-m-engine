package edu.vanderbilt.clinicalsystems.epic.api;

import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;

@RoutineUnit(value="EA3LIB5")
public class Chronicles {

	@RoutineTag public native String znxIxID( String ini, long item, String value, String id ) ;
	@RoutineTag public native String znxIxID( String ini, long item, String value, String id, long direction ) ;
	
}
