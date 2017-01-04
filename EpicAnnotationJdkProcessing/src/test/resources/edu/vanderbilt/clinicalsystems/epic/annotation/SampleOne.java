package edu.vanderbilt.clinicalsystems.epic.annotation;

import static edu.vanderbilt.clinicalsystems.epic.api.Chronicles.znxIxID;
import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.ARRAY_TYPE;
import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.zECFGet;
import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.zECFGetElmt;
import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.zECFNew;
import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.zECFNumElmts;
import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.zECFSetElmt;
import edu.vanderbilt.clinicalsystems.epic.annotation.EpicTag;
import edu.vanderbilt.clinicalsystems.epic.api.oo.EpicCommunicationFoundation;
import edu.vanderbilt.clinicalsystems.m.core.Value;
/**
 *  sample service
 */
@EpicRoutine
public class SampleOne {
		
	@EpicTag
	public static void sample() {
		int k ;
		for ( int x = 0 ; x < 10 ; ++x )
			k = 0 ;
	}
	
}
