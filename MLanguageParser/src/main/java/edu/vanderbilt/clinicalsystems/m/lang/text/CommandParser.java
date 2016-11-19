package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.model.Command;

public interface CommandParser {

	ExpressionParser expressionParser() ;

	List<? extends Command> parseCommandSequence(InputStream source) throws IOException;
	List<? extends Command> parseCommandSequence(Reader reader) throws IOException;

	List<? extends Command> parseCommandSequence(String text) ;

}
