package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;

public class RoutineANTLRParser implements RoutineParser, CommandParser, ExpressionParser {
	
	public RoutineANTLRParser() {
		
	}

	@Override public CommandParser commandParser() { return this; }
	
	@Override public ExpressionParser expressionParser()  { return this; }

	@Override
	public Routine parse( String text ) {
		return parseRoutine( new ANTLRInputStream(text) ) ;
	}
	
	@Override
	public Routine parse( Reader reader) throws IOException {
		return parseRoutine( new ANTLRInputStream(reader) ) ;
	}
	
	@Override
	public Routine parse( File file, String encoding ) throws IOException {
		return parseRoutine( new ANTLRFileStream(file.getPath(), encoding) ) ;
	}
	
	@Override
	public Routine parse( URL source ) throws IOException {
		try ( InputStream is = source.openStream() ) { return parse(is) ; }
	}
	
	@Override
	public Routine parse( InputStream source ) throws IOException {
		return parseRoutine( new ANTLRInputStream(source) ) ;
	}
	
	@Override
	public Command parseCommand(InputStream source) throws IOException {
		return parseCommand( new ANTLRInputStream(source) ) ;
	}
	@Override
	public Command parseCommand(Reader reader) throws IOException {
		return parseCommand( new ANTLRInputStream(reader) ) ;
	}

	@Override
	public Command parseCommand(String text)  {
		return parseCommand( new ANTLRInputStream(text) ) ;
	}

	@Override
	public Expression parseExpression(InputStream source) throws IOException {
		return parseExpression( new ANTLRInputStream(source) ) ;
	}
	@Override
	public Expression parseExpression(Reader reader) throws IOException {
		return parseExpression( new ANTLRInputStream(reader) ) ;
	}

	@Override
	public Expression parseExpression(String text)  {
		return parseExpression( new ANTLRInputStream(text) ) ;
	}

	private MumpsParser makeMumpsParser(ANTLRInputStream source) {
		MumpsLexer lexer = new MumpsLexer(source) ;
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		MumpsParser parser = new MumpsParser(tokens) ;
		return parser ;
	}
	
	private Routine parseRoutine( ANTLRInputStream source ) {
		MumpsParser parser = makeMumpsParser(source) ;
		return  parser.routine().result ;
	}
	
	private Command parseCommand( ANTLRInputStream source ) {
		MumpsParser parser = makeMumpsParser(source) ;
		return  parser.command().result ;
	}
	
	private Expression parseExpression( ANTLRInputStream source ) {
		MumpsParser parser = makeMumpsParser(source) ;
		return parser.expression().result ;
	}

}
