package edu.vanderbilt.clinicalsystems.epic.annotation;

/**
 *  DoLoop Sample
 */
@EpicRoutine
public class DoLoop {
	
	@EpicTag public static void loopBody(int i) { }
	@EpicTag public static boolean sometimes() { return true ; }
	
	/**
	 * Expect
	 *  F D loopBody(-1) Q
	 */
	@EpicTag
	public static void doOnceLoop() {
		do {
			loopBody( -1 ) ;
		} while ( false ) ;
	}
	
	/**
	 * Expect
	 *  F  D loopBody(i) Q:sometimes()
	 */
	@EpicTag
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
	@EpicTag
	public static void doWhileLoop() {
		int i = 0 ;
		do {
			loopBody(i) ;
			--i ;
		} while ( i < 10 ) ;
	}

}
