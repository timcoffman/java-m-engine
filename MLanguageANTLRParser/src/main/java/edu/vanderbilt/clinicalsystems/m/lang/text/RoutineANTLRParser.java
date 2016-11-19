package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
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
	public List<? extends Command> parseCommandSequence(InputStream source) throws IOException {
		return parseCommandSequence( new ANTLRInputStream(source) ) ;
	}
	@Override
	public List<? extends Command> parseCommandSequence(Reader reader) throws IOException {
		return parseCommandSequence( new ANTLRInputStream(reader) ) ;
	}

	@Override
	public List<? extends Command> parseCommandSequence(String text)  {
		return parseCommandSequence( new ANTLRInputStream(text) ) ;
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
	
	private List<? extends Command> parseCommandSequence( ANTLRInputStream source ) {
		List<Command> commands = new ArrayList<Command>() ;
		
		MumpsParser parser = makeMumpsParser(source) ;
		Block block = parser.commandSequence().result ;
		if ( null != block )
			for ( RoutineElement routineElement : block.elements() )
				commands.add( (Command)routineElement ) ;
		return commands ;
	}
	
	private Expression parseExpression( ANTLRInputStream source ) {
		MumpsParser parser = makeMumpsParser(source) ;
		return parser.expression().result ;
	}

}
