package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

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
	
	@Before
	public void configureDatabase() {
		m_db = new Database() ;
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void cannotRetrieveAbsentKey() {
		m_db.at( "missing key" ) ;
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void cannotAddKeyTwice() {
		m_db.create( "present key" ) ;
		m_db.create( "present key" ) ;
	}

	@Test(expected=IllegalArgumentException.class)
	public void cannotDropAbsentKey() {
		m_db.drop( "missing key" ) ;
	}
	
	@Test
	public void canDropKey() {
		m_db.create( "present key" ) ;
		m_db.drop( "present key" ) ;
		assertThat( m_db.isEmpty(), equalTo(true) ) ;
	}
	
	@Test
	public void canCreateAndRetrieveKey() {
		Node node = m_db.create( "present key" ) ;
		assertThat( m_db.at("present key"), equalTo(node) ) ;
	}
	
	@Test
	public void canCreateTree() {
		Node node = m_db.create( "present key" ) ;
		node.create("a").assign("value") ;
		node.create("b").assign("value") ;
		assertThat( m_db.at("present key"), equalTo(node) ) ;
		assertThat( m_db.at("present key").at("a"), equalTo( node.at("a") ) ) ;
		assertThat( m_db.at("present key").at("b"), equalTo( node.at("b") ) ) ;
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
		ExecutionResult result = m_db.execute( makeAssignmentCommand( "X", Scope.GLOBAL, Constant.from(123) ) );
		
		assertThat( result, equalTo(ExecutionResult.CONTINUE) );
		assertThat( m_db.at("X").value(), equalTo("123") ) ;
	}
	
	@Test
	public void cannotCallMissingRoutine() throws EngineException {
		ExecutionResult result = m_db.execute( makeTaggedRoutineCallCommand( null, "missing routine" ) );
		
		assertThat( result, equalTo(ExecutionResult.ERROR) );
		assertThat( m_db.error(), notNullValue() ) ;
		assertThat( m_db.error().errorCode(), equalTo(ErrorCode.MISSING_ROUTINE) ) ;
	}
	
	@Test
	public void cannotCallMissingTag() throws RoutineWriterException, EngineException {
		m_db.install( makeSimpleRoutine( "TESTROUTINE" ) ) ;
		
		ExecutionResult result = m_db.execute( makeTaggedRoutineCallCommand( "missing tag", "TESTROUTINE" ) );
		
		assertThat( result, equalTo(ExecutionResult.ERROR) );
		assertThat( m_db.error(), notNullValue() ) ;
		assertThat( m_db.error().errorCode(), equalTo(ErrorCode.MISSING_TAG) ) ;
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
		
		ExecutionResult result = m_db.execute( makeTaggedRoutineCallCommand( null, "TESTROUTINE" ) );
		
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
		
		ExecutionResult result = m_db.execute( makeTaggedRoutineCallCommand( "testtag", "TESTROUTINE" ) );
		
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
		
		ExecutionResult result = m_db.execute( makeTaggedRoutineCallCommand( null, "TESTROUTINE" ) );
		
		assertThat( result, equalTo(ExecutionResult.QUIT) );
		assertThat( m_db.result(), equalTo( Constant.from(123) ) ) ;
	}
	
}
