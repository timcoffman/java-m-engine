package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class AbstractWriterTest {

	private static File outputFolder;
	
	@BeforeClass
	public static void setup() throws IOException {
		outputFolder = File.createTempFile( RoutineJavaWriterTest.class.getSimpleName(), null);
		outputFolder.delete();
		outputFolder.mkdir();
		System.out.println( String.format("creating output files from %1$s in %2$s...", RoutineJavaWriterTest.class.getSimpleName(), outputFolder) );
	}
	
	@AfterClass
	public static void teardown() throws IOException {
		System.out.println( String.format("created output files from %1$s in %2$s", RoutineJavaWriterTest.class.getSimpleName(), outputFolder) );
	}
	
	protected static File makeTempFolder( String testName ) {
		File folder = new File( outputFolder, "TEST-" + testName );
		folder.delete();
		folder.mkdir();
		System.out.println( String.format("creating output files from %1$s() in %2$s...", testName, folder) );
		return folder;
	}
	

}
