package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.Executor.ExecutionResult;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Assignment;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Destination;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;

public class PersistenceTest {

	private PersistentStorage m_persistentStore ;
	
	private Database m_db1 ;
	private Connection m_cxn1 ;
	private LineBufferInputOutputDevice m_io1 ;
	
	private Database m_db2 ;
	private Connection m_cxn2 ;
	private LineBufferInputOutputDevice m_io2 ;
	
	@Before
	public void openDatabaseConnection() {
		m_persistentStore = new InMemoryPersistentStorage() ;
		
		m_db1 = new Database( m_persistentStore ) ;
		m_io1 = new LineBufferInputOutputDevice() ;
		m_cxn1 = m_db1.openConnection(m_io1) ;
		
		m_db2 = new Database( m_persistentStore ) ;
		m_io2 = new LineBufferInputOutputDevice() ;
		m_cxn2 = m_db2.openConnection(m_io2) ;
	}

	@After
	public void closeDatabaseConnection() {
		m_cxn1.close() ;
		m_cxn2.close() ;
	}

	@Test
	public void canSetVariable() throws EngineException {
		ExecutionResult result = m_cxn1.execute( new Command( CommandType.SET, new AssignmentList(
				new Assignment(
						Destination.wrap( new DirectVariableReference( Scope.PERSISTENT, "X" ) ),
						Constant.from( "123" )
				)
		) ) ) ;
		
		assertThat( result, equalTo(ExecutionResult.CONTINUE) );
		assertThat( m_persistentStore.at("X").value(), equalTo("123") ) ;
		assertThat( m_cxn1.globalContext().root(Scope.PERSISTENT).at("X").value(), equalTo("123") ) ;
		assertThat( m_cxn2.globalContext().root(Scope.PERSISTENT).at("X").value(), equalTo("123") ) ;
	}
	
	@Test
	public void canMergeVariable() throws EngineException {
		Node node = m_db1.get("y") ;
		node.assign("Hello, World!") ;
		node.get("a").assign("1") ;
		node.get("b").assign("2") ;
		ExecutionResult result = m_cxn1.execute( new Command( CommandType.MERGE, new AssignmentList(
				new Assignment(
						Destination.wrap( new DirectVariableReference( Scope.PERSISTENT, "X" ) ),
						new DirectVariableReference( Scope.TRANSIENT, "y" )
						)
				) ) ) ;
		
		assertThat( result, equalTo(ExecutionResult.CONTINUE) );
		assertThat( m_persistentStore.at("X"), equalTo(node) ) ;
		assertThat( m_cxn1.globalContext().root(Scope.PERSISTENT).at("X"), equalTo(node) ) ;
		assertThat( m_cxn2.globalContext().root(Scope.PERSISTENT).at("X"), equalTo(node) ) ;
	}
	
}
