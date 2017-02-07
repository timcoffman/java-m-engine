package edu.vanderbilt.clinicalsystems.m.lang;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import edu.vanderbilt.clinicalsystems.m.lang.text.Representation;

public class RepresentationInferenceTest {

//	@Test
	public void canInferVariableRepresentationFromAssignment() {
		
		RepresentationInference inference = new RepresentationInference();
		inference.assign( "x", Representation.BOOLEAN ) ;
		assertThat( inference.representationOfVariable( "x" ), equalTo( Representation.BOOLEAN ) ) ;
		
	}
	
}
