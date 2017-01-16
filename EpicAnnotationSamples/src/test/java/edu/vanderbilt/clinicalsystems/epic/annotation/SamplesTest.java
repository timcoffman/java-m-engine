package edu.vanderbilt.clinicalsystems.epic.annotation;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;
import edu.vanderbilt.clinicalsystems.m.junit.MLanguageTestRunner;

@RoutineUnit
@RunWith(MLanguageTestRunner.class)
public class SamplesTest {

	@RoutineTag
	@Test
	public void canPerformBasicSummationLoop() {
		int actual = Samples.basicSummationLoop(1, 1, 3) ;
		int expected = 1 + 2 + 3 ;
		assertThat( actual, equalTo(expected) ) ;
		
	}
	
	@RoutineTag
	@Test
	public void canPerformBasicSort() {
		String actual = Samples.basicSort("c;d;b;a",";") ;
		String expected = "a;b;c;d" ;
		assertThat( actual, equalTo(expected) ) ;
		
	}

}
