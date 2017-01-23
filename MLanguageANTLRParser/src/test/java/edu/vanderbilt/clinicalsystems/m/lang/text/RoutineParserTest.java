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
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;

import org.hamcrest.Matchers;
import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.ParameterPassMethod;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.Comment;
import edu.vanderbilt.clinicalsystems.m.lang.model.MultilineBlock;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.Tag;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Assignment;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Destination;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.FormatCommand;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutput;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutputList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCallList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.ConditionalExpression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.IndirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.TagReference;

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
	public void canParseBuiltinFunction() throws IOException {
		final Expression expression = new RoutineANTLRParser().parseExpression( "$CHAR(123)" ) ; 
		assertThat( expression, equalTo( new BuiltinFunctionCall( BuiltinFunction.CHAR, Arrays.asList( Constant.from("123") ) ) ) );
	}
	
	@Test
	public void canParseBuiltinSelectFunction() throws IOException {
		final Expression expression = new RoutineANTLRParser().parseExpression( "$SELECT(1:123)" ) ; 
		assertThat( expression, equalTo( new BuiltinFunctionCall( BuiltinFunction.SELECT, Arrays.asList( new ConditionalExpression( Constant.from("1"), Constant.from("123") ) ) ) ) );
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
	public void canParseMergeCommand() throws IOException {
		final List<? extends Command> commands = new RoutineANTLRParser().parseCommandSequence( "MERGE @INDIRECT1=@INDIRECT2@(\"KEY1\",\"KEY2\")" ) ;
		assertThat( commands.size(), equalTo(1) );
		Command command = commands.get(0) ;
		assertThat( command.commandType(), equalTo( CommandType.MERGE) );
		assertThat( command.argument(), instanceOf( AssignmentList.class ) );
		assertThat( ((AssignmentList)command.argument()).elements(), hasItem(
				new Assignment(
						Destination.wrap( new IndirectVariableReference( new DirectVariableReference(Scope.TRANSIENT, "INDIRECT1") ) ),
						new IndirectVariableReference( new DirectVariableReference(ParameterPassMethod.BY_VALUE, Scope.TRANSIENT, "INDIRECT2"), Arrays.asList(Constant.from("KEY1"), Constant.from("KEY2")) )
					)
			) );
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
	public void canParseTaggedRoutineCalls() throws IOException {
		final List<? extends Command> commands = new RoutineANTLRParser().parseCommandSequence( "DO MYTAG^MYROUTINE(\"abc\")" ) ; 
		assertThat( commands.size(), equalTo(1) );
		Command command = commands.get(0) ;
		assertThat( command.commandType(), equalTo( CommandType.DO ) );
		assertThat( command.argument(), instanceOf( TaggedRoutineCallList.class ) );
		assertThat( ((TaggedRoutineCallList)command.argument()).elements(), hasItem( new TaggedRoutineCall( new TagReference("MYTAG", "MYROUTINE"), Arrays.asList(Constant.from("abc"))) ) );
	}
	
	@Test
	public void canParseTwoCommands() throws IOException {
		Reader r = new StringReader( "SET ^X=123 SET ^Y=456" ) ;
		final List<? extends Command> commands = new RoutineANTLRParser().parseCommandSequence( r ) ;
		assertThat( commands.size(), equalTo(2) );
	}
	
	private void printRoutine( Routine routine ) throws RoutineWriterException {
		RoutineNativeFormatter routineFormatter = new RoutineNativeFormatter();
		routineFormatter.options().setCommandsPerLineLimit(1) ;
		routineFormatter.options().setCommentsPerLineLimit(1) ;
		routineFormatter.options().setWriteAbbreviatedBuiltinFunctionSymbols( true ) ;
		routineFormatter.options().setWriteAbbreviatedBuiltinVariableSymbols( true ) ;
		routineFormatter.options().setWriteAbbreviatedCommandSymbols( true ) ;
//		RoutineTreeFormatter routineFormatter = new RoutineTreeFormatter();
		
		System.out.println("---------- Start Routine : " + routine.name() + " ----------" ) ;
		PrintWriter writer = new PrintWriter(System.out,true);
		routine.write( new RoutineLinearWriter( writer, routineFormatter) );
		writer.flush();
		System.out.println("----------- End Routine -----------" ) ;
	}
	
	@Test
	public void canParse() throws IOException, RoutineWriterException {
		
		try ( InputStream is = RoutineParserTest.class.getResourceAsStream("EALIBECF1.m") ) {
			final Routine routine = new RoutineANTLRParser().parse(is) ;
			
			printRoutine( routine ) ;
		}
	}
	
	@Test
	public void canParseEmptyRoutine() throws IOException, RoutineWriterException {
		Reader r = new StringReader(
			"MYROUTINE"
			) ;
		final Routine routine = new RoutineANTLRParser().parse(r) ;
		
		printRoutine( routine ) ;

		assertThat( routine.root().elements(), Matchers.<RoutineElement>contains(
				equalTo( new Tag("MYROUTINE") )
				) ) ;
	}
	
	@Test
	public void canParseRoutineWithSingleCommand() throws IOException, RoutineWriterException {
		Reader r = new StringReader(
				"MYROUTINE ;canParseRoutineWithSingleCommand\n"
				+ "\tQUIT "
				) ;
		final Routine routine = new RoutineANTLRParser().parse(r) ;
		
		printRoutine( routine ) ;
		
		assertThat( routine.root().elements(), Matchers.<RoutineElement>contains(
				equalTo( new Tag("MYROUTINE") ),
				equalTo( new Comment( "canParseRoutineWithSingleCommand" ) ),
				equalTo( new Command(CommandType.QUIT) )
				) ) ;
	}

	@Test
	public void canParseRoutineWithSingleCommandMissingArgument() throws IOException, RoutineWriterException {
		Reader r = new StringReader(
				"MYROUTINE ;canParseRoutineWithSingleCommandMissingArgument\n"
						+ "\tDO\n"
						+ "\t. Q \n"
				) ;
		final Routine routine = new RoutineANTLRParser().parse(r) ;
		
		printRoutine( routine ) ;
		
		assertThat( routine.root().elements(), Matchers.<RoutineElement>contains(
				equalTo( new Tag( "MYROUTINE" ) ),
				equalTo( new Comment( "canParseRoutineWithSingleCommandMissingArgument" ) ),
				equalTo( new Command( CommandType.DO, new MultilineBlock(
						new Command( CommandType.QUIT )
					) ) )
				) ) ;
	}
	

	@Test
	public void canParseMissingBlock() throws IOException, RoutineWriterException {
		Reader r = new StringReader(
				"MYROUTINE ;canParseMissingBlock\n"
						+ "\tDO \n"
						+ "\tQ \n"
				) ;
		final Routine routine = new RoutineANTLRParser().parse(r) ;
		
		printRoutine( routine ) ;
		
		assertThat( routine.root().elements(), Matchers.<RoutineElement>contains(
				equalTo( new Tag( "MYROUTINE" ) ),
				equalTo( new Comment( "canParseMissingBlock" ) ),
				equalTo( new Command( CommandType.DO, new MultilineBlock() ) ),
				equalTo( new Command( CommandType.QUIT ) )
				) ) ;
	}
	
	
	@Test
	public void canParseBlockDepthOne() throws IOException, RoutineWriterException {
		Reader r = new StringReader(
				"MYROUTINE ;canParseBlockDepthOne\n"
					+ "\tDO \n"
					+ "\t. Q \n"
			) ;
		final Routine routine = new RoutineANTLRParser().parse(r) ;
		
		printRoutine( routine ) ;
		
		assertThat( routine.root().elements(), Matchers.<RoutineElement>contains(
				equalTo( new Tag("MYROUTINE") ),
				equalTo( new Comment( "canParseBlockDepthOne" ) ),
				equalTo( new Command(CommandType.DO, new MultilineBlock(
						new Command(CommandType.QUIT)
					) ))
				) ) ;
	}

	@Test
	public void canParseBlockDepthTwo() throws IOException, RoutineWriterException {
		Reader r = new StringReader(
				"MYROUTINE ;canParseBlockDepthTwo\n"
					+ "\tDO \n"
					+ "\t. DO \n"
					+ "\t. . Q \n"
			) ;
		final Routine routine = new RoutineANTLRParser().parse(r) ;
		
		printRoutine( routine ) ;
		
		assertThat( routine.root().elements(), Matchers.<RoutineElement>contains(
				equalTo( new Tag("MYROUTINE") ),
				equalTo( new Comment( "canParseBlockDepthTwo" ) ),
				equalTo( new Command(CommandType.DO, new MultilineBlock(
						new Command(CommandType.DO, new MultilineBlock(
							new Command(CommandType.QUIT)
						) )
					)) )
				) ) ;
	}
	
	@Test
	public void canParseMultilevelBlock() throws IOException, RoutineWriterException {
		Reader r = new StringReader(
				"MYROUTINE ;canParseMultilevelBlock\n"
						+ "\tDO \n"
						+ "\t. WRITE !\n"
						+ "\t. DO \n"
						+ "\t. . WRITE !\n"
						+ "\t. . Q \n"
						+ "\t. WRITE !\n"
						+ "\tWRITE !\n"
				) ;
		final Routine routine = new RoutineANTLRParser().parse(r) ;
		
		printRoutine( routine ) ;
		
		assertThat( routine.root().elements(), Matchers.<RoutineElement>contains(
				equalTo( new Tag("MYROUTINE") ),
				equalTo( new Comment( "canParseMultilevelBlock" ) ),
				equalTo( new Command(CommandType.DO, new MultilineBlock(
						new Command(CommandType.WRITE,new InputOutputList(FormatCommand.carriageReturn())),
						new Command(CommandType.DO, new MultilineBlock(
								new Command(CommandType.WRITE,new InputOutputList(FormatCommand.carriageReturn())),
								new Command(CommandType.QUIT)
							) ),
							new Command(CommandType.WRITE,new InputOutputList(FormatCommand.carriageReturn()))
						)) ),
				equalTo( new Command(CommandType.WRITE,new InputOutputList(FormatCommand.carriageReturn())) )
				) ) ;
	}
}
