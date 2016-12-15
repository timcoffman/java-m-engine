package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ServiceLoader;

import org.junit.Test;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.writer.FileCodeWriter;

import edu.vanderbilt.clinicalsystems.epic.lib.Epic;
import edu.vanderbilt.clinicalsystems.m.Core;
import edu.vanderbilt.clinicalsystems.m.lang.Compatibility;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;

public class RoutineJavaWriterTest {

	@Test
	public void canInferTypes() throws Exception {
		ServiceLoader<RoutineParserFactory> serviceLoader = ServiceLoader.load(RoutineParserFactory.class);
		RoutineParserFactory routineParserFactory = serviceLoader.iterator().next();
		RoutineParser routineParser = routineParserFactory.createRoutineParser();
		
		Routine routine = routineParser.parse(RoutineJavaWriterTest.class.getResource("INFERENCE.m"));

		RoutineJavaUnitBuilder routineBuilder = new RoutineJavaUnitBuilder();
		routineBuilder.env().additionalCompatibility( Compatibility.EXTENSION ) ;
		Core.useLibrariesIn(routineBuilder.env());
		Epic.useLibrariesIn(routineBuilder.env());

		try {
			routineBuilder.build(RoutineJavaWriterTest.class.getPackage().getName(), routine);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}

		File f = new File(
				"/Users/timvanderbilt/Documents/Development/Epic/workspace-trunk/"
				+ "EpicAnnotationProcessing/"
				+ "src/test/java"
			);
		CodeWriter cw = new FileCodeWriter(f);
		try {
			routineBuilder.codeModel().build(cw);
		} finally {
			cw.close();
		}
	}

//	@Test
	public void canSerializeRoutine() throws Exception {
		URL src = RoutineJavaWriterTest.class.getResource("EALIBECF1.m");
		File dst = new File(src.toURI().resolve("EALIBECF1.ser"));

		ServiceLoader<RoutineParserFactory> serviceLoader = ServiceLoader.load(RoutineParserFactory.class);
		RoutineParserFactory routineParserFactory = serviceLoader.iterator().next();
		RoutineParser routineParser = routineParserFactory.createRoutineParser();
		
		Routine routine = routineParser.parse(src);

		try (ObjectOutputStream s = new ObjectOutputStream( new FileOutputStream(dst))) {
			s.writeObject(routine);
		}
	}

//	@Test
	public void canWriteEpicClass() throws Exception {
		Routine routine;
		try (ObjectInputStream s = new ObjectInputStream( RoutineJavaWriterTest.class.getResourceAsStream("EALIBECF1.ser"))) {
			routine = (Routine) s.readObject();
		}

		RoutineJavaUnitBuilder routineBuilder = new RoutineJavaUnitBuilder();
		routineBuilder.env().additionalCompatibility( Compatibility.EXTENSION ) ;
		Core.useLibrariesIn(routineBuilder.env());
		Epic.useLibrariesIn(routineBuilder.env());

		try {
			routineBuilder.build(RoutineJavaWriterTest.class.getPackage().getName(), routine);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		// ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
		// routineBuilder.codeModel().build( new SingleStreamCodeWriter(baos) );
		//
		// System.out.println( baos.toString("UTF-8") ) ;
		// assertThat( baos.toString("UTF-8"), not(equalTo("")) ) ;

		File f = new File(
				"/Users/timvanderbilt/Documents/Development/Epic/workspace-trunk/"
				+ "EpicAnnotationProcessing/"
				+ "src/test/java"
			);
		CodeWriter cw = new FileCodeWriter(f);
		try {
			routineBuilder.codeModel().build(cw);
		} finally {
			cw.close();
		}
	}

}
