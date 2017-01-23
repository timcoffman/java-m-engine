package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.ErrorCode;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.Executor.ExecutionResult;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinSystemVariable;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinVariable;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.Comment;
import edu.vanderbilt.clinicalsystems.m.lang.model.InlineBlock;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.Tag;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Assignment;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.DeclarationList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Destination;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.FormatCommand;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutput;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutputList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCallList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BinaryOperation;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinSystemVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.FunctionCall.Returning;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.TagReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class DatabaseTest {

	private Database m_db ;
	private Connection m_cxn ;
	private LineBufferInputOutputDevice m_io ;
	
	@Before
	public void openDatabaseConnection() {
		m_db = new Database() ;
		m_io = new LineBufferInputOutputDevice() ;
		m_cxn = m_db.openConnection(m_io) ;
	}

	@After
	public void closeDatabaseConnection() {
		m_cxn.close() ;
	}

	private Command makeDeclarationCommand( String variableName, Scope scope ) {
		DirectVariableReference variable = new DirectVariableReference(scope, variableName);
		Command command = new Command( CommandType.NEW, new DeclarationList( variable ) ) ;
		return command ;
	}
	
	private Command makeAssignmentCommand( String variableName, Scope scope, Expression source ) {
		return makeAssignmentCommand( variableName, Collections.emptyList(), scope, source ) ;
	}
	
	private Command makeAssignmentCommand( String variableName, List<Expression> keys, Scope scope, Expression source ) {
		DirectVariableReference variable = new DirectVariableReference(scope, variableName, keys );
		Command command = new Command( CommandType.SET, new AssignmentList( new Assignment( Destination.wrap(variable), source) ) ) ;
		return command ;
	}
	
	private Command makeMergeCommand( String variableName, Scope scope, Expression source ) {
		return makeMergeCommand( variableName, Collections.emptyList(), scope, source ) ;
	}
	
	private Command makeMergeCommand( String variableName, List<Expression> keys, Scope scope, Expression source ) {
		DirectVariableReference variable = new DirectVariableReference(scope, variableName, keys );
		Command command = new Command( CommandType.MERGE, new AssignmentList( new Assignment( Destination.wrap(variable), source) ) ) ;
		return command ;
	}
	
	private Command makeMergeCommand( BuiltinSystemVariable builtinVariable, List<Expression> keys, Expression source ) {
		BuiltinSystemVariableReference variable = new BuiltinSystemVariableReference( builtinVariable, keys );
		Command command = new Command( CommandType.MERGE, new AssignmentList( new Assignment( Destination.wrap(variable), source) ) ) ;
		return command ;
	}
	
	private Command makeTaggedRoutineCallCommand( String tagName, String routineName, Expression ... parameters ) {
		TagReference tagRef = new TagReference(tagName, routineName) ;
		TaggedRoutineCall taggedRoutineCall = new TaggedRoutineCall(tagRef ) ;
		TaggedRoutineCallList taggedRoutineCallList = new TaggedRoutineCallList( taggedRoutineCall ) ;
		Command command = new Command( CommandType.DO, taggedRoutineCallList ) ;
		return command ;
	}
	
	private Command makeTaggedFunctionCallCommand( String tagName, String routineName, Expression ... parameters ) {
		TagReference tagRef = new TagReference(tagName, routineName) ;
		RoutineFunctionCall routineFunctionCall = new RoutineFunctionCall(tagRef,Returning.SOME_VALUE,Expression.list(parameters)) ;
		Command command = new Command( CommandType.QUIT, new ExpressionList(routineFunctionCall) ) ;
		return command ;
	}
	
	private Command makeExecuteCommand( String unparsedMumpsCode ) {
		Command command = new Command( CommandType.EXECUTE, new ExpressionList( Constant.from(unparsedMumpsCode) ) );
		return command ;
	}
	
	private Routine makeSimpleRoutine( String routineName, RoutineElement ... elements ) {
		Routine routine = new Routine() ;
		routine.appendElement( new Tag(routineName) );
		routine.appendElement( new Comment( routineName.toLowerCase() ) );
		
		RoutineElement lastRoutineElement = null;
		for (RoutineElement routineElement : elements)
			routine.appendElement( lastRoutineElement=routineElement );
		if ( !(lastRoutineElement instanceof Command) || CommandType.QUIT != ((Command)lastRoutineElement).commandType() )
			routine.appendElement( new Command( CommandType.QUIT, Argument.NOTHING ) );
		return routine ;
	}
	
	@Test
	public void canSetVariable() throws EngineException {
		ExecutionResult result = m_cxn.execute( makeAssignmentCommand( "X", Scope.PERSISTENT, Constant.from(123) ) );
		
		assertThat( result, equalTo(ExecutionResult.CONTINUE) );
		assertThat( m_db.at("X").value(), equalTo("123") ) ;
	}
	
	@Test
	public void canAssignLocalVariableInOuterFrame() throws EngineException {
		m_cxn.execute( makeAssignmentCommand( "x", Scope.TRANSIENT, Constant.from(123) ) );
		try ( ExecutionFrame frame = m_cxn.createChildFrame() ) {
			frame.execute( makeAssignmentCommand( "x", Scope.TRANSIENT, Constant.from(456) ) ) ;
			
			assertThat( frame.findNode( new DirectVariableReference(Scope.TRANSIENT, "x") ).value(), equalTo("456") ) ;
		}

		assertThat( m_cxn.findNode( new DirectVariableReference(Scope.TRANSIENT, "x") ).value(), equalTo("456") ) ;
	}
	
	@Test
	public void canHideLocalVariableInOuterFrame() throws EngineException {
		m_cxn.execute( makeAssignmentCommand( "x", Scope.TRANSIENT, Constant.from(123) ) );
		try ( ExecutionFrame frame = m_cxn.createChildFrame() ) {
			frame.execute( makeDeclarationCommand( "x", Scope.TRANSIENT ) );
			frame.execute( makeAssignmentCommand( "x", Scope.TRANSIENT, Constant.from(456) ) ) ;
			
			assertThat( frame.findNode( new DirectVariableReference(Scope.TRANSIENT, "x") ).value(), equalTo("456") ) ;
		}
		
		assertThat( m_cxn.findNode( new DirectVariableReference(Scope.TRANSIENT, "x") ).value(), equalTo("123") ) ;
	}
	
	@Test
	public void canMergeVariables() throws EngineException {
		m_cxn.execute( makeAssignmentCommand( "X", Scope.PERSISTENT, Constant.from("-x-") ) );
		m_cxn.execute( makeAssignmentCommand( "X", asList(Constant.from("a")), Scope.PERSISTENT, Constant.from(123) ) );
		m_cxn.execute( makeAssignmentCommand( "X", asList(Constant.from("b")), Scope.PERSISTENT, Constant.from(456) ) );
		
		m_cxn.execute( makeAssignmentCommand( "Y", Scope.PERSISTENT, Constant.from("-y-") ) );
		m_cxn.execute( makeAssignmentCommand( "Y", asList(Constant.from("b")), Scope.PERSISTENT, Constant.from(789) ) );
		m_cxn.execute( makeAssignmentCommand( "Y", asList(Constant.from("c")), Scope.PERSISTENT, Constant.from(555) ) );
		
		ExecutionResult result = m_cxn.execute( makeMergeCommand( "X", Scope.PERSISTENT, new DirectVariableReference( Scope.PERSISTENT, "Y") ) );
		
		assertThat( result, equalTo(ExecutionResult.CONTINUE) );
		assertThat( m_db.at("X").value(), equalTo("-y-") ) ;
		assertThat( m_db.at("X").at("a").value(), equalTo("123") ) ;
		assertThat( m_db.at("X").at("b").value(), equalTo("789") ) ;
		assertThat( m_db.at("X").at("c").value(), equalTo("555") ) ;
	}
	
	@Test
	public void canExecuteArbitraryCode() throws EngineException {
		ExecutionResult result = m_cxn.execute( makeExecuteCommand( "SET ^X=123 SET ^Y=456" ) );
		
		assertThat( result, equalTo(ExecutionResult.CONTINUE) );
		assertThat( m_db.at("X").value(), equalTo("123") ) ;
		assertThat( m_db.at("Y").value(), equalTo("456") ) ;
	}
	
	@Test
	public void canReadInput() throws EngineException {
		m_io.offer( "test line" );
		ExecutionResult result = m_cxn.execute( makeExecuteCommand( "READ ^X" ) );
		
		assertThat( result, equalTo(ExecutionResult.CONTINUE) );
		assertThat( m_db.at("X").value(), equalTo("test line") ) ;
	}
	
	@Test
	public void canWriteOutput() throws EngineException {
		ExecutionResult result = m_cxn.execute( makeExecuteCommand( "WRITE \"test line\",!" ) );
		
		assertThat( result, equalTo(ExecutionResult.CONTINUE) );
		assertThat( m_io.take(), equalTo("test line") ) ;
	}
	
	@Test
	public void canIterateOverKeys() throws EngineException {
		m_cxn.execute( makeDeclarationCommand( "x", Scope.TRANSIENT ) ) ;
		m_cxn.execute( makeAssignmentCommand( "x", asList(Constant.from("0x"  )), Scope.TRANSIENT, Constant.from("mixed numbers and uppercase letters") ) ) ;
		m_cxn.execute( makeAssignmentCommand( "x", asList(Constant.from("Abc" )), Scope.TRANSIENT, Constant.from("few uppercase letters") ) ) ;
		m_cxn.execute( makeAssignmentCommand( "x", asList(Constant.from("Abcd")), Scope.TRANSIENT, Constant.from("more uppercase letters") ) ) ;
		m_cxn.execute( makeAssignmentCommand( "x", asList(Constant.from("abc" )), Scope.TRANSIENT, Constant.from("few lowercase letters") ) ) ;
		m_cxn.execute( makeAssignmentCommand( "x", asList(Constant.from("abcd")), Scope.TRANSIENT, Constant.from("more lowercase letters") ) ) ;
		m_cxn.execute( makeAssignmentCommand( "x", asList(Constant.from("123" )), Scope.TRANSIENT, Constant.from("number") ) ) ;
		m_cxn.execute( makeAssignmentCommand( "x", asList(Constant.from("!!!" )), Scope.TRANSIENT, Constant.from("early punctuation") ) ) ;
		m_cxn.execute( makeAssignmentCommand( "x", asList(Constant.from("???" )), Scope.TRANSIENT, Constant.from("middle punctuation") ) ) ;
		m_cxn.execute( makeAssignmentCommand( "x", asList(Constant.from("___" )), Scope.TRANSIENT, Constant.from("late punctuation") ) ) ;
		
		final DirectVariableReference keyVar = new DirectVariableReference(Scope.TRANSIENT, "key");
		final DirectVariableReference subscriptedVar = new DirectVariableReference(Scope.TRANSIENT, "x", Expression.list(keyVar) );
		final BuiltinFunctionCall nextKeyCall = new BuiltinFunctionCall( BuiltinFunction.ORDER, Expression.list(subscriptedVar));
		m_cxn.execute( makeDeclarationCommand( "key", Scope.TRANSIENT ) ) ;
		m_cxn.execute( makeAssignmentCommand( "key", Scope.TRANSIENT, nextKeyCall ) ) ;
		ExecutionResult result = m_cxn.execute( new Command( CommandType.FOR, Argument.NOTHING, new InlineBlock(
				/* Q:key=""         */ new Command( new BinaryOperation(keyVar, OperatorType.EQUALS, Constant.NULL), CommandType.QUIT, Argument.NOTHING ),
				/* W x(key)         */ new Command( CommandType.WRITE, new InputOutputList( InputOutput.wrap( subscriptedVar ), FormatCommand.carriageReturn() ) ),
				/* S key=$O(x(key)) */ makeAssignmentCommand( "key", Scope.TRANSIENT, nextKeyCall )
		) ) );
		
		assertThat( result, equalTo(ExecutionResult.CONTINUE) );
		assertThat( m_io.take(), equalTo("number") ) ;
		assertThat( m_io.take(), equalTo("early punctuation") ) ;
		assertThat( m_io.take(), equalTo("mixed numbers and uppercase letters") ) ;
		assertThat( m_io.take(), equalTo("middle punctuation") ) ;
		assertThat( m_io.take(), equalTo("few uppercase letters") ) ;
		assertThat( m_io.take(), equalTo("more uppercase letters") ) ;
		assertThat( m_io.take(), equalTo("late punctuation") ) ;
		assertThat( m_io.take(), equalTo("few lowercase letters") ) ;
		assertThat( m_io.take(), equalTo("more lowercase letters") ) ;
		assertThat( m_io.take(), nullValue() ) ;
	}
	
	@Test
	public void cannotCallMissingRoutine() throws EngineException {
		ExecutionResult result = m_cxn.execute( makeTaggedRoutineCallCommand( null, "missing routine" ) );
		
		assertThat( result, equalTo(ExecutionResult.ERROR) );
		assertThat( m_cxn.error(), notNullValue() ) ;
		assertThat( m_cxn.error().errorCode(), equalTo(ErrorCode.MISSING_ROUTINE) ) ;
	}
	
	@Test
	public void cannotCallMissingTag() throws RoutineWriterException, EngineException {
		m_db.install( makeSimpleRoutine( "TESTROUTINE" ) ) ;
		
		ExecutionResult result = m_cxn.execute( makeTaggedRoutineCallCommand( "missing tag", "TESTROUTINE" ) );
		
		assertThat( result, equalTo(ExecutionResult.ERROR) );
		assertThat( m_cxn.error(), notNullValue() ) ;
		assertThat( m_cxn.error().errorCode(), equalTo(ErrorCode.MISSING_TAG) ) ;
	}
	
	@Test
	public void canInstallAndExecuteRoutine() throws RoutineWriterException, EngineException {
		m_db.install(
			makeSimpleRoutine( "TESTROUTINE",
				makeAssignmentCommand( "X", Scope.PERSISTENT, Constant.from(123) )
				)
			);
		
		Node routineNode = m_db.at( BuiltinSystemVariable.ROUTINE ).at( "TESTROUTINE" );
		assertThat( routineNode, notNullValue() ) ;
		assertThat( routineNode.firstKey(), equalTo("0") ) ;
		assertThat( routineNode.lastKey(),  equalTo("3") ) ; /* as formatted, three lines (1..3) */
		
		ExecutionResult result = m_cxn.execute( makeTaggedRoutineCallCommand( null, "TESTROUTINE" ) );
		
		assertThat( result, equalTo(ExecutionResult.CONTINUE) );
		assertThat( m_db.at("X").value(), equalTo("123") ) ;
	}
	
	@Test
	public void canInstallRoutineAndExecuteTag() throws RoutineWriterException, EngineException {
		m_db.install(
			makeSimpleRoutine( "TESTROUTINE",
				makeAssignmentCommand( "X", Scope.PERSISTENT, Constant.from(123) ),
				new Tag( "testtag" ),
				makeAssignmentCommand( "X", Scope.PERSISTENT, Constant.from(456) )
				)
			);
		
		Node routineNode = m_db.at( BuiltinSystemVariable.ROUTINE ).at( "TESTROUTINE" );
		assertThat( routineNode, notNullValue() ) ;
		assertThat( routineNode.firstKey(), equalTo("0") ) ;
		assertThat( routineNode.lastKey(),  equalTo("4") ) ; /* as formatted, three lines (1..3) */
		
		ExecutionResult result = m_cxn.execute( makeTaggedRoutineCallCommand( "testtag", "TESTROUTINE" ) );
		
		assertThat( result, equalTo(ExecutionResult.CONTINUE) );
		assertThat( m_db.at("X").value(), equalTo("456") ) ;
	}
	
	@Test
	public void canInstallAndReturnValueFromRoutine() throws RoutineWriterException, EngineException {
		m_db.install(
			makeSimpleRoutine( "TESTROUTINE",
				makeAssignmentCommand( "X", Scope.PERSISTENT, Constant.from(123) ),
				new Command( CommandType.QUIT, new ExpressionList(new DirectVariableReference(Scope.PERSISTENT, "X")) )
				)
			);
		
		ExecutionResult result = m_cxn.execute( makeTaggedFunctionCallCommand( null, "TESTROUTINE" ) );
		
		assertThat( result, equalTo(ExecutionResult.QUIT) );
		assertThat( m_cxn.result(), equalTo( EvaluationResult.fromConstant(Constant.from(123)) ) ) ;
	}
	
	@Test
	public void canEvaluateBuiltinVariable() throws RoutineWriterException, EngineException {
		ExecutionResult result = m_cxn.execute( new Command( CommandType.QUIT, new ExpressionList(new BuiltinVariableReference(BuiltinVariable.HOROLOG) ) ) ) ;
		assertThat( result, equalTo(ExecutionResult.QUIT) );
		assertThat( m_cxn.result(), notNullValue() ) ;
		assertThat( m_cxn.result(), not(equalTo(EvaluationResult.fromConstant(Constant.from("")))) ) ;
	}
	
	@Test
	public void canInstallRoutineViaRoutineSystemVariable() throws RoutineWriterException, EngineException {
		m_cxn.execute( makeDeclarationCommand("x", Scope.TRANSIENT ) ) ;
		m_cxn.execute( makeAssignmentCommand( "x", asList(Constant.from("1")), Scope.TRANSIENT, Constant.from("TESTROUTINE") ) ) ;
		m_cxn.execute( makeAssignmentCommand( "x", asList(Constant.from("2")), Scope.TRANSIENT, Constant.from(" N y S ^Y=100+23") ) ) ;
		m_cxn.execute( makeAssignmentCommand( "x", asList(Constant.from("3")), Scope.TRANSIENT, Constant.from(" Q y") ) ) ;
		m_cxn.execute( makeMergeCommand( BuiltinSystemVariable.ROUTINE, asList(Constant.from("TESTROUTINE")), new DirectVariableReference(Scope.TRANSIENT,"x") ) ) ;

		Node routineNode = m_db.at( BuiltinSystemVariable.ROUTINE ).at( "TESTROUTINE" );
		assertThat( routineNode, notNullValue() ) ;
		assertThat( routineNode.firstKey(), equalTo("0") ) ;
		assertThat( routineNode.lastKey(),  equalTo("3") ) ; /* as defined, three lines (1..3) */
		
		CompiledRoutine routine = m_db.lookup( "TESTROUTINE" ) ;
		assertThat( routine, notNullValue() ) ;
		
		ExecutionResult result = m_cxn.execute( makeTaggedRoutineCallCommand( null, "TESTROUTINE" ) );
		assertThat( result, equalTo(ExecutionResult.CONTINUE) );
		assertThat( m_db.at("Y").value(), equalTo("123") ) ;
		
	}
	
}
