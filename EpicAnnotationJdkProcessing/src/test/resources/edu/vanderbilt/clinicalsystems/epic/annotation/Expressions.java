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
 *  expressions
 */
@EpicRoutine
public class Expressions {
		
	/**
	 * main entry point, expecting "Q"
	 */
	@EpicTag
	public void main( String x, Integer y ) {
		/* nothing */
	}

	/**
	 * helper function, expecting "Q s_s"
	 */
	@EpicTag
	public String helper( String s ) {
		return s + s ;
	}
	
	/**
	 * helper2 function, expecting "Q b*c+a"
	 */
	@EpicTag
	public double helper2( double a, double b, double c ) {
		return a + b * c ;
	}
	
	/**
	 * helper3 function, expecting "N z S z=$$helper2(1.0,2.0,3.0) Q b*z+a"
	 */
	@EpicTag
	public Float helper3( Float a, Float b, Float c ) {
		float z = (float)helper2(1.0,2.0,3.0), k = 99 ; 
		long t = System.currentTimeMillis() ;
		return a + b * z ;
	}

}
