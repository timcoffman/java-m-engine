package edu.vanderbilt.clinicalsystems.epic.annotation;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ServiceLoader;

import org.junit.Test;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.factory.RoutineJdkToolsFactory;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.factory.RoutineToolsFactory;

public class ProcessorTest {

	@Test
	public void canFindRegisteredRoutineToolsFactory2() throws URISyntaxException {
		ServiceLoader<RoutineToolsFactory> serviceLoader = ServiceLoader.load( RoutineToolsFactory.class ) ;
		assertThat( serviceLoader, hasItem( isA(RoutineJdkToolsFactory.class) ) ) ;
	}
	
	private static final RoutineTestUtils ROUTINE_TEST_UTILS = new RoutineTestUtils(ProcessorTest.class) ;  
	
	@Test
	public void canProcessOperators() throws URISyntaxException, IOException {
		ROUTINE_TEST_UTILS.canProcessRoutineAnnotations("Operators" ) ;
	}
	
	@Test
	public void canProcessBuiltinFunctions() throws URISyntaxException, IOException {
		ROUTINE_TEST_UTILS.canProcessRoutineAnnotations("BuiltinFunctions" ) ;
	}
	
	@Test
	public void canProcessLiterals() throws URISyntaxException, IOException {
		ROUTINE_TEST_UTILS.canProcessRoutineAnnotations("Literals" ) ;
	}
	
	@Test
	public void canProcessForLoop() throws URISyntaxException, IOException {
		ROUTINE_TEST_UTILS.canProcessRoutineAnnotations("ForLoop" ) ;
	}
	
	@Test
	public void canProcessWhileLoop() throws URISyntaxException, IOException {
		ROUTINE_TEST_UTILS.canProcessRoutineAnnotations("WhileLoop" ) ;
	}
	
	@Test
	public void canProcessDoLoop() throws URISyntaxException, IOException {
		ROUTINE_TEST_UTILS.canProcessRoutineAnnotations("DoLoop" ) ;
	}
	
	@Test
	public void canProcessIfThenElse() throws URISyntaxException, IOException {
		ROUTINE_TEST_UTILS.canProcessRoutineAnnotations("IfThenElse" ) ;
	}
	
	@Test
	public void canProcessSampleOne() throws URISyntaxException, IOException {
		ROUTINE_TEST_UTILS.canProcessRoutineAnnotations("SampleOne" ) ;
	}
	
	@Test
	public void canProcessExpressions() throws URISyntaxException, IOException {
		ROUTINE_TEST_UTILS.canProcessRoutineAnnotations("Expressions" ) ;
	}
	
	@Test
	public void canProcessServices() throws URISyntaxException, IOException {
		ROUTINE_TEST_UTILS.canProcessRoutineAnnotations("Services" ) ;
	}
	
	@Test
	public void canProcessExample() throws URISyntaxException, IOException {
		ROUTINE_TEST_UTILS.canProcessRoutineAnnotations("Example" ) ;
	}
	
	@Test
	public void canProcessJunitTest() throws URISyntaxException, IOException {
		ROUTINE_TEST_UTILS.canProcessRoutineAnnotations("JUnitTest" ) ;
	}
	
}
