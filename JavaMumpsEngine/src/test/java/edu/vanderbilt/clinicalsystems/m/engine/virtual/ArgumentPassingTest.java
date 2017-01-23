package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.ParameterPassMethod;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.ParameterName;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.Tag;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Assignment;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Destination;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCallList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.TagReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class ArgumentPassingTest {

	private Database m_db ;
	private Connection m_cxn ;
	private LineBufferInputOutputDevice m_io ;
	
	@Before
	public void openDatabaseConnection() throws RoutineWriterException {
		m_db = new Database() ;
		m_io = new LineBufferInputOutputDevice() ;
		m_cxn = m_db.openConnection(m_io) ;

		Routine routine = new Routine() ;
		routine.appendElement( new Tag("MYROUTINE") );
		routine.appendElement( new Command(CommandType.QUIT,Argument.NOTHING) );
		routine.appendElement( new Tag("ADDKEY", Arrays.asList( new ParameterName("passedByReference") )) );
		routine.appendElement( new Command(CommandType.SET, new AssignmentList(
				new Assignment(
						Destination.wrap( new DirectVariableReference(Scope.TRANSIENT,"passedByReference", Arrays.asList( Constant.from("KEY") ) ) ),
						Constant.from("VALUE")
				)
		)) );
		routine.appendElement( new Command(CommandType.QUIT,Argument.NOTHING) );
		m_db.install( routine );
	}

	@After
	public void closeDatabaseConnection() {
		m_cxn.close() ;
	}

	@Test
	public void canCreateVariablePassedByReference() throws EngineException, RoutineWriterException {
		
		Node variable = m_cxn.createLocalNode( "VAR" ) ;
		DirectVariableReference variableRef = new DirectVariableReference( ParameterPassMethod.BY_REFERENCE, Scope.TRANSIENT, "VAR") ;
		TaggedRoutineCall taggedRoutineCall = new TaggedRoutineCall( new TagReference("ADDKEY", "MYROUTINE"), Arrays.asList(variableRef) ) ;
		m_cxn.execute( new Command( CommandType.DO, new TaggedRoutineCallList( taggedRoutineCall ) ) ) ;
		
		assertThat( variable.at("KEY").value(), equalTo("VALUE") ) ;
	}
	
	@Test
	public void canModifyVariablePassedByReference() throws EngineException, RoutineWriterException {
		
		Node variable = m_cxn.createLocalNode( "VAR" ) ;
		variable.create("KEY").assign("OLDVALUE") ;
		assertThat( variable.at("KEY").value(), equalTo("OLDVALUE") ) ;
		
		DirectVariableReference variableRef = new DirectVariableReference( ParameterPassMethod.BY_REFERENCE, Scope.TRANSIENT, "VAR") ;
		TaggedRoutineCall taggedRoutineCall = new TaggedRoutineCall( new TagReference("ADDKEY", "MYROUTINE"), Arrays.asList(variableRef) ) ;
		m_cxn.execute( new Command( CommandType.DO, new TaggedRoutineCallList( taggedRoutineCall ) ) ) ;
		
		assertThat( variable.at("KEY").value(), equalTo("VALUE") ) ;
	}
	
}
