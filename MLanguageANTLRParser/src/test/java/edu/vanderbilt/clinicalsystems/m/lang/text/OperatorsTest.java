package edu.vanderbilt.clinicalsystems.m.lang.text;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction;
import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BinaryOperation;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.IndirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.UnaryOperation;

public class OperatorsTest {

	private static final DirectVariableReference VAR_X = new DirectVariableReference(Scope.TRANSIENT, "x");
	private static final Constant LITERAL_ABC = Constant.from("abc");
	private static final Constant LITERAL_123 = Constant.from("123");
	
	@Test
	public void canParseConcatenationOperator() {
		Expression expr = new RoutineANTLRParser().parseExpression("x_\"abc\"" ) ;
		assertThat( expr, equalTo( new BinaryOperation( VAR_X, OperatorType.CONCAT, LITERAL_ABC ) ) ) ;
	}
	
	@Test
	public void canParseAdditionOperator() {
		Expression expr = new RoutineANTLRParser().parseExpression("x+123" ) ;
		assertThat( expr, equalTo( new BinaryOperation( VAR_X, OperatorType.ADD, LITERAL_123 ) ) ) ;
	}
	
	@Test
	public void canParseIndirectionOperatorOnLiteral() {
		Expression expr = new RoutineANTLRParser().parseExpression("@\"abc\"" ) ;
		assertThat( expr, equalTo( new UnaryOperation( OperatorType.INDIRECTION, LITERAL_ABC ) ) ) ;
//		assertThat( expr, equalTo( new IndirectVariableReference( LITERAL_ABC ) ) ) ;
	}
	
	@Test
	public void canParseIndirectionOperatorOnBuiltinFunction() {
		Expression expr = new RoutineANTLRParser().parseExpression("@$PIECE(\"x\",1)" ) ;
		BuiltinFunctionCall builtinFunctionCall = new BuiltinFunctionCall( BuiltinFunction.PIECE, Arrays.asList( Constant.from("x"), Constant.from(1) ) );
		assertThat( expr, equalTo( new UnaryOperation( OperatorType.INDIRECTION, builtinFunctionCall ) ) ) ;
//		assertThat( expr, equalTo( new IndirectVariableReference( builtinFunctionCall ) ) ) ;
	}
	
	@Test
	public void canParseIndirectionOperatorOnVariable() {
		Expression expr = new RoutineANTLRParser().parseExpression("@x" ) ;
		assertThat( expr, equalTo( new UnaryOperation( OperatorType.INDIRECTION, VAR_X ) ) ) ;
//		assertThat( expr, equalTo( new IndirectVariableReference( builtinFunctionCall ) ) ) ;
	}
	
	@Test
	public void canParseIndirectionOperatorOnVariableWithKeys() {
		Expression expr = new RoutineANTLRParser().parseExpression("@x(\"abc\")" ) ;
		assertThat( expr, equalTo( new UnaryOperation( OperatorType.INDIRECTION, VAR_X.child(LITERAL_ABC) ) ) ) ;
	}
	
	@Test
	public void canParseKeysWithIndirectionOperatorOnVariable() {
		Expression expr = new RoutineANTLRParser().parseExpression("@x@(\"abc\")" ) ;
		assertThat( expr, equalTo( new IndirectVariableReference( VAR_X, Arrays.asList(LITERAL_ABC) ) ) ) ;
	}
	
	@Test
	public void canParseKeysWithIndirectionOperatorOnVariableWithKeys() {
		Expression expr = new RoutineANTLRParser().parseExpression("@x(\"abc\")@(\"abc\")" ) ;
		assertThat( expr, equalTo( new IndirectVariableReference( VAR_X.child(LITERAL_ABC), Arrays.asList(LITERAL_ABC) ) ) ) ;
	}
	
	
}
