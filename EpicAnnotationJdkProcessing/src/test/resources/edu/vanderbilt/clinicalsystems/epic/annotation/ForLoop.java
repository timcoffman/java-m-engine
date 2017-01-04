package edu.vanderbilt.clinicalsystems.epic.annotation;

/**
 *  ForLoop Sample
 */
@EpicRoutine
public class ForLoop {
	
	@EpicTag public static void loopBody(int i) { }
	
	/**
	 * Expect F 1:1:10
	 */
	@EpicTag
	public static void forLoopIncrementByOne() {
		for( int i = 1 ; i <= 10 ; ++i ) loopBody(i) ;
	}

	/**
	 * Expect F 1:2:10
	 */
	@EpicTag
	public static void forLoopIncrementByTwo() {
		for( int i = 1 ; i <= 10 ; i+=2 ) loopBody(i) ;
	}

	/**
	 * Expect F 1
	 */
	@EpicTag
	public static void forLoopIncrementByOneForever() {
		for( int i = 1 ; ; ++i ) loopBody(i) ;
	}
	
	/**
	 * Expect F 1:2
	 */
	@EpicTag
	public static void forLoopIncrementByTwoForever() {
		for( int i = 1 ; ; i+=2 ) loopBody(i) ;
	}
	
	/**
	 * Expect F 1:1:10
	 */
	@EpicTag
	public static void forLoopStopNotIncludedIncrementByOne() {
		for( int i = 0 ; i < 10 ; ++i ) loopBody(i) ;
	}

	/**
	 * Expect F 1:2:10
	 */
	@EpicTag
	public static void forLoopStopNotIncludedIncrementByTwo() {
		for( int i = 0 ; i < 10 ; i+=2 ) loopBody(i) ;
	}

}
