package edu.vanderbilt.clinicalsystems.m.lang.text.repr;

import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.BOOLEAN;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.DECIMAL;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.INTEGER;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.STRING;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.VOID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.text.repr.ClassSymbol;
import edu.vanderbilt.clinicalsystems.m.text.repr.ConstantValue;
import edu.vanderbilt.clinicalsystems.m.text.repr.MethodSymbol;
import edu.vanderbilt.clinicalsystems.m.text.repr.OperationNode;
import edu.vanderbilt.clinicalsystems.m.text.repr.RepresentationInferenceKie;
import edu.vanderbilt.clinicalsystems.m.text.repr.RepresentationNode;
import edu.vanderbilt.clinicalsystems.m.text.repr.SymbolScope;
import edu.vanderbilt.clinicalsystems.m.text.repr.VariableSymbol;

public class RepresentationInferenceKieTest {

	private RepresentationInferenceKie m_inference ;
	private ConstantValue m_constantBoolean ;
	private ConstantValue m_constantNullInteger ;
	private ConstantValue m_constantInteger ;
	private ConstantValue m_constantDecimal ;
	private ConstantValue m_constantEmptyString ;
	private ConstantValue m_constantString ;
	private SymbolScope m_rootScope ;
	private VariableSymbol m_variableX ;
	private ClassSymbol m_classC;
	private SymbolScope m_classScope;
	private VariableSymbol m_variableM ;
	private MethodSymbol m_methodF ;
	private MethodSymbol m_methodG ;
	private SymbolScope m_methodScope;
	private VariableSymbol m_variableV ;
	
	@Before
	public void configureInference() {
		m_inference = new RepresentationInferenceKie();
		
		m_constantBoolean = m_inference.createConstantValue( Boolean.TRUE, BOOLEAN );
		m_constantNullInteger = m_inference.createConstantValue( null, INTEGER );
		m_constantInteger = m_inference.createConstantValue( Integer.valueOf(37), INTEGER );
		m_constantDecimal = m_inference.createConstantValue( Double.valueOf(3.14), DECIMAL );
		m_constantEmptyString  = m_inference.createConstantValue( null, STRING );
		m_constantString  = m_inference.createConstantValue( String.valueOf("abc123"), STRING );

		m_rootScope = m_inference.createScope() ;
		m_variableX = m_inference.createVariableSymbol( m_rootScope, "x" ) ;
		
		m_classC = m_inference.createClassSymbol(m_rootScope,"c") ;
		m_classScope = m_classC.getDeclarationScope() ;
		m_variableM = m_inference.createVariableSymbol( m_classScope, "m" ) ;
		
		m_methodF = m_inference.createMethodSymbol( m_classScope, "f" ) ;
		m_methodScope = m_methodF.getBodyScope();
		
		m_methodG = m_inference.createMethodSymbol( m_classScope, "g" ) ;
		m_methodG.createParameter(0, "p0" ) ;
		m_methodG.createParameter(1, "p1" ) ;
		m_methodG.createParameter(2, "p2" ) ;
		
		m_variableV = m_inference.createVariableSymbol( m_methodScope, "v" ) ;
		

	}

	@After
	public void concludeInference() throws Exception {
		m_inference.close() ;
	}

	@Test
	public void canFindVariableSymbol() {
		assertThat( m_inference.variableSymbolFor(m_rootScope, m_variableX.getName() ), equalTo( Optional.of(m_variableX) ) ) ;
	}
	
	@Test
	public void canFindMethodSymbolWithNoParametersRegardlessIgnoringParameterCount() {
		assertThat( m_inference.methodSymbolFor(m_classScope, m_methodF.getName(), -1 ), equalTo( Optional.of(m_methodF) ) ) ;
	}
	
	@Test
	public void canFindMethodSymbolWithNoParameters() {
		assertThat( m_inference.methodSymbolFor(m_classScope, m_methodF.getName(), 0 ), equalTo( Optional.of(m_methodF) ) ) ;
	}
	
	@Test
	public void canFindMethodSymbolWithSomeParametersRegardlessIgnoringParameterCount() {
		assertThat( m_inference.methodSymbolFor(m_classScope, m_methodG.getName(), -1 ), equalTo( Optional.of(m_methodG) ) ) ;
	}
	
	@Test
	public void canFindMethodSymbolWithSomeParameters() {
		assertThat( m_inference.methodSymbolFor(m_classScope, m_methodG.getName(), 3 ), equalTo( Optional.of(m_methodG) ) ) ;
	}
	
	@Test
	public void canFindEnclosingMethodSymbol() {
		assertThat( m_inference.enclosingMethod(m_methodScope), equalTo( m_methodF ) ) ;
	}
	
	@Test
	public void canFindEnclosingMethodSymbolFromEnclosedScope() {
		SymbolScope enclosedScope = m_inference.createScope( m_methodScope, "enclosed" ) ;
		assertThat( m_inference.enclosingMethod(enclosedScope), equalTo( m_methodF ) ) ;
	}
	
	@Test
	public void canFindEnclosingMethodSymbolFromNestedScope() {
		SymbolScope enclosedScope = m_inference.createScope( m_methodScope, "enclosed" ) ;
		SymbolScope nestedScope = m_inference.createScope( enclosedScope, "nested" ) ;
		assertThat( m_inference.enclosingMethod(nestedScope), equalTo( m_methodF ) ) ;
	}
	
	@Test
	public void canFindEnclosingClassSymbol() {
		assertThat( m_inference.enclosingClass(m_classScope), equalTo( m_classC ) ) ;
	}
	
	@Test
	public void canFindEnclosingClassSymbolFromEnclosedScope() {
		assertThat( m_inference.enclosingClass(m_methodScope), equalTo( m_classC ) ) ;
	}
	
	@Test
	public void canFindEnclosingClassSymbolFromNestedScope() {
		SymbolScope enclosedScope = m_inference.createScope( m_methodScope, "enclosed" ) ;
		assertThat( m_inference.enclosingClass(enclosedScope), equalTo( m_classC ) ) ;
	}
	
	@Test
	public void canInferRepresentationOfConstant() {
		assertThat( m_inference.representationFor( m_constantBoolean ), equalTo( BOOLEAN ) ) ;
	}
	
	@Test
	public void canListNoVariableSymbols() {
		SymbolScope emptyScope = m_inference.createScope( m_rootScope, "<empty>" ) ;
		assertThat( m_inference.allSymbols( emptyScope ).size(), equalTo( 0 ) ) ;
	}
	
	@Test
	public void canListSomeVariableSymbols() {
		SymbolScope scope = m_inference.createScope( m_rootScope, "<not-empty>" ) ;
		m_inference.createVariableSymbol( scope, "x" ) ;
		m_inference.createVariableSymbol( scope, "y" ) ;
		m_inference.createVariableSymbol( scope, "z" ) ;
		assertThat( m_inference.allSymbols( scope ).size(), equalTo( 3 ) ) ;
	}
	
	@Test
	public void canInferVariableRepresentationFromNoAssignment() {
		assertThat( m_inference.representationFor( m_variableX ), equalTo( VOID ) ) ;
	}
	
	@Test
	public void canInferMethodRepresentationFromNoReturns() {
		assertThat( m_inference.representationFor( m_methodF ), equalTo( VOID ) ) ;
	}
	
	private org.hamcrest.Matcher<Object> factOfType( String name ) { return instanceOf( m_inference.factClass(name) ) ; }
	
	@Test
	public void canInferVariableRepresentationFromBooleanAssignment() {
		m_inference.isAssignedFrom( m_variableX, m_constantBoolean ) ;
		assertThat( m_inference.representationFor( m_variableX ), equalTo( BOOLEAN ) ) ;
		assertThat( m_inference.rulesOn( m_variableX ), hasItem( factOfType("VariableAssignment") ) ) ;
	}
	
	@Test
	public void canInferVariableRepresentationFromIntegerAssignment() {
		m_inference.isAssignedFrom( m_variableX, m_constantInteger ) ;
		assertThat( m_inference.representationFor( m_variableX ), equalTo( INTEGER ) ) ;
		
	}
	
	@Test
	public void canInferVariableRepresentationFromNullAssignment() {
		m_inference.isAssignedFrom( m_variableX, m_constantNullInteger ) ;
		assertThat( m_inference.representationFor( m_variableX ), equalTo( VOID ) ) ;
		
	}
	
	@Test
	public void canInferVariableRepresentationFromEmptyStringAssignment() {
		m_inference.isAssignedFrom( m_variableX, m_constantEmptyString ) ;
		assertThat( m_inference.representationFor( m_variableX ), equalTo( VOID ) ) ;
		
	}
	
	@Test
	public void canInferMethodRepresentationFromReturningBoolean() {
		m_inference.returns( m_methodF, m_constantBoolean ) ;
		assertThat( m_inference.representationFor( m_methodF ), equalTo( BOOLEAN ) ) ;
	}
	
	@Test
	public void canInferVariableRepresentationFromDecimalAssignment() {
		m_inference.isAssignedFrom( m_variableX, m_constantDecimal ) ;
		assertThat( m_inference.representationFor( m_variableX ), equalTo( DECIMAL ) ) ;
		
	}
	
	@Test
	public void canInferVariableRepresentationFromStringAssignment() {
		m_inference.isAssignedFrom( m_variableX, m_constantString ) ;
		assertThat( m_inference.representationFor( m_variableX ), equalTo( STRING ) ) ;
	}
	
	@Test
	public void canInferVariableRepresentationFromIntegerAndStringAssignment() {
		m_inference.isAssignedFrom( m_variableX, m_constantInteger ) ;
		m_inference.isAssignedFrom( m_variableX, m_constantString ) ;
		assertThat( m_inference.representationFor( m_variableX ), equalTo( STRING ) ) ;
		
	}
	
	@Test
	public void canInferVariableRepresentationFromIntegerAndEmptyStringAssignment() {
		m_inference.isAssignedFrom( m_variableX, m_constantInteger ) ;
		m_inference.isAssignedFrom( m_variableX, m_constantEmptyString ) ;
		assertThat( m_inference.representationFor( m_variableX ), equalTo( INTEGER ) ) ;
		
	}
	
	@Test
	public void canInferVariableRepresentationFromBooleanAndIntegerAssignment() {
		m_inference.print(System.out);
		
		m_inference.isAssignedFrom( m_variableX, m_constantInteger ) ;
		m_inference.isAssignedFrom( m_variableX, m_constantBoolean ) ;
		assertThat( m_inference.representationFor( m_variableX ), equalTo( INTEGER ) ) ;
	}
	
	@Test
	public void canInferVariableRepresentationFromBooleanAndNullIntegerAssignment() {
		m_inference.isAssignedFrom( m_variableX, m_constantNullInteger ) ;
		m_inference.isAssignedFrom( m_variableX, m_constantBoolean ) ;
		assertThat( m_inference.representationFor( m_variableX ), equalTo( BOOLEAN ) ) ;
		
	}
	
	@Test
	public void canMethodReturnFromScopeReturn() {
		m_inference.returns( m_methodF.getBodyScope(), m_constantInteger );
		assertThat( m_inference.representationFor( m_methodF ), equalTo( INTEGER ) ) ;
	}
	
	@Test
	public void canMethodReturnFromEnclosedScopeReturn() {
		SymbolScope enclosedScope = m_inference.createScope( m_methodScope, "enclosed" ) ;
		m_inference.returns( enclosedScope, m_constantInteger );
		assertThat( m_inference.representationFor( m_methodF ), equalTo( INTEGER ) ) ;
	}
	
	@Test
	public void canMethodReturnFromNestedScopeReturn() {
		SymbolScope enclosedScope = m_inference.createScope( m_methodScope, "enclosed" ) ;
		SymbolScope nestedScope = m_inference.createScope( enclosedScope, "nested" ) ;
		m_inference.returns( nestedScope, m_constantInteger );
		assertThat( m_inference.representationFor( m_methodF ), equalTo( INTEGER ) ) ;
	}
	
	private void printNode( RepresentationNode node ) {
		System.out.println( node.toString() ) ;
		for ( Object rule : m_inference.rulesOn(node) )
			System.out.println( "  " + m_inference.describeRule( rule, node ) );
		System.out.println() ;
	}
	
	@Test
	public void canCombineStringWithInteger() {
		OperationNode node = m_constantString.combinesWith( m_constantInteger ) ;
		m_variableX.isAssigned( node ) ;
		m_inference.print(System.out);
		assertThat( m_inference.representationFor( node ), equalTo( STRING ) ) ;
		assertThat( m_inference.representationFor( m_variableX ), equalTo( STRING ) ) ;
	}
	
	@Test
	public void canCombineIntegerWithString() {
		OperationNode node = m_constantInteger.combinesWith( m_constantString ) ;
		m_variableX.isAssigned( node ) ;
		assertThat( m_inference.representationFor( node ), equalTo( STRING ) ) ;
		assertThat( m_inference.representationFor( m_variableX ), equalTo( STRING ) ) ;
	}
	
}
