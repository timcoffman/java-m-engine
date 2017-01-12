package edu.vanderbilt.clinicalsystems.epic.api;

import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag;

@RoutineUnit("%Zefnlib")
public class FoundationsCore {

	@RoutineTag("up") public static native String up( String str ) ;
	
}
