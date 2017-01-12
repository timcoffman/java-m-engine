package edu.vanderbilt.clinicalsystems.epic.annotation;

import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit ;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag ;

/**
 *  ForLoop Sample
 */
@RoutineUnit
public class WhileLoop {
	
	@RoutineTag public static void loopBody(int i) { }
	
	/**
	 * Expect F 1:1:10
	 */
	@RoutineTag
	public static void forLoopIncrementByOne() {
		for( int i = 1 ; i <= 10 ; ++i ) loopBody(i) ;
	}

	/**
	 * Expect F 1:2:10
	 */
	@RoutineTag
	public static void forLoopIncrementByTwo() {
		for( int i = 1 ; i <= 10 ; i+=2 ) loopBody(i) ;
	}

	/**
	 * Expect F 1
	 */
	@RoutineTag
	public static void forLoopIncrementByOneForever() {
		for( int i = 1 ; ; ++i ) loopBody(i) ;
	}
	
	/**
	 * Expect F 1:2
	 */
	@RoutineTag
	public static void forLoopIncrementByTwoForever() {
		for( int i = 1 ; ; i+=2 ) loopBody(i) ;
	}
	
	/**
	 * Expect F 1:1:10
	 */
	@RoutineTag
	public static void forLoopStopNotIncludedIncrementByOne() {
		for( int i = 0 ; i < 10 ; ++i ) loopBody(i) ;
	}

	/**
	 * Expect F 1:2:10
	 */
	@RoutineTag
	public static void forLoopStopNotIncludedIncrementByTwo() {
		for( int i = 0 ; i < 10 ; i+=2 ) loopBody(i) ;
	}

}
