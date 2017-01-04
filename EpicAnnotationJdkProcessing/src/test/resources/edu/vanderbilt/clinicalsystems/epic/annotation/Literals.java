package edu.vanderbilt.clinicalsystems.epic.annotation;

/**
 *  DoLoop Sample
 */
@EpicRoutine
public class Literals {
	
	/**
	 * Expect
	 *  Q 1
	 */
	@EpicTag
	public static boolean returnTrue() {
		return true ;
	}
	
	/**
	 * Expect
	 *  Q 0
	 */
	@EpicTag
	public static boolean returnFalse() {
		return false ;
	}
	
}
