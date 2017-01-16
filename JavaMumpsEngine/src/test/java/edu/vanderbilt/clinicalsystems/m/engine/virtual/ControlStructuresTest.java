package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.Executor.ExecutionResult;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.InlineBlock;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.FormatCommand;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutput;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutputList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.LoopDefinition;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;

public class ControlStructuresTest {

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
	
	private Command makeForLoopCommand( Expression start, Expression step, Expression stop ) {
		DirectVariableReference loopVar = new DirectVariableReference(Scope.LOCAL, "i") ;
		LoopDefinition loopDefinition = new LoopDefinition(loopVar, start, step, stop) ;
		Command command = new Command( CommandType.FOR, loopDefinition, new InlineBlock( new Command(CommandType.WRITE,new InputOutputList(InputOutput.wrap(loopVar),FormatCommand.carriageReturn())) ) ) ;
		return command ;
	}

	@Test
	public void canEvaluateForLoop() throws EngineException {
		canEvaluateForLoop( Arrays.asList("1"), Constant.from(1), null, null ) ;
		canEvaluateForLoop( Arrays.asList("1","2","3"), 1, 1, 3 ) ;
		canEvaluateForLoop( Arrays.asList(), 3, 1, 1 ) ;
		canEvaluateForLoop( Arrays.asList("3","2","1"), 3, -1, 1 ) ;
	}
	
	private void canEvaluateForLoop( List<String> expected, int start, int step, int stop ) {
		canEvaluateForLoop( expected, Constant.from(start), Constant.from(step), Constant.from(stop) ) ;
	}
	
	private void canEvaluateForLoop( List<String> expected, Expression start, Expression step, Expression stop ) {
		ExecutionResult result = m_cxn.execute( makeForLoopCommand( start, step, stop ) );
		
		assertThat( result, equalTo(ExecutionResult.CONTINUE) );
		for ( String s : expected )
			assertThat( m_io.take(), equalTo(s) ) ;
		assertThat( m_io.take(), nullValue() ) ;
	}
	
	
}
