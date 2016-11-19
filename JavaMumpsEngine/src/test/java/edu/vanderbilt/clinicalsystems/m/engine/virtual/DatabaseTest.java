package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.ErrorCode;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.Executor.ExecutionResult;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinSystemVariable;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.RoutineAccess;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.Comment;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.Tag;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Assignment;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Destination;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
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

	private Command makeAssignmentCommand( String variableName, Scope scope, Expression source ) {
		DirectVariableReference variable = new DirectVariableReference(Scope.GLOBAL, "X");
		Command command = new Command( CommandType.SET, new AssignmentList( new Assignment( Destination.wrap(variable), source) ) ) ;
		return command ;
	}
	
	private Command makeTaggedRoutineCallCommand( String tagName, String routineName, Expression ... parameters ) {
		TaggedRoutineCall taggedRoutineCall = new TaggedRoutineCall(tagName, routineName, RoutineAccess.EXPLICIT ) ;
		Command command = new Command( CommandType.DO, taggedRoutineCall ) ;
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
		ExecutionResult result = m_cxn.execute( makeAssignmentCommand( "X", Scope.GLOBAL, Constant.from(123) ) );
		
		assertThat( result, equalTo(ExecutionResult.CONTINUE) );
		assertThat( m_db.at("X").value(), equalTo("123") ) ;
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
				makeAssignmentCommand( "X", Scope.GLOBAL, Constant.from(123) )
				)
			);
		
		Node routineNode = m_db.at( BuiltinSystemVariable.ROUTINE ).at( "TESTROUTINE" );
		assertThat( routineNode, notNullValue() ) ;
		assertThat( routineNode.firstKey(), equalTo("0") ) ;
		assertThat( routineNode.lastKey(),  equalTo("3") ) ; /* as formatted, three lines (1..3) */
		
		ExecutionResult result = m_cxn.execute( makeTaggedRoutineCallCommand( null, "TESTROUTINE" ) );
		
		assertThat( result, equalTo(ExecutionResult.QUIT) );
		assertThat( m_db.at("X").value(), equalTo("123") ) ;
	}
	
	@Test
	public void canInstallRoutineAndExecuteTag() throws RoutineWriterException, EngineException {
		m_db.install(
			makeSimpleRoutine( "TESTROUTINE",
				makeAssignmentCommand( "X", Scope.GLOBAL, Constant.from(123) ),
				new Tag( "testtag" ),
				makeAssignmentCommand( "X", Scope.GLOBAL, Constant.from(456) )
				)
			);
		
		Node routineNode = m_db.at( BuiltinSystemVariable.ROUTINE ).at( "TESTROUTINE" );
		assertThat( routineNode, notNullValue() ) ;
		assertThat( routineNode.firstKey(), equalTo("0") ) ;
		assertThat( routineNode.lastKey(),  equalTo("4") ) ; /* as formatted, three lines (1..3) */
		
		ExecutionResult result = m_cxn.execute( makeTaggedRoutineCallCommand( "testtag", "TESTROUTINE" ) );
		
		assertThat( result, equalTo(ExecutionResult.QUIT) );
		assertThat( m_db.at("X").value(), equalTo("456") ) ;
	}
	
	@Test
	public void canInstallAndReturnValueFromRoutine() throws RoutineWriterException, EngineException {
		m_db.install(
			makeSimpleRoutine( "TESTROUTINE",
				makeAssignmentCommand( "X", Scope.GLOBAL, Constant.from(123) ),
				new Command( CommandType.QUIT, new ExpressionList(new DirectVariableReference(Scope.GLOBAL, "X")) )
				)
			);
		
		ExecutionResult result = m_cxn.execute( makeTaggedRoutineCallCommand( null, "TESTROUTINE" ) );
		
		assertThat( result, equalTo(ExecutionResult.QUIT) );
		assertThat( m_cxn.result(), equalTo( Constant.from(123) ) ) ;
	}
	
}
