package edu.vanderbilt.clinicalsystems.epic.annotation;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.annotation.InjectRoutine;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;
import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.junit.MLanguageTestRunner;

@RoutineUnit
@RunWith(MLanguageTestRunner.class)
public class SamplesTest {

	@InjectRoutine
	private Samples m_samples ;
	
	private edu.vanderbilt.clinicalsystems.epic.api.Chronicles m_mockChronicles ;
	private edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation m_mockEcf ;
	
	@Before
	public void setup() throws Exception {
		m_samples = new Samples() ;
		/*  same as:
		 *  MLanguageTestRunner.inject( this, Samples.class, new Samples() )
		 */

		m_mockChronicles = mock(edu.vanderbilt.clinicalsystems.epic.api.Chronicles.class) ;
		List<String> patientIds = Arrays.asList( "123", "456", "789" );
		// when(m_mockChronicles.znxIxID(anyString(), anyInt(), anyString(), anyString())).thenReturn( "123", "456", "789", "" );
		when(m_mockChronicles.znxIxID(anyString(), anyInt(), anyString(), anyString())).thenAnswer( new Answer<String>() {
			@Override public String answer(InvocationOnMock invocation) throws Throwable {
				String id = invocation.getArgumentAt(3, String.class) ;
				for ( String checkId : patientIds )
					if ( id.compareTo(checkId) < 0 )
						return checkId ; 
				return "" ;
			}
		}) ;
		
		MLanguageTestRunner.inject( m_samples, edu.vanderbilt.clinicalsystems.epic.api.Chronicles.class, m_mockChronicles ) ;
		
		m_mockEcf = mock(edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.class) ;
		when(m_mockEcf.getProperty(anyString())).thenReturn( "123456789" );
		when(m_mockEcf.getProperty(anyString(),anyString())).thenReturn( "123456789" );
		when(m_mockEcf.getProperty(anyString(),anyString(),any())).thenReturn( "123456789" );
		
		MLanguageTestRunner.inject( m_samples, edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.class, m_mockEcf ) ;
	}
	
	@RoutineTag
	@Test(expected=EngineException.class)
	public void cannotCallUntaggedMethods() {
		m_samples.methodThatsNotATag();
	}
	
	@RoutineTag
	@Test
	public void canCallTaggedMethods() {
		m_samples.tagWithNoParameters();
		m_samples.tagWithStringParameter( "abc" );
		m_samples.tagWithIntegerParameter( 7 );
		m_samples.tagWithDoubleParameter( 3.14 );
		m_samples.tagWithValueParameter( Value.nullValue() );
//		m_samples.tagWithOtherParameter( System.out );
	}
	
	@RoutineTag
	@Test
	public void canPerformBasicSummationLoop() {
		int actual = m_samples.basicSummationLoop(1, 1, 3) ;
		int expected = 1 + 2 + 3 ;
		assertThat( actual, equalTo(expected) ) ;
		
	}
	
	@RoutineTag
	@Test
	public void canPerformBasicSort() {
		String actual = m_samples.basicSort("c;d;b;a",";") ;
		String expected = "a;b;c;d" ;
		assertThat( actual, equalTo(expected) ) ;
		
	}
	
	@RoutineTag
	@Test
	public void canPerformEnhancedSort() {
		String actual = m_samples.enhancedSort("c;d;b;a",";") ;
		String expected = "a;b;c;d" ;
		assertThat( actual, equalTo(expected) ) ;
		
	}
	
	@RoutineTag
	@Test
	public void canInvokeWebService() {
		m_samples.myService();
	}
	
	@RoutineTag
	@Test
	public void canInvokeWebService2() {
		m_samples.myService2();
	}
	
	@RoutineTag
	@Test
	public void canUpdateNames() {
		m_samples.updateNames();
	}

}
