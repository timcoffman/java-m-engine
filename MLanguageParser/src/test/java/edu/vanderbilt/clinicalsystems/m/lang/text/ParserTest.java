package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;

public class ParserTest {


	@Test
	public void canParseOneTag() throws IOException {
		StringReader r = new StringReader("MyTag") ;
		MumpsLexer lexer = new MumpsLexer(new ANTLRInputStream(r)) ;
		CommonTokenStream tokens = new CommonTokenStream(lexer);
//		assertThat( tokens.get(0).getText(), equalTo("MyTag") );
	}
	
	@Test
	public void canParse() throws IOException, RoutineWriterException {
		
		try ( InputStream is = ParserTest.class.getResourceAsStream("EALIBECF1.m") ) {
//		try ( InputStream is = ParserTest.class.getResourceAsStream("Sample.m") ) {
			MumpsLexer lexer = new MumpsLexer(new ANTLRInputStream(is)) ;
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			MumpsParser parser = new MumpsParser(tokens) ;
//			parser.setErrorHandler( new BailErrorStrategy() );
			MumpsParser.RoutineContext routineContext = parser.routine() ;
			
			MumpsParserListener extractor = new MumpsParserBaseListener() {
				
			};
			ParseTreeWalker.DEFAULT.walk( extractor, routineContext ) ;
			final Routine routine = routineContext.result ;
			
			RoutineNativeFormatter routineFormatter = new RoutineNativeFormatter();
			routineFormatter.options().setCommandsPerLineLimit(1) ;
			routineFormatter.options().setCommentsPerLineLimit(1) ;
//			RoutineTreeFormatter routineFormatter = new RoutineTreeFormatter();
			
			System.out.println("---------- Start Routine ----------" ) ;
			PrintWriter writer = new PrintWriter(System.out,true);
			routine.write( new RoutineLinearWriter( writer, routineFormatter) );
			writer.flush();
			System.out.println("----------- End Routine -----------" ) ;
			
		}
	}
	
}
