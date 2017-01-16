package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import static edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction.LENGTH;
import static edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction.PIECE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.ErrorCode;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.Executor.ExecutionResult;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;

public class OperatorsTest {

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

	private Command makeBuiltinFunctionCallCommand( BuiltinFunction builtinFunction, Expression ... parameters ) {
		BuiltinFunctionCall builtinFunctionCall = new BuiltinFunctionCall(builtinFunction,Expression.list(parameters)) ;
		Command command = new Command( CommandType.QUIT, new ExpressionList(builtinFunctionCall) ) ;
		return command ;
	}
		
	@Test
	public void canEvaluateLengthFunction() throws EngineException {
		canCallBuiltinFunction( Constant.from("0"),LENGTH, Constant.from(""         ) ) ;
		canCallBuiltinFunction( Constant.from("3"),LENGTH, Constant.from("ABC"      ) ) ;
		canCallBuiltinFunction( Constant.from("0"),LENGTH, Constant.from(""         ),Constant.from(";") ) ;
		canCallBuiltinFunction( Constant.from("2"),LENGTH, Constant.from(";"        ),Constant.from(";") ) ;
		canCallBuiltinFunction( Constant.from("1"),LENGTH, Constant.from("ABC"      ),Constant.from(";") ) ;
		canCallBuiltinFunction( Constant.from("2"),LENGTH, Constant.from(";ABC"     ),Constant.from(";") ) ;
		canCallBuiltinFunction( Constant.from("3"),LENGTH, Constant.from("A;B;C"    ),Constant.from(";") ) ;
		canCallBuiltinFunction( Constant.from("0"),LENGTH, Constant.from(""         ),Constant.from(":::") ) ;
		canCallBuiltinFunction( Constant.from("2"),LENGTH, Constant.from(":::"      ),Constant.from(":::") ) ;
		canCallBuiltinFunction( Constant.from("1"),LENGTH, Constant.from("ABC"      ),Constant.from(":::") ) ;
		canCallBuiltinFunction( Constant.from("3"),LENGTH, Constant.from("A:::B:::C"),Constant.from(":::") ) ;
	}

	@Test
	public void canEvaluatePieceFunction() throws EngineException {
		canCallBuiltinFunction( Constant.from(""   ),PIECE, Constant.from("ABC::DEF::GHI"), Constant.from("::"), Constant.from(0) ) ;
		canCallBuiltinFunction( Constant.from("ABC"),PIECE, Constant.from("ABC::DEF::GHI"), Constant.from("::"), Constant.from(1) ) ;
		canCallBuiltinFunction( Constant.from("DEF"),PIECE, Constant.from("ABC::DEF::GHI"), Constant.from("::"), Constant.from(2) ) ;
		canCallBuiltinFunction( Constant.from("GHI"),PIECE, Constant.from("ABC::DEF::GHI"), Constant.from("::"), Constant.from(3) ) ;
		canCallBuiltinFunction( Constant.from(""   ),PIECE, Constant.from("ABC::DEF::GHI"), Constant.from("::"), Constant.from(4) ) ;

		canCallBuiltinFunction( ErrorCode.ILLEGAL_ARGUMENT, PIECE, Constant.from("ABC::DEF::GHI"), Constant.from(""), Constant.from(1), Constant.from(2) ) ;
		
		canCallBuiltinFunction( Constant.from(""        ),PIECE, Constant.from("ABC::DEF::GHI"), Constant.from("::"), Constant.from(-1), Constant.from(0) ) ;
		canCallBuiltinFunction( Constant.from("ABC"     ),PIECE, Constant.from("ABC::DEF::GHI"), Constant.from("::"), Constant.from(0), Constant.from(1) ) ;
		canCallBuiltinFunction( Constant.from("ABC"     ),PIECE, Constant.from("ABC::DEF::GHI"), Constant.from("::"), Constant.from(1), Constant.from(1) ) ;
		canCallBuiltinFunction( Constant.from("ABC::DEF"),PIECE, Constant.from("ABC::DEF::GHI"), Constant.from("::"), Constant.from(1), Constant.from(2) ) ;
		canCallBuiltinFunction( Constant.from("DEF::GHI"),PIECE, Constant.from("ABC::DEF::GHI"), Constant.from("::"), Constant.from(2), Constant.from(3) ) ;
		canCallBuiltinFunction( Constant.from("GHI"     ),PIECE, Constant.from("ABC::DEF::GHI"), Constant.from("::"), Constant.from(3), Constant.from(4) ) ;
		canCallBuiltinFunction( Constant.from(""        ),PIECE, Constant.from("ABC::DEF::GHI"), Constant.from("::"), Constant.from(4), Constant.from(5) ) ;
		
		canCallBuiltinFunction( Constant.from("ABC::DEF::GHI"),PIECE, Constant.from("ABC::DEF::GHI"), Constant.from("::"), Constant.from(-1), Constant.from(5) ) ;
		canCallBuiltinFunction( Constant.from("ABC::DEF::GHI"),PIECE, Constant.from("ABC::DEF::GHI"), Constant.from("::"), Constant.from(0), Constant.from(4) ) ;
		canCallBuiltinFunction( Constant.from("ABC::DEF::GHI"),PIECE, Constant.from("ABC::DEF::GHI"), Constant.from("::"), Constant.from(1), Constant.from(3) ) ;
	}
	
	private void canCallBuiltinFunction( ErrorCode expected, BuiltinFunction builtinFunction, Expression ... parameters ) {
		ExecutionResult result = m_cxn.execute( makeBuiltinFunctionCallCommand( builtinFunction, parameters ) );
		
		assertThat( result, equalTo(ExecutionResult.ERROR) );
		assertThat( m_cxn.error().errorCode(), equalTo(expected) ) ;
	}
	
	private void canCallBuiltinFunction( Constant expected, BuiltinFunction builtinFunction, Expression ... parameters ) {
		ExecutionResult result = m_cxn.clear().execute( makeBuiltinFunctionCallCommand( builtinFunction, parameters ) );
		
		assertThat( result, equalTo(ExecutionResult.QUIT) );
		assertThat( m_cxn.result(), equalTo(EvaluationResult.fromConstant(expected)) ) ;
	}
	
	
}
