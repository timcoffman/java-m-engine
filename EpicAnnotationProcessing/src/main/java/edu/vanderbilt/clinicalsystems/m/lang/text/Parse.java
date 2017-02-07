package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ServiceLoader;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.writer.FileCodeWriter;

import edu.vanderbilt.clinicalsystems.epic.lib.Epic;
import edu.vanderbilt.clinicalsystems.m.Core;
import edu.vanderbilt.clinicalsystems.m.lang.Compatibility;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;

public class Parse {

	public static int main( String[] args ) throws Exception {
		
		RoutineParserFactory factory = ServiceLoader.load( RoutineParserFactory.class ).iterator().next() ;
		RoutineParser routineParser = factory.createRoutineParser() ;
		
		RoutineJavaUnitBuilder routineBuilder = new RoutineJavaUnitBuilder();
		routineBuilder.env().additionalCompatibility( Compatibility.EXTENSION ) ;
		Core.useLibrariesIn(routineBuilder.env());
		Epic.useLibrariesIn(routineBuilder.env());
		
		File src = new File( args[1] ) ;
		File dst = new File( src.getParentFile(), src.getName().replaceAll("\\.[^.]$", ".java")) ;
		try ( InputStream in = new FileInputStream(src) ) {
				
			Routine routine = routineParser.parse(in) ;

			routineBuilder.build( args[0], routine);
			CodeWriter cw = new FileCodeWriter(dst);
			try {
				routineBuilder.codeModel().build(cw);
			} finally {
				cw.close();
			}

		}
		
		return 0 ;
	}
	
}
