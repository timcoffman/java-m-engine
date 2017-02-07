package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.Executor.ExecutionResult;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BinaryOperation;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.IndirectVariableReference;

public class PatternMatchTest {

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

	@Test
	public void canEvaluatePatternMatchingExactlyOneAtom() throws EngineException {
		assertThatPattern( "1A" )
			.matches( "a" )
			.doesNotMatch( "" )
			.doesNotMatch( "abc" ).or( "aa" )
			.doesNotMatch( "1" ).or( "123" )
			.doesNotMatch( "." )
			;
	}

	private PatternMatchAsserter assertThatPattern( String pattern ) {
		return new PatternMatchAsserter( pattern ) ;
	}
	
	private class PatternMatchAsserter {
		private final String m_pattern ;
		private boolean m_matchingState ;
		public PatternMatchAsserter( String pattern ) { m_pattern = pattern ; }
		public PatternMatchAsserter matches( String text ) { assertThatPatternMatchResultsIn( m_pattern, text, m_matchingState=true ) ; return this ; }
		public PatternMatchAsserter doesNotMatch( String text ) { assertThatPatternMatchResultsIn( m_pattern, text, m_matchingState=false ) ; return this ; }
		public PatternMatchAsserter and( String text ) { assertThatPatternMatchResultsIn( m_pattern, text, m_matchingState ) ; return this ; }
		public PatternMatchAsserter or( String text ) { assertThatPatternMatchResultsIn( m_pattern, text, m_matchingState ) ; return this ; }
	}
	
	private void assertThatPatternMatchResultsIn( String pattern, String text, boolean matches ) {
		m_cxn.createLocalNode("p").assign(pattern);
		m_cxn.createLocalNode("x").assign(text);
		ExecutionResult result = m_cxn.clear().execute( new Command( CommandType.QUIT, new ExpressionList(
				new BinaryOperation(
						new DirectVariableReference(Scope.TRANSIENT, "x"),
						OperatorType.MATCH,
						new IndirectVariableReference( new DirectVariableReference(Scope.TRANSIENT, "p") )
					)
			)));
		assertThat( m_cxn.error(), nullValue() ) ;
		assertThat( result, equalTo( ExecutionResult.QUIT ) ) ;
		assertThat( m_cxn.result(), equalTo( EvaluationResult.fromConstant( Constant.from(matches) )) ) ;
	}
	
}
