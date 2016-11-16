package edu.vanderbilt.clinicalsystems.epic.api;

import edu.vanderbilt.clinicalsystems.epic.annotation.EpicRoutineLibrary;
import edu.vanderbilt.clinicalsystems.epic.annotation.EpicTag;
import edu.vanderbilt.clinicalsystems.m.core.Value;

@EpicRoutineLibrary(value="EALIB",implicit=true)
public class Chronicles {

	@EpicTag public static native Value znxIxID( Value ini, Value item, Value value, Value id ) ;
	@EpicTag public static native Value znxIxID( String ini, int item, Value value, Value id ) ;
	
}
