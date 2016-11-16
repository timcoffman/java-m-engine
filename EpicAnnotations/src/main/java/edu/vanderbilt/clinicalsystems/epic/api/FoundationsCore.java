package edu.vanderbilt.clinicalsystems.epic.api;

import edu.vanderbilt.clinicalsystems.epic.annotation.EpicRoutineLibrary;
import edu.vanderbilt.clinicalsystems.epic.annotation.EpicTag;

@EpicRoutineLibrary("%Zefnlib")
public class FoundationsCore {

	@EpicTag("up") public static native String up( String str ) ;
	
}
