package edu.vanderbilt.clinicalsystems.epic.annotation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

public class RoutineTestUtils {

	private final Class<?> m_resourceBase;

	public RoutineTestUtils( Class<?> resourceBase ) {
		m_resourceBase = resourceBase ;
	}
	
	private static final Pattern IGNORE_LINES_M_LANGUAGE = Pattern.compile("Generated-On|^\\s*;") ;
	private static final Pattern IGNORE_LINES_TREE = Pattern.compile("^###|^\\s*$") ;
	
	private File findOutputFolder( File dir, String pkgName ) {
		if ( null == pkgName || pkgName.isEmpty() )
			return dir ;
		String[] s = pkgName.split("\\.",2) ;
		File pkgDir = new File( dir, s[0] ) ;
		if ( s.length < 2 )
			return pkgDir ;
		return findOutputFolder( pkgDir, s[1] );
	}
		
	public void canProcessRoutineAnnotations(String name) throws URISyntaxException, IOException {
		File destinationDir = File.createTempFile("generated-sources-", null ) ;
		destinationDir.delete() ;
		destinationDir.mkdir() ;
		File packageDir = findOutputFolder( destinationDir, m_resourceBase.getPackage().getName() );
		try {
			URL sourceResource = m_resourceBase.getResource( name + ".java" );
			if ( null == sourceResource )
				throw new IOException("source resource (" + name + ".java) is not available for testing" ) ;
			process( sourceResource, destinationDir ) ;
			
			URL actualResourceMLanguage = new File( packageDir, name + ".m" ).toURI().toURL();
			URL actualResourceTree = new File( packageDir, name + ".tree" ).toURI().toURL();
			URL actualMetaTree = new File( packageDir, name + ".xml" ).toURI().toURL();
			
			assertThat( actualResourceMLanguage, notNullValue() ) ;
			assertThat( actualResourceTree, notNullValue() ) ;
			assertThat( actualMetaTree, notNullValue() ) ;
			
			try ( java.util.Scanner s = new java.util.Scanner( actualMetaTree.openStream() ) ) {
				System.out.println( s.useDelimiter("\\A").next() ) ;
			}
			
			URL expectedResourceMLanguage = m_resourceBase.getResource( name + "Expected.m");
			URL expectedResourceTree = m_resourceBase.getResource( name + "Expected.tree");
			if (  null == expectedResourceMLanguage && null == expectedResourceTree )
				throw new IOException("neither an M-Language resource (" + name + "Expected.m) nor a Tree expected resource (" + name + "Expected.tree) was available for testing") ;
			
			if ( null != expectedResourceMLanguage ) {
				assertThatResourceContentsEqual( actualResourceMLanguage, expectedResourceMLanguage, IGNORE_LINES_M_LANGUAGE ) ;
			}
			
			if ( null != expectedResourceTree ) {
				assertThatResourceContentsEqual( actualResourceTree, expectedResourceTree, IGNORE_LINES_TREE ) ;
			}
		} finally {
			File[] files = destinationDir.listFiles();
			if ( null != files )
				for ( File file : files )
					file.delete() ;
			destinationDir.delete() ;
		}
	}
	
	private void process( URL resourceFile, File destinationDir ) throws URISyntaxException, IOException { process( new File( resourceFile.toURI() ), destinationDir ) ; }
	
	private void process( File resourceFile, File destinationDir ) throws IOException { process( getSourceFileResources(resourceFile), destinationDir ) ; }
	
	private void process( Iterable<? extends JavaFileObject> files, File destinationDir ) throws IOException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, null, null) ;
		standardFileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singleton( destinationDir ) );
		
		CompilationTask task = compiler.getTask(new PrintWriter(System.out), standardFileManager, null, null, null, files);
		task.setProcessors(Arrays.asList(new Processor()));
		
		task.call();
	}
	
	private Iterable<? extends JavaFileObject> getSourceFileResources(File resourceFile) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager files = compiler.getStandardFileManager(null, null, null);
		return files.getJavaFileObjects(resourceFile) ;
	}

	private String finalPathComponent( URL resource ) {
		return resource.getPath().replaceAll("^.*/","") ;
	}
	
	private static final int CONTEXT_LINES = 5 ; 
	
	private void assertThatResourceContentsEqual( URL actualResource, URL expectedResource, Pattern ignoreLines ) throws FileNotFoundException, IOException, URISyntaxException {
		int actualLineNumber = 0 ;
		int expectedLineNumber = 0 ;
		Deque<String> actualQueue = new ArrayDeque<String>() ;
		boolean passed = false ;
		try ( BufferedReader actualReader = new BufferedReader( new FileReader( new File(actualResource.toURI()) ) ) ) {
			try ( BufferedReader expectedReader = new BufferedReader( new FileReader( new File(expectedResource.toURI()) ) ) ) {
				
				try {
					String actualLine ;
					String expectedLine ;
					do {
						do {
							++expectedLineNumber ;
							expectedLine = expectedReader.readLine() ;
						} while ( null != expectedLine && null != ignoreLines && ignoreLines.matcher(expectedLine).find() ) ;
						do {
							++actualLineNumber ;
							actualLine = actualReader.readLine() ;
						} while ( null != actualLine && null != ignoreLines && ignoreLines.matcher(actualLine).find() ) ;
						
						if ( null != actualLine ) {
							actualQueue.offer(actualLine) ;
							while ( actualQueue.size() > CONTEXT_LINES ) actualQueue.remove() ;
						}
						
						assertThat( finalPathComponent(actualResource) + "#" + actualLineNumber + " is equal to " + finalPathComponent(expectedResource) + "#" + expectedLineNumber, actualLine, equalTo(expectedLine) ) ;
					} while ( null != expectedLine ) ;
					assertThat( "line #" + actualLineNumber + " is the end of file", actualLine, nullValue() ) ;
					passed = true ;
				} finally {
					if ( !passed ) {
						String line ;
						while ( actualQueue.size() > 1 )
							System.out.println( "  > " + actualQueue.remove() ) ;
						if ( !actualQueue.isEmpty() )
							System.out.println( "!!! " + actualQueue.remove() ) ;
						for ( int i = 0 ; i < CONTEXT_LINES ; ++i ) {
							line =  actualReader.readLine() ;
							if ( null != line )
								System.out.println( "  > " + line ) ;
						}
					}
				}
			} 

		}
	}
}
