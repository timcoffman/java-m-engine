package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.util.Comparator;

import edu.vanderbilt.clinicalsystems.m.lang.model.expression.ConstantSupport;

public class MLanguageCollator implements Comparator<String> {

	@Override
	public int compare(String lhs, String rhs) {
		/* if they're the same instance, or both null, they're equal */
		if ( lhs == rhs )
			return 0 ;
		
		/* if either one is null, it is before the other */
		if ( null == lhs )
			return -1 ;
		else if ( null == rhs  )
			return 1 ;
		
		/* if they're both empty, they're equal; otherwise if either one is empty, it is before the other */ 
		if ( lhs.isEmpty() && rhs.isEmpty() )
			return 0 ;
		else if ( lhs.isEmpty() )
			return -1 ;
		else if ( rhs.isEmpty() )
			return 1 ;
		
		boolean lhsIsNumber = ConstantSupport.representsNumber(lhs);
		boolean rhsIsNumber = ConstantSupport.representsNumber(rhs);
		/* if they're both numbers, compare them numerically; otherwise if either one is a number, it is before the other */ 
		if ( lhsIsNumber && rhsIsNumber ) {
			return Double.compare( ConstantSupport.toDouble(lhs), ConstantSupport.toDouble(rhs) ) ;
		} else if ( lhsIsNumber ) {
			return -1 ;
		} else if ( rhsIsNumber ) {
			return 1 ;
		}
		
		/* compare them lexically */ 
		return lhs.compareTo( rhs ) ;
	}
	
}
