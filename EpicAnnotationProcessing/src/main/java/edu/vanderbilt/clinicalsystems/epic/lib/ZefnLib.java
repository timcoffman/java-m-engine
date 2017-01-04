package edu.vanderbilt.clinicalsystems.epic.lib;

import edu.vanderbilt.clinicalsystems.epic.annotation.EpicRoutineLibrary;
import edu.vanderbilt.clinicalsystems.epic.annotation.EpicTag;

@EpicRoutineLibrary("%Zefnlib")
public class ZefnLib {

	@EpicTag("zGtTmpGlo") public static native String acquirePrivateTempGlobal() ;
	@EpicTag("%zRelTmpGlo") public static native String releasePrivateTempGlobal( String name ) ;
	
	@EpicTag("zGtTmpGloShrd") public static native String acquireSharedTempGlobal() ;
	@EpicTag("%zRelTmpGloShrd") public static native String releaseSharedTempGlobal( String name ) ;
	
}
