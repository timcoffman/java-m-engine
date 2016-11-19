package edu.vanderbilt.clinicalsystems.m.lang.text;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ServiceLoader;

import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutput;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutputList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;

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
	public void canParseZeroCommands() throws IOException {
		final List<? extends Command> commands = new RoutineANTLRParser().parseCommandSequence( "\t;comment\r\n" ) ;
		assertThat( commands.size(), equalTo(0) );
	}
	
	@Test
	public void canParseOneCommand() throws IOException {
		final List<? extends Command> commands = new RoutineANTLRParser().parseCommandSequence( "WRITE \"Hello World!\"" ) ;
		assertThat( commands.size(), equalTo(1) );
		Command command = commands.get(0) ;
		assertThat( command.commandType(), equalTo( CommandType.WRITE ) );
		assertThat( command.argument(), instanceOf( InputOutputList.class ) );
		assertThat( ((InputOutputList)command.argument()).elements(), hasItem( InputOutput.wrap(Constant.from("Hello World!")) ) );
	}
	
	@Test
	public void canParseMultipleCommands() throws IOException {
		final List<? extends Command> commands = new RoutineANTLRParser().parseCommandSequence( "WRITE \"Hello World!\" SET X=123 QUIT X" ) ;
		assertThat( commands.size(), equalTo(3) );
		Command command1 = commands.get(0) ;
		Command command2 = commands.get(1) ;
		Command command3 = commands.get(2) ;
		assertThat( command1.commandType(), equalTo( CommandType.WRITE ) );
		assertThat( command2.commandType(), equalTo( CommandType.SET   ) );
		assertThat( command3.commandType(), equalTo( CommandType.QUIT  ) );
	}
	
	@Test
	public void canParseTwoCommands() throws IOException {
		Reader r = new StringReader( "SET ^X=123 SET ^Y=456" ) ;
		final List<? extends Command> commands = new RoutineANTLRParser().parseCommandSequence( r ) ;
		assertThat( commands.size(), equalTo(2) );
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
