package edu.vanderbilt.clinicalsystems;

import java.io.IOException;

import org.junit.Test;

public class GenerateJavaStubsFromMResourcesTest {

	@Test
	public void test() throws IOException {
		GenerateJavaStubsFromMResources.main( new String[] { "src/generated-sources/java", "src/main/resources" } ) ;
	}

}
