package edu.vanderbilt.clinicalsystems.m.core.lib;

import edu.vanderbilt.clinicalsystems.m.core.annotation.Library;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Operator;
import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;

@Library
public class Math {
		
	@Operator(OperatorType.POWER) public static native double power( double mantissa, double exponent ) ;
}
