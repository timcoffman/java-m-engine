package edu.vanderbilt.clinicalsystems.epic.lib;

import edu.vanderbilt.clinicalsystems.epic.annotation.EpicRoutineLibrary;
import edu.vanderbilt.clinicalsystems.epic.annotation.EpicTag;
import edu.vanderbilt.clinicalsystems.m.core.Value;

@EpicRoutineLibrary("%Zefnlib")
public class ZefnLib {

	@EpicTag("zGtTmpGlo") public static native Value acquirePrivateTempGlobal() ;
	@EpicTag("%zRelTmpGlo") public static native Value releasePrivateTempGlobal( String name ) ;
	
	@EpicTag("zGtTmpGloShrd") public static native Value acquireSharedTempGlobal() ;
	@EpicTag("%zRelTmpGloShrd") public static native Value releaseSharedTempGlobal( String name ) ;
	
}
