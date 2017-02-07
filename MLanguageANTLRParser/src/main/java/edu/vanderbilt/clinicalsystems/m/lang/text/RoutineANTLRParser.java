package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.MatchPattern;

public class RoutineANTLRParser implements RoutineParser, CommandParser, ExpressionParser {
	
	private Optional<Collection<RoutineParseListener>> m_listeners = Optional.empty();
	
	public RoutineANTLRParser() {
		
	}

	@Override public CommandParser commandParser() { return this; }
	
	@Override public ExpressionParser expressionParser()  { return this; }

	@Override public void listen( RoutineParseListener listener ) {
		if ( !m_listeners.isPresent() )
			m_listeners = Optional.of( new ArrayList<RoutineParseListener>() ) ;
		m_listeners.get().add(listener) ;
	}

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

	@Override
	public MatchPattern parseMatchPattern(InputStream source) throws IOException {
		return parseMatchPattern( new ANTLRInputStream(source) ) ;
	}
	@Override
	public MatchPattern parseMatchPattern(Reader reader) throws IOException {
		return parseMatchPattern( new ANTLRInputStream(reader) ) ;
	}
	
	@Override
	public MatchPattern parseMatchPattern(String text)  {
		return parseMatchPattern( new ANTLRInputStream(text) ) ;
	}
	
	private MumpsParser makeMumpsParser(ANTLRInputStream source) {
		MumpsLexer lexer = new MumpsLexer(source) ;
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		MumpsParser parser = new MumpsParser(tokens) ;
		if ( ! m_listeners.map( Collection::isEmpty ).orElse(true) ) {
			Collection<RoutineParseListener> listeners = m_listeners.get();
			parser.addParseListener( new ParseTreeListener() {
				@Override public void enterEveryRule(ParserRuleContext parserRuleContext) { /* nothing */ }
				@Override public void exitEveryRule(ParserRuleContext parserRuleContext) { /* nothing */ }
				@Override public void visitErrorNode(ErrorNode errorNode) { /* nothing */ }
				@Override public void visitTerminal(TerminalNode terminalNode) {
					listeners.forEach( (x)->x.parsingText(terminalNode.getText()) ) ;
				}
			});
		}
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
		return parser.completeExpression().result ;
	}

	private MatchPattern parseMatchPattern( ANTLRInputStream source ) {
		MumpsParser parser = makeMumpsParser(source) ;
		return parser.completeMatchPattern().result ;
	}
	
}
