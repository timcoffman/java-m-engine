package edu.vanderbilt.clinicalsystems.epic.annotation;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;
import java.util.ServiceLoader;

import org.junit.Test;

public class ProcessorTest {

	@Test
	public void canFindRegisteredProcessor2() throws URISyntaxException {
		ServiceLoader<javax.annotation.processing.Processor> serviceLoader = ServiceLoader.load( javax.annotation.processing.Processor.class ) ;
		assertThat( serviceLoader, hasItem( isA(edu.vanderbilt.clinicalsystems.epic.annotation.Processor.class) ) ) ;
	}

}
