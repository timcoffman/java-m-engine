package edu.vanderbilt.clinicalsystems.epic.annotation;

import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit ;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag ;
import edu.vanderbilt.clinicalsystems.m.core.annotation.support.ReadWriteCodeType;
import edu.vanderbilt.clinicalsystems.m.core.lib.ReadWrite;

/**
 *  ForLoop Sample
 */
@RoutineUnit
public class ForLoop {
	
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

	/**
	 * Expect N key S key="" F  key=$O(x(key)) Q:key="" W key
	 */
	@RoutineTag
	public static void enhancedForLoop() {
		Value x = Value.nullValue() ;
		for( String key : x.keys() )
			ReadWrite.write( key );
	}
	
}
