package edu.vanderbilt.clinicalsystems.epic.annotation;

import edu.vanderbilt.clinicalsystems.epic.annotation.EpicRoutine;
import edu.vanderbilt.clinicalsystems.epic.annotation.EpicTag;
/**
 *  Operators Sample
 */
@EpicRoutine
public class Operators {
	
	/**
	 * Expect Q +x
	 */
	@EpicTag
	public static int prefixPlus(int x) {
		return +x ;
	}
	
	/**
	 * Expect Q -x
	 */
	@EpicTag
	public static int prefixMinus(int x) {
		return -x ;
	}

	/**
	 * Expect Q x  S x=x+1
	 */
	@EpicTag
	public static int postfixIncrement(int x) {
		return x++ ;
	}
	
	/**
	 * Expect S x=x+1 Q x
	 */
	@EpicTag
	public static int prefixIncrement(int x) {
		return ++x ;
	}
	
	/**
	 * Expect Q x  S x=x-1
	 */
	@EpicTag
	public static int postfixDecrement(int x) {
		return x-- ;
	}
	
	/**
	 * Expect S x=x-1 Q x
	 */
	@EpicTag
	public static int prefixDecrement(int x) {
		return --x ;
	}
		
	/**
	 * Expect S x=x+7 Q x
	 */
	@EpicTag
	public static int assignmentPlus(int x) {
		return x += 7 ;
	}
		
	/**
	 * Expect S x=x-7 Q x
	 */
	@EpicTag
	public static int assignmentMinus(int x) {
		return x -= 7 ;
	}
		
}
