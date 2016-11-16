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
	public int prefixPlus(int x) {
		return +x ;
	}
	
	/**
	 * Expect Q -x
	 */
	@EpicTag
	public int prefixMinus(int x) {
		return -x ;
	}

	/**
	 * Expect Q x  S x=x+1
	 */
	@EpicTag
	public int postfixIncrement(int x) {
		return x++ ;
	}
	
	/**
	 * Expect S x=x+1 Q x
	 */
	@EpicTag
	public int prefixIncrement(int x) {
		return ++x ;
	}
	
	/**
	 * Expect Q x  S x=x-1
	 */
	@EpicTag
	public int postfixDecrement(int x) {
		return x-- ;
	}
	
	/**
	 * Expect S x=x-1 Q x
	 */
	@EpicTag
	public int prefixDecrement(int x) {
		return --x ;
	}
		
	/**
	 * Expect S x=x+7 Q x
	 */
	@EpicTag
	public int assignmentPlus(int x) {
		return x += 7 ;
	}
		
	/**
	 * Expect S x=x-7 Q x
	 */
	@EpicTag
	public int assignmentMinus(int x) {
		return x -= 7 ;
	}
		
}
