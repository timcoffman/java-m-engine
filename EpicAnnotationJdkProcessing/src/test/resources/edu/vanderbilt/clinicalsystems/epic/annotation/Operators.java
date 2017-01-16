package edu.vanderbilt.clinicalsystems.epic.annotation;

import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit ;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag ;

/**
 *  Operators Sample
 */
@RoutineUnit
public class Operators {
	
	/**
	 * Expect Q x_"abc"
	 */
	@RoutineTag
	public static String concatenation(String x) {
		return x + "abc" ;
	}
	
	/**
	 * Expect S x=x_"abc" Q x
	 */
	@RoutineTag
	public static String assignmentConcatenation(String x) {
		return x += "abc"  ;
	}
	
	/**
	 * Expect Q +x
	 */
	@RoutineTag
	public static int prefixPlus(int x) {
		return +x ;
	}
	
	/**
	 * Expect Q -x
	 */
	@RoutineTag
	public static int prefixMinus(int x) {
		return -x ;
	}

	/**
	 * Expect Q x  S x=x+1
	 */
	@RoutineTag
	public static int postfixIncrement(int x) {
		return x++ ;
	}
	
	/**
	 * Expect S x=x+1 Q x
	 */
	@RoutineTag
	public static int prefixIncrement(int x) {
		return ++x ;
	}
	
	/**
	 * Expect Q x  S x=x-1
	 */
	@RoutineTag
	public static int postfixDecrement(int x) {
		return x-- ;
	}
	
	/**
	 * Expect S x=x-1 Q x
	 */
	@RoutineTag
	public static int prefixDecrement(int x) {
		return --x ;
	}
		
	/**
	 * Expect S x=x+7 Q x
	 */
	@RoutineTag
	public static int assignmentPlus(int x) {
		return x += 7 ;
	}
		
	/**
	 * Expect S x=x-7 Q x
	 */
	@RoutineTag
	public static int assignmentMinus(int x) {
		return x -= 7 ;
	}
		
}
