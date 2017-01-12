package edu.vanderbilt.clinicalsystems.epic.lib;

import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag;

@RoutineUnit("%Zefnlib")
public class ZefnLib {

	@RoutineTag("zGtTmpGlo") public static native String acquirePrivateTempGlobal() ;
	@RoutineTag("%zRelTmpGlo") public static native String releasePrivateTempGlobal( String name ) ;
	
	@RoutineTag("zGtTmpGloShrd") public static native String acquireSharedTempGlobal() ;
	@RoutineTag("%zRelTmpGloShrd") public static native String releaseSharedTempGlobal( String name ) ;
	
}
