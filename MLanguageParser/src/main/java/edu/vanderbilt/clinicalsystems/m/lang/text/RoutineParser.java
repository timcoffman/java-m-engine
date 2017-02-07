package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;

public interface RoutineParser {

	CommandParser commandParser() ;
	ExpressionParser expressionParser() ;
	
	Routine parse(URL source) throws IOException;
	Routine parse(File file, String encoding) throws IOException;
	
	Routine parse(InputStream source) throws IOException;
	Routine parse(Reader reader) throws IOException;

	Routine parse(String text) ;

	void listen( RoutineParseListener listener );
}
