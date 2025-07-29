package edu.vanderbilt.clinicalsystems.epic.lib;

import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;

@RoutineUnit("%Zefnlib")
public class ZefnLib {

	public static int MAPPED_TO_CACHETEMP = 0 ; // perhaps should be ""
	public static int MAPPED_TO_SCRATCHDB = 1 ;
	public static int USED_EXCLUSIVELY_BY_AUDIT_BATCHES = 2 ;
	
	@RoutineTag("zGtTmpGlo") public static native String acquirePrivateTempGlobal() ;
	@RoutineTag("zGtTmpGlo") public static native String acquirePrivateTempGlobal(int alternativeLocation) ;
	@RoutineTag("zGtTmpGlo") public static native String acquirePrivateTempGlobal(int alternativeLocation, String key ) ;
	@RoutineTag("zGtTmpGlo") public static native String acquirePrivateTempGlobal(int alternativeLocation, String key, boolean useLocaleCollation) ;
	
	@RoutineTag("%zRelTmpGlo") public static native String releasePrivateTempGlobal( String name ) ;
	
	@RoutineTag("zGtTmpGloShrd") public static native String acquireSharedTempGlobal() ;
	@RoutineTag("%zRelTmpGloShrd") public static native String releaseSharedTempGlobal( String name ) ;
	
}
