package edu.vanderbilt.clinicalsystems.m.lang.text;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ServiceLoader;

import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;

public class RoutineParserTest {

	@Test
	public void canFindRegisteredRoutineParserFactory() throws URISyntaxException {
		ServiceLoader<RoutineParserFactory> serviceLoader = ServiceLoader.load( RoutineParserFactory.class ) ;
		assertThat( serviceLoader, hasItem( isA(RoutineANTLRParserFactory.class) ) ) ;
	}
	
	@Test
	public void canFindRegisteredCommandParserFactory() throws URISyntaxException {
		ServiceLoader<CommandParserFactory> serviceLoader = ServiceLoader.load( CommandParserFactory.class ) ;
		assertThat( serviceLoader, hasItem( isA(RoutineANTLRParserFactory.class) ) ) ;
	}
	
	@Test
	public void canFindRegisteredExpressionParserFactory() throws URISyntaxException {
		ServiceLoader<ExpressionParserFactory> serviceLoader = ServiceLoader.load( ExpressionParserFactory.class ) ;
		assertThat( serviceLoader, hasItem( isA(RoutineANTLRParserFactory.class) ) ) ;
	}
	

	@Test
	public void canParseOneTag() throws IOException {
		final Routine routine = new RoutineANTLRParser().parse( "MyTag" ) ; 
		assertThat( routine.name(), equalTo("MyTag") );
	}
	
	@Test
	public void canParse() throws IOException, RoutineWriterException {
		
		try ( InputStream is = RoutineParserTest.class.getResourceAsStream("EALIBECF1.m") ) {
			final Routine routine = new RoutineANTLRParser().parse(is) ;
			
			RoutineNativeFormatter routineFormatter = new RoutineNativeFormatter();
			routineFormatter.options().setCommandsPerLineLimit(1) ;
			routineFormatter.options().setCommentsPerLineLimit(1) ;
			routineFormatter.options().setWriteAbbreviatedBuiltinFunctionSymbols( true ) ;
			routineFormatter.options().setWriteAbbreviatedBuiltinVariableSymbols( true ) ;
			routineFormatter.options().setWriteAbbreviatedCommandSymbols( true ) ;
//			RoutineTreeFormatter routineFormatter = new RoutineTreeFormatter();
			
			System.out.println("---------- Start Routine ----------" ) ;
			PrintWriter writer = new PrintWriter(System.out,true);
			routine.write( new RoutineLinearWriter( writer, routineFormatter) );
			writer.flush();
			System.out.println("----------- End Routine -----------" ) ;
			
		}
	}
	
}
