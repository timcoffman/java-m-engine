package edu.vanderbilt.clinicalsystems.epic.annotation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit ;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag ;

/**
 *  JUnitTest
 */
@RoutineUnit
public class JUnitTest {
	
	/**
	 * Expect
	 *  NEW actual
	 *  SET actual=$$summation^Example(1,1,3)
	 *  NEW expected
	 *  SET expected=1+2+3
	 *  DO assertThat^Assert(actual,equalTo^CoreMatchers(expected))
	 *  QUIT
	 */
	@RoutineTag
	public void assertSomething() {
		int actual = Example.summation(1, 1, 3);
		int expected = 1 + 2 + 3 ;
		assertThat( actual, equalTo(expected) ) ;
	}


}
