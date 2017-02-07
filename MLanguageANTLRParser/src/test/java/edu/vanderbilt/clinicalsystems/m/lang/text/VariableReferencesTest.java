package edu.vanderbilt.clinicalsystems.m.lang.text;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.lang.BuiltinSystemVariable;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinVariable;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinSystemVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;

public class VariableReferencesTest {

	@Test
	public void canParseDirectLocalTransientVariable() {
		Expression expr = new RoutineANTLRParser().parseExpression("x") ;
		assertThat( expr, equalTo( new DirectVariableReference(Scope.TRANSIENT, "x") ) ) ;
	}
	
	@Test
	public void canParseDirectLocalPersistentVariable() {
		Expression expr = new RoutineANTLRParser().parseExpression("^x") ;
		assertThat( expr, equalTo( new DirectVariableReference(Scope.PERSISTENT, "x") ) ) ;
	}
	
	@Test
	public void canParseBuiltinVariable() {
		Expression expr = new RoutineANTLRParser().parseExpression("$H") ;
		assertThat( expr, equalTo( new BuiltinVariableReference(BuiltinVariable.HOROLOG) ) ) ;
	}
	
	@Test
	public void canParseBuiltinSystemVariable() {
		Expression expr = new RoutineANTLRParser().parseExpression("^$R") ;
		assertThat( expr, equalTo( new BuiltinSystemVariableReference(BuiltinSystemVariable.ROUTINE) ) ) ;
	}
	
}
