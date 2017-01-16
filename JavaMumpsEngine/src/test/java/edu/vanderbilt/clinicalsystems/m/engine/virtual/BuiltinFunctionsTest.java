package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import static edu.vanderbilt.clinicalsystems.m.lang.OperatorType.ADD;
import static edu.vanderbilt.clinicalsystems.m.lang.OperatorType.CONCAT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.ErrorCode;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.Executor.ExecutionResult;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BinaryOperation;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;

public class BuiltinFunctionsTest {

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

	private Command makeOperatorCommand( Expression lhs, OperatorType operator, Expression rhs ) {
		BinaryOperation operation = new BinaryOperation(lhs, operator, rhs) ;
		Command command = new Command( CommandType.QUIT, new ExpressionList(operation) ) ;
		return command ;
	}
		
	@Test
	public void canApplyAdditionOperator() throws EngineException {
		canApplyOperator( Constant.from("7"), Constant.from("13"), ADD, Constant.from("-6") ) ;
		canApplyOperator( ErrorCode.NOT_A_NUMBER, Constant.from("13"), ADD, Constant.from("abc") ) ;
	}
	
	@Test
	public void canApplyConcatentaionOperator() throws EngineException {
		canApplyOperator( Constant.from("13-6"), Constant.from("13"), CONCAT, Constant.from("-6") ) ;
	}
	
	private void canApplyOperator( ErrorCode expected, Expression lhs, OperatorType operator, Expression rhs ) {
		ExecutionResult result = m_cxn.execute( makeOperatorCommand( lhs, operator, rhs ) );
		
		assertThat( result, equalTo(ExecutionResult.ERROR) );
		assertThat( m_cxn.error().errorCode(), equalTo(expected) ) ;
	}
	
	private void canApplyOperator( Constant expected,  Expression lhs, OperatorType operator, Expression rhs ) {
		ExecutionResult result = m_cxn.clear().execute( makeOperatorCommand( lhs, operator, rhs ) );
		
		assertThat( result, equalTo(ExecutionResult.QUIT) );
		assertThat( m_cxn.result(), equalTo(EvaluationResult.fromConstant(expected)) ) ;
	}
	
	
}
