package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.File;
import java.util.ServiceLoader;

import org.junit.Test;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.writer.FileCodeWriter;

import edu.vanderbilt.clinicalsystems.epic.lib.Epic;
import edu.vanderbilt.clinicalsystems.m.Core;
import edu.vanderbilt.clinicalsystems.m.lang.Compatibility;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBuilder.JavaMethodContents;

public class WebServiceRoutineWriterTest extends AbstractWriterTest {

	@Test
	public void canWriteJavaStubForLibrary() throws Exception {
		ServiceLoader<RoutineParserFactory> serviceLoader = ServiceLoader.load(RoutineParserFactory.class);
		RoutineParserFactory routineParserFactory = serviceLoader.iterator().next();
		RoutineParser routineParser = routineParserFactory.createRoutineParser();
		
		Routine routine = routineParser.parse(RoutineJavaWriterTest.class.getResource("HULIB.m"));
		
		RoutineJavaUnitBuilder routineBuilder = new RoutineJavaUnitBuilder( JavaMethodContents.STUB );
		routineBuilder.env().additionalCompatibility( Compatibility.EXTENSION ) ;
		Core.useLibrariesIn(routineBuilder.env());
		Epic.useLibrariesIn(routineBuilder.env());
		
		try {
			routineBuilder.build(RoutineJavaWriterTest.class.getPackage().getName(), routine);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		
		File f = makeTempFolder("canWriteJavaStubForLibrary");
		CodeWriter cw = new FileCodeWriter(f);
		try {
			routineBuilder.codeModel().build(cw);
		} finally {
			cw.close();
		}
	}
	
	@Test
	public void canWriteJavaForWebService() throws Exception {
		ServiceLoader<RoutineParserFactory> serviceLoader = ServiceLoader.load(RoutineParserFactory.class);
		RoutineParserFactory routineParserFactory = serviceLoader.iterator().next();
		RoutineParser routineParser = routineParserFactory.createRoutineParser();
		
		Routine routine = routineParser.parse(RoutineJavaWriterTest.class.getResource("PSWEARCVG.m"));
		
		RoutineJavaUnitBuilder routineBuilder = new RoutineJavaUnitBuilder();
		routineBuilder.env().additionalCompatibility( Compatibility.EXTENSION ) ;
		Core.useLibrariesIn(routineBuilder.env());
		Epic.useLibrariesIn(routineBuilder.env());
		
		try {
			routineBuilder.build(RoutineJavaWriterTest.class.getPackage().getName(), routine);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		
		File f = makeTempFolder("canWriteJavaForWebService");
		CodeWriter cw = new FileCodeWriter(f);
		try {
			routineBuilder.codeModel().build(cw);
		} finally {
			cw.close();
		}
	}

}
