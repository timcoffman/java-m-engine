package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import edu.vanderbilt.clinicalsystems.m.lang.model.Command;

public interface CommandParser {

	ExpressionParser expressionParser() ;

	Command parseCommand(InputStream source) throws IOException;
	Command parseCommand(Reader reader) throws IOException;

	Command parseCommand(String text) ;

}
