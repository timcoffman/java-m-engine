package edu.vanderbilt.clinicalsystems.epic.annotation;

/**
 *  ForLoop Sample
 */
@EpicRoutine
public class ForLoop {
	
	@EpicTag public void loopBody(int i) { }
	
	/**
	 * Expect F 1:1:10
	 */
	@EpicTag
	public void forLoopIncrementByOne() {
		for( int i = 1 ; i <= 10 ; ++i ) loopBody(i) ;
	}

	/**
	 * Expect F 1:2:10
	 */
	@EpicTag
	public void forLoopIncrementByTwo() {
		for( int i = 1 ; i <= 10 ; i+=2 ) loopBody(i) ;
	}

	/**
	 * Expect F 1
	 */
	@EpicTag
	public void forLoopIncrementByOneForever() {
		for( int i = 1 ; ; ++i ) loopBody(i) ;
	}
	
	/**
	 * Expect F 1:2
	 */
	@EpicTag
	public void forLoopIncrementByTwoForever() {
		for( int i = 1 ; ; i+=2 ) loopBody(i) ;
	}
	
	/**
	 * Expect F 1:1:10
	 */
	@EpicTag
	public void forLoopStopNotIncludedIncrementByOne() {
		for( int i = 0 ; i < 10 ; ++i ) loopBody(i) ;
	}

	/**
	 * Expect F 1:2:10
	 */
	@EpicTag
	public void forLoopStopNotIncludedIncrementByTwo() {
		for( int i = 0 ; i < 10 ; i+=2 ) loopBody(i) ;
	}

}
