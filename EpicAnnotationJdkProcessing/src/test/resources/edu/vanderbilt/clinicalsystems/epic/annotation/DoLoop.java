package edu.vanderbilt.clinicalsystems.epic.annotation;

import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit ;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag ;

/**
 *  DoLoop Sample
 */
@RoutineUnit
public class DoLoop {
	
	@RoutineTag public static void loopBody(int i) { }
	@RoutineTag public static boolean sometimes() { return true ; }
	
	/**
	 * Expect
	 *  F D loopBody(-1) Q
	 */
	@RoutineTag
	public static void doOnceLoop() {
		do {
			loopBody( -1 ) ;
		} while ( false ) ;
	}
	
	/**
	 * Expect
	 *  F  D loopBody(i) Q:sometimes()
	 */
	@RoutineTag
	public static void doForeverLoop() {
		do {
			loopBody( -1 ) ;
			if ( sometimes() ) break ;
		} while ( true ) ;
	}
	
	/**
	 * Expect
	 *  N i S i=0 F  Q:'(i<10) D
	 *  . D loopBody(i)
	 */
	@RoutineTag
	public static void doWhileLoop() {
		int i = 0 ;
		do {
			loopBody(i) ;
			--i ;
		} while ( i < 10 ) ;
	}

}
