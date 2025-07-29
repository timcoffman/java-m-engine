package edu.vanderbilt.clinicalsystems.m.text.repr;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.definition.type.FactField;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

import edu.vanderbilt.clinicalsystems.m.lang.text.Representation;

public class RepresentationInferenceKie implements RepresentationInference, AutoCloseable {

	private static final String WHITESPACE = "                    ";
	private static String whitespace( int length ) { return WHITESPACE.substring(0,Math.min(WHITESPACE.length(),length)); }
	
	private KieSession m_kieSession;

	public RepresentationInferenceKie() {
		KieServices kieServices = KieServices.Factory.get();
		KieContainer kieContainer = kieServices.getKieClasspathContainer();
		
		KieBase kieBase = kieContainer.getKieBase() ;
		
		m_kieSession = kieBase.newKieSession();
//		m_kieSession.addEventListener(new DebugAgendaEventListener());
	}
	
	@Override
	public void close() throws Exception {
		m_kieSession.dispose();
	}

	@Override
	public void print( PrintStream printStream ) {
		print( rootScope(), printStream ) ;
	}
	
	@Override
	public void print( SymbolScope scope, PrintStream printStream ) {
		print( scope, "", printStream ) ;
		for ( Object obj : m_kieSession.getObjects( new ClassObjectFilter(ConstantValue.class) )) {
			ConstantValue constantValue = (ConstantValue)obj ;
			printStream.println( constantValue ) ;
			for ( Object rule : rulesOn( constantValue ) ) {
				printStream.println( "  " + describeRule( rule, constantValue ) ) ;
			}
		}
	}

	public Class<?> factClass(String name) {
		FactType factType = m_kieSession.getKieBase().getFactType( RepresentationInferenceKie.class.getPackage().getName(), name ) ;
		return factType.getFactClass() ;
	}

	public String describeRule( Object fact, RepresentationNode context ) {
		Class<?> factClass = fact.getClass();
		if ( factClass.getPackage() != RepresentationInferenceKie.class.getPackage() )
			return factClass.getName();
		FactType factType = m_kieSession.getKieBase().getFactType( RepresentationInferenceKie.class.getPackage().getName(), factClass.getSimpleName() ) ;
		StringBuilder sb = new StringBuilder() ;
		sb.append( factType.getSimpleName() ) ;
		sb.append( "(" ) ;
		boolean firstField = true ;
		for ( FactField field : factType.getFields() ) {
			Object value = field.get(fact) ;
			if ( value == this )
				continue ;
			if ( null == value )
				continue ;
			if ( value == context )
				continue ;
			if ( !firstField )
				sb.append(", ") ;
			sb.append( field.getName() ) ;
			sb.append(": ") ;
			sb.append( value.toString() ) ;
			firstField = false ;
		}
		sb.append( ")" ) ;
		return sb.toString() ;
	}
	
	private void print( SymbolScope scope, String indent, PrintStream printStream ) {
		m_kieSession.fireAllRules() ;
		printStream.println( indent + scope.description() + " {" ) ;
		for ( RepresentationNode node : scopeReturns(scope) ) {
			printStream.println( indent + indent + "returns \"" + node.toString() ) ;
		}
		for ( Symbol symbol : scope.allSymbols() ) {
			printStream.println( indent + indent + "\"" + symbol.getName() + "\": " + symbol.getClass().getSimpleName() + " -> " + symbol.representation() ) ;
			String padding = whitespace(symbol.getName().length() + 4) ;
			printStream.println( indent + indent + padding + "at least: " + representsAtLeast(symbol).stream().map(Representation::toString).collect( Collectors.joining(" | ", "(", ")") ) );
			for ( Object rule : rulesOn(symbol) ) {
				printStream.println( indent + indent + padding + describeRule(rule, symbol) ) ;
			}
			if ( symbol instanceof ClassSymbol ) {
				print( ((ClassSymbol)symbol).getDeclarationScope(), indent + "  ", printStream );
			}
			if ( symbol instanceof MethodSymbol ) {
				int position = 0 ;
				MethodParameter methodParameter = parameterOf( (MethodSymbol)symbol, position++ ) ;
				while ( null != methodParameter ) {
					printStream.println( indent + indent + padding + "(" + methodParameter.getPosition() + ") \"" + methodParameter.getName() + "\" -> " + methodParameter.representation() ) ;
					methodParameter = parameterOf( (MethodSymbol)symbol, position++ ) ;
				} 
				for ( RepresentationNode node : methodReturns((MethodSymbol)symbol) ) {
					printStream.println( indent + indent + padding + "returns \"" + node.toString() ) ;
				}
				print( ((MethodSymbol)symbol).getBodyScope(), indent + "  ", printStream );
			}
		}
		printStream.println( indent + "}" ) ;
	}

	
	private FactType getFactType(String factType) {
		return m_kieSession.getKieBase().getFactType(RepresentationInferenceKie.class.getPackage().getName(), factType );
	}
	
	private FactType getFactType(Class<?> interfaceType) {
		return m_kieSession.getKieBase().getFactType( interfaceType.getPackage().getName(), interfaceType.getSimpleName() + "Fact" );
	}
	
	private <T> T insertNewFact( Class<T> interfaceType, Object ... propertyNamesAndValues ) {
		return interfaceType.cast( insertNewFact( getFactType(interfaceType), propertyNamesAndValues ) );
	}
	
	private Object insertNewFact( String factType, Object ... propertyNamesAndValues ) {
		return insertNewFact( getFactType(factType), propertyNamesAndValues ) ;
	}
	
	private static class EqualityFilter implements ObjectFilter {
		private final Object m_checkFact ;
		public EqualityFilter(Object checkFact) { m_checkFact = checkFact ; }
		@Override public boolean accept(Object fact) {
			return fact.equals(m_checkFact) ;
		}
	}
	
	private <T> T insertUnique( T fact ) {
		Collection<? extends Object> equalFacts = m_kieSession.getObjects( new EqualityFilter(fact) ) ;
		Iterator<? extends Object> i = equalFacts.iterator();
		if ( i.hasNext() ) {
			T existingFact = (T)i.next();
			return existingFact;
		} else {
			m_kieSession.insert( fact ) ;
			return fact ;
		}
	}
	
	private Object insertNewFact( FactType factType, Object ... propertyNamesAndValues ) {
		try {
			Object fact = factType.newInstance() ;
			for ( int i = 0 ; i < propertyNamesAndValues.length ; i+=2 ) {
				String propertyName = (String)propertyNamesAndValues[i] ;
				Object propertyValue = propertyNamesAndValues[i+1] ;
				factType.set( fact, propertyName, propertyValue);
			}
			factType.set( fact, "system", this);
			return insertUnique( fact ) ;
		} catch (InstantiationException | IllegalAccessException ex) {
			throw new RuntimeException(ex) ;
		}
	}
	
	private <T> T insertFact( T fact  ) {
		return insertUnique( fact ) ;
	}
	
	@Override
	public SymbolScope createScope() {
		return insertFact( new SymbolScopeFact(  ) ) ;
	}
	
	@Override
	public SymbolScope createScope( SymbolScope enclosingScope, String description ) {
		return insertFact( new SymbolScopeFact( enclosingScope, description ) ) ;
	}
	
	@Override
	public ConstantValue createConstantValue(Object value, Representation representation) {
		return insertFact( new ConstantValueFact( value, representation ) ) ;
	}
	
	@Override
	public ClassSymbol createClassSymbol(SymbolScope scope, String name) {
		return insertFact( new ClassSymbolFact( scope, name, createScope(scope, "class-declaration:" + name) ) ) ;
	}
	
	@Override
	public MethodSymbol createMethodSymbol(SymbolScope scope, String name) {
		return insertFact( new MethodSymbolFact( scope, name, createScope(scope, "method-body:" + name) ) ) ;
	}
	
	@Override
	public VariableSymbol createVariableSymbol(SymbolScope scope, String name) {
		return insertFact( new VariableSymbolFact( scope, name ) ) ;
	}
	
	@Override
	public RepresentationNode createUnknownNode() {
		return insertFact( new UnknownNodeFact() ) ;
	}
	
	@Override
	public MethodParameter createMethodParameter(MethodSymbol method, int i, String name) {
		return insertFact( new MethodParameterFact( method, i, name ) ) ;
	}

	@Override
	public void isDeclaredAs(RepresentationNode node, Representation representation) {
		Objects.requireNonNull(node) ;
		Objects.requireNonNull(representation) ;
		insertNewFact( "RepresentedAs", "node", node, "representation", representation ) ;
	}

	@Override
	public void isUsedAs(RepresentationNode node, Representation representation) {
		Objects.requireNonNull(node) ;
		Objects.requireNonNull(representation) ;
		insertNewFact( "UsedAs", "node", node, "representation", representation ) ;
	}
	
	@Override
	public VariableSymbol isAssignedFrom(VariableSymbol variable, RepresentationNode source) {
		Objects.requireNonNull(variable) ;
		Objects.requireNonNull(source) ;
		insertNewFact( "VariableAssignment", "variable", variable, "source", source ) ;
		return variable ;
	}
	
	@Override
	public OperationNode isComparedWith(RepresentationNode lhs, RepresentationNode rhs) {
		Objects.requireNonNull(lhs) ;
		Objects.requireNonNull(rhs) ;
		return (OperationNode)insertNewFact( "Comparison", "node", lhs, "comparedTo", rhs ) ;
	}
	
	@Override
	public OperationNode areAlternatives(RepresentationNode node, RepresentationNode alternativeNode) {
		Objects.requireNonNull(node) ;
		Objects.requireNonNull(alternativeNode) ;
		return (OperationNode)insertNewFact( "Choice", "node", node, "alternativeTo", alternativeNode ) ;
	}
	
	@Override
	public OperationNode combines(RepresentationNode node, RepresentationNode withNode ) {
		Objects.requireNonNull(node) ;
		Objects.requireNonNull(withNode) ;
		return (OperationNode)insertNewFact( "Combination", "node", node, "withNode", withNode ) ;
	}
	
	@Override
	public OperationNode transforms(RepresentationNode node, Representation producingRepresentation) {
		Objects.requireNonNull(node) ;
		Objects.requireNonNull(producingRepresentation) ;
		return (OperationNode)insertNewFact( "Transformation", "node", node, "representation", producingRepresentation ) ;
	}
	
	@Override
	public void returns(MethodSymbol method, RepresentationNode source) {
		Objects.requireNonNull(method) ;
		Objects.requireNonNull(source) ;
		insertNewFact( "MethodReturn", "method", method, "source", source ) ;
	}

	@Override
	public void returns(SymbolScope scope, RepresentationNode source) {
		Objects.requireNonNull(scope) ;
		Objects.requireNonNull(source) ;
		insertNewFact( "ScopeReturn", "scope", scope, "source", source ) ;
	}
	
	@Override
	public SymbolScope rootScope() {
		m_kieSession.fireAllRules() ;
		QueryResults queryResults = m_kieSession.getQueryResults("ScopeQuery", new Object[] { null } ) ; 
		Iterator<QueryResultsRow> i = queryResults.iterator();
		if ( i.hasNext() ) {
			QueryResultsRow row = i.next();
			Object scope = row.get("symbolScope") ;
			return (SymbolScope)scope ;
		}
		return null ;
	}
	
	@Override
	public Set<RepresentationNode> scopeReturns( SymbolScope scope ) {
		Set<RepresentationNode> nodes = new HashSet<RepresentationNode>();
		m_kieSession.fireAllRules() ;
		QueryResults queryResults = m_kieSession.getQueryResults("ScopeReturnsQuery", scope ) ; 
		Iterator<QueryResultsRow> i = queryResults.iterator();
		while ( i.hasNext() ) {
			QueryResultsRow row = i.next();
			FactType scopeReturnFactType = getFactType("ScopeReturn") ;
			Object scopeReturn = row.get("scopeReturn") ;
			nodes.add( (RepresentationNode)scopeReturnFactType.get(scopeReturn, "source") ) ;
		}
		return nodes ;
	}
	
	@Override
	public Set<RepresentationNode> methodReturns( MethodSymbol method ) {
		Set<RepresentationNode> nodes = new HashSet<RepresentationNode>();
		m_kieSession.fireAllRules() ;
		QueryResults queryResults = m_kieSession.getQueryResults("MethodReturnsQuery", method ) ; 
		Iterator<QueryResultsRow> i = queryResults.iterator();
		while ( i.hasNext() ) {
			QueryResultsRow row = i.next();
			FactType methodReturnFactType = getFactType("MethodReturn") ;
			Object methodReturn = row.get("methodReturn") ;
			nodes.add( (RepresentationNode)methodReturnFactType.get(methodReturn, "source") ) ;
		}
		return nodes ;
	}
	
	@Override
	public Set<Representation> representsAtLeast( Symbol symbol ) {
		Set<Representation> representations = new HashSet<Representation>();
		m_kieSession.fireAllRules() ;
		QueryResults queryResults = m_kieSession.getQueryResults("NodeMustRepresentAtLeastsQuery", symbol ) ; 
		Iterator<QueryResultsRow> i = queryResults.iterator();
		while ( i.hasNext() ) {
			QueryResultsRow row = i.next();
			FactType nodeMustRepresentAtLeastFactType = getFactType("NodeMustRepresentAtLeast") ;
			Object nodeMustRepresentAtLeast = row.get("nodeMustRepresentAtLeast") ;
			representations.add( (Representation)nodeMustRepresentAtLeastFactType.get(nodeMustRepresentAtLeast, "representation") ) ;
		}
		return representations ;
	}
	
	@Override
	public Representation representationFor(RepresentationNode representationNode) {
		m_kieSession.fireAllRules() ;
		QueryResults queryResults = m_kieSession.getQueryResults("NodeRepresentedAsQuery", representationNode ) ; 
		Iterator<QueryResultsRow> i = queryResults.iterator();
		if ( i.hasNext() ) {
			QueryResultsRow row = i.next();
			FactType representedAsFactType = getFactType("RepresentedAs") ;
			Object representedAs = row.get("representedAs") ;
			return (Representation) representedAsFactType.get( representedAs, "representation" ) ;
		}
		return null ;
	}

	@Override
	public <T extends VariableSymbol> Optional<T> variableSymbolFor(SymbolScope scope, String name, SymbolScopeSearchStrategy searchStragtegy) {
		VariableSymbol symbol = variableSymbolInScope( scope, name ) ;
		switch ( searchStragtegy ) {
		case ENCLOSING:
			if ( null != scope.getEnclosingScope() )
				return variableSymbolFor(scope.getEnclosingScope(), name, searchStragtegy) ;
			else
				return Optional.empty() ;
		case IMMEDIATE:
		default:
			return Optional.ofNullable( (T)symbol ) ;
		}
	}
	
	@Override
	public <T extends MethodSymbol> Optional<T> methodSymbolFor(SymbolScope scope, String name, int numberOfParameters, SymbolScopeSearchStrategy searchStragtegy) {
		MethodSymbol symbol = methodSymbolInScope( scope, name, numberOfParameters ) ;
		switch ( searchStragtegy ) {
		case ENCLOSING:
			if ( null != scope.getEnclosingScope() )
				return methodSymbolFor(scope.getEnclosingScope(), name, numberOfParameters, searchStragtegy) ;
			else
				return Optional.empty() ;
		case IMMEDIATE:
		default:
			return Optional.ofNullable( (T)symbol ) ;
		}
	}
	
	@Override
	public <T extends ClassSymbol> Optional<T> classSymbolFor(SymbolScope scope, String name, SymbolScopeSearchStrategy searchStragtegy) {
		ClassSymbol symbol = classSymbolInScope( scope, name ) ;
		switch ( searchStragtegy ) {
		case ENCLOSING:
			if ( null != scope.getEnclosingScope() )
				return classSymbolFor(scope.getEnclosingScope(), name, searchStragtegy) ;
			else
				return Optional.empty() ;
		case IMMEDIATE:
		default:
			return Optional.ofNullable( (T)symbol ) ;
		}
	}
	
	private VariableSymbol variableSymbolInScope(SymbolScope scope, String name) {
		m_kieSession.fireAllRules() ;
		QueryResults queryResults = m_kieSession.getQueryResults("VariableSymbolQuery", scope, name ) ; 
		Iterator<QueryResultsRow> i = queryResults.iterator();
		if ( i.hasNext() ) {
			QueryResultsRow row = i.next();
			VariableSymbol symbol = (VariableSymbol)row.get("symbol") ;
			return symbol ;
		}
		return null ;
	}

	private MethodSymbol methodSymbolInScope(SymbolScope scope, String name, int numberOfParameters) {
		m_kieSession.fireAllRules() ;
		QueryResults queryResults ;
		if ( numberOfParameters < 0 )
			queryResults = m_kieSession.getQueryResults("MethodSymbolIgnoringParametersQuery", scope, name ) ;
		else
			queryResults = m_kieSession.getQueryResults("MethodSymbolQuery", scope, name, numberOfParameters ) ;
		Iterator<QueryResultsRow> i = queryResults.iterator();
		if ( i.hasNext() ) {
			QueryResultsRow row = i.next();
			MethodSymbol symbol = (MethodSymbol)row.get("symbol") ;
			return symbol ;
		}
		return null ;
	}
	
	private ClassSymbol classSymbolInScope(SymbolScope scope, String name) {
		m_kieSession.fireAllRules() ;
		QueryResults queryResults = m_kieSession.getQueryResults("ClassSymbolQuery", scope, name ) ; 
		Iterator<QueryResultsRow> i = queryResults.iterator();
		if ( i.hasNext() ) {
			QueryResultsRow row = i.next();
			ClassSymbol symbol = (ClassSymbol)row.get("symbol") ;
			return symbol ;
		}
		return null ;
	}

	@Override
	public MethodSymbol enclosingMethod(SymbolScope scope) {
		m_kieSession.fireAllRules() ;
		QueryResults queryResults = m_kieSession.getQueryResults("EnclosingMethodQuery", scope ) ; 
		Iterator<QueryResultsRow> i = queryResults.iterator();
		if ( i.hasNext() ) {
			QueryResultsRow row = i.next();
			MethodSymbol methodSymbol = (MethodSymbol)row.get("methodSymbol") ;
			return methodSymbol ;
		}
		return null ;
	}

	@Override
	public ClassSymbol enclosingClass(SymbolScope scope) {
		m_kieSession.fireAllRules() ;
		QueryResults queryResults = m_kieSession.getQueryResults("EnclosingClassQuery", scope ) ; 
		Iterator<QueryResultsRow> i = queryResults.iterator();
		if ( i.hasNext() ) {
			QueryResultsRow row = i.next();
			ClassSymbol classSymbol = (ClassSymbol)row.get("classSymbol") ;
			return classSymbol ;
		}
		return null ;
	}

	@Override
	public Set<Symbol> allSymbols(SymbolScope scope) {
		Set<Symbol> symbols = new HashSet<Symbol>();
		m_kieSession.fireAllRules() ;
		QueryResults queryResults = m_kieSession.getQueryResults("SymbolsQuery", scope ) ; 
		Iterator<QueryResultsRow> i = queryResults.iterator();
		while ( i.hasNext() ) {
			QueryResultsRow row = i.next();
			Symbol symbol = (Symbol)row.get("symbol") ;
			symbols.add( symbol ) ;
		}
		return symbols ;
	}
	
	@Override
	public MethodParameter parameterOf(MethodSymbol method, String name) {
		m_kieSession.fireAllRules() ;
		QueryResults queryResults = m_kieSession.getQueryResults("MethodParameterByNameQuery", method, name ) ; 
		Iterator<QueryResultsRow> i = queryResults.iterator();
		if ( i.hasNext() ) {
			QueryResultsRow row = i.next();
			MethodParameter methodParameter = (MethodParameter)row.get("methodParameter") ;
			return methodParameter ;
		}
		return null ;
	}

	@Override
	public MethodParameter parameterOf(MethodSymbol method, int position) {
		m_kieSession.fireAllRules() ;
		QueryResults queryResults = m_kieSession.getQueryResults("MethodParameterByPositionQuery", method, position ) ; 
		Iterator<QueryResultsRow> i = queryResults.iterator();
		if ( i.hasNext() ) {
			QueryResultsRow row = i.next();
			MethodParameter methodParameter = (MethodParameter)row.get("methodParameter") ;
			return methodParameter ;
		}
		return null ;
	}
	
	@Override
	public Set<Object> rulesOn(RepresentationNode node) {
		Set<Object> facts = new HashSet<Object>();
		m_kieSession.fireAllRules() ;
		QueryResults queryResults = m_kieSession.getQueryResults("RulesQuery", node ) ; 
		Iterator<QueryResultsRow> i = queryResults.iterator();
		if ( i.hasNext() ) {
			QueryResultsRow row = i.next();
			Object fact = row.get("fact") ;
			facts.add( fact );
		}
		return facts ;
	}
	
	public abstract class RepresentationNodeFact implements RepresentationNode {
		@Override public RepresentationInference getSystem() { return RepresentationInferenceKie.this ; }

		@Override public String toString() { return getClass().getSimpleName(); }
		
		@Override
		public boolean equals(Object obj) {
			if ( null == obj ) return false ;
			if ( this == obj ) return true ;
			if ( !(obj instanceof RepresentationNode) ) return false ;
			RepresentationNode representationNode = (RepresentationNode)obj ;
			return getSystem() == representationNode.getSystem() ; // exact same system
		}
	}
	
	public abstract class EntityNodeFact extends RepresentationNodeFact implements EntityNode {
		
	}

	public class UnknownNodeFact extends RepresentationNodeFact {
		/* nothing known about it */
		/* just takes up space */
		
		@Override
		public boolean equals(Object obj) {
			if ( null == obj ) return false ;
			if ( this == obj ) return true ;
			if ( !(obj instanceof UnknownNodeFact) ) return false ;
			return false ; /* each one is unique */
		}
	}
	
	public class ConstantValueFact extends EntityNodeFact implements ConstantValue {
		private final Object m_value ;
		private final Representation m_representation ;

		public ConstantValueFact(Object value, Representation representation ) {
			Objects.requireNonNull(representation) ;
			m_value = value;
			m_representation = representation ;
		}

		@Override
		public Object getValue() { return m_value; }

		@Override
		public Representation getRepresentation() { return m_representation; }
		
		@Override public String toString() { return getClass().getSimpleName() + " = " + m_value + " (" + m_representation + ")"; }
		
		@Override
		public boolean equals(Object obj) {
			if ( null == obj ) return false ;
			if ( this == obj ) return true ;
			if ( !(obj instanceof ConstantValue) ) return false ;
			ConstantValue constantValue = (ConstantValue)obj ;
			return super.equals(obj) && (
					null == m_value
					? null == constantValue.getValue()
					: m_value.equals(constantValue.getValue())
				);
		}
	}
	
	public class SymbolScopeFact implements SymbolScope {
		private final SymbolScope m_enclosingScope;
		private final String m_description;

		public SymbolScopeFact() {
			m_enclosingScope = null;
			m_description = "<root>" ;
		}

		public SymbolScopeFact(SymbolScope enclosingScope, String description) {
			Objects.requireNonNull(enclosingScope) ;
			Objects.requireNonNull(description) ;
			m_enclosingScope = enclosingScope;
			m_description = description ;
		}
		
		@Override
		public SymbolScope getEnclosingScope() { return m_enclosingScope ; }

		@Override
		public String description() { return m_description ; }
		
		@Override
		public ClassSymbol createClass(String name) { return RepresentationInferenceKie.this.createClassSymbol(this, name) ; } 
		@Override
		public MethodSymbol createMethod(String name) { return RepresentationInferenceKie.this.createMethodSymbol(this, name) ; } 
		@Override
		public VariableSymbol createVariable(String name) { return RepresentationInferenceKie.this.createVariableSymbol(this, name) ; } 

		@Override
		public <T extends VariableSymbol> Optional<T> variableSymbolFor( String name, SymbolScopeSearchStrategy searchStrategy ) {
			return RepresentationInferenceKie.this.variableSymbolFor( this, name, searchStrategy ) ;
		} 
		@Override
		public <T extends MethodSymbol> Optional<T> methodSymbolFor( String name, int numberOfParameters, SymbolScopeSearchStrategy searchStrategy ) {
			return RepresentationInferenceKie.this.methodSymbolFor( this, name, numberOfParameters, searchStrategy ) ;
		} 
		@Override
		public <T extends ClassSymbol> Optional<T> classSymbolFor( String name, SymbolScopeSearchStrategy searchStrategy ) {
			return RepresentationInferenceKie.this.classSymbolFor( this, name, searchStrategy ) ;
		} 
		
		@Override
		public Set<Symbol> allSymbols() { return RepresentationInferenceKie.this.allSymbols( this ) ; } 
		@Override
		public MethodSymbol enclosingMethod() { return RepresentationInferenceKie.this.enclosingMethod( this ) ; }
		@Override
		public ClassSymbol enclosingClass() { return RepresentationInferenceKie.this.enclosingClass( this ) ; }

		@Override
		public void returns(RepresentationNode representationNode) { RepresentationInferenceKie.this.returns( this, representationNode ) ; }

		@Override public String toString() { return getClass().getSimpleName() + " : " + m_description ; }

		@Override
		public boolean equals(Object obj) {
			if ( null == obj ) return false ;
			if ( this == obj ) return true ;
			if ( !(obj instanceof SymbolScope) ) return false ;
			SymbolScope symbolScope = (SymbolScope)obj ;
			return
				(null == m_enclosingScope
					? null == symbolScope.getEnclosingScope()
					: m_enclosingScope.equals(symbolScope.getEnclosingScope())
				)
				&& m_description.equals( symbolScope.description() );
		}
	}
	
	public abstract class SymbolFact extends EntityNodeFact implements Symbol {
		private final SymbolScope m_scope ; 
		private final String m_name ; 

		public SymbolFact(SymbolScope scope, String name) {
			Objects.requireNonNull(scope) ;
			Objects.requireNonNull(name) ;
			m_scope = scope;
			m_name = name ;
		}
		
		@Override
		public SymbolScope getScope() { return m_scope ; }

		@Override
		public String getName() { return m_name ; }
		
		@Override
		public void declaredAs(Representation representation) { RepresentationInferenceKie.this.isDeclaredAs( this, representation ) ; }

		@Override public String toString() { return getClass().getSimpleName() + " \"" + m_name + "\""; }

		@Override
		public boolean equals(Object obj) {
			if ( null == obj ) return false ;
			if ( this == obj ) return true ;
			if ( !(obj instanceof Symbol) ) return false ;
			Symbol symbol = (Symbol)obj ;
			return super.equals(obj) && m_scope.equals(symbol.getScope()) && m_name.equals(symbol.getName());
		}
	}
	
	public class ClassSymbolFact extends SymbolFact implements ClassSymbol {
		private final SymbolScope m_declarationScope ;
		
		public ClassSymbolFact(SymbolScope scope, String name, SymbolScope declarationScope ) {
			super(scope, name);
			Objects.requireNonNull(declarationScope) ;
			m_declarationScope = declarationScope ;
		}

		@Override
		public SymbolScope getDeclarationScope() {
			return m_declarationScope;
		}

		@Override
		public boolean equals(Object obj) {
			if ( null == obj ) return false ;
			if ( this == obj ) return true ;
			if ( !(obj instanceof ClassSymbol) ) return false ;
			return super.equals(obj) ;
		}
	}
	
	public class MethodSymbolFact extends SymbolFact implements MethodSymbol {
		private final SymbolScope m_bodyScope ;
		
		public MethodSymbolFact(SymbolScope scope, String name, SymbolScope bodyScope ) {
			super(scope, name);
			Objects.requireNonNull(bodyScope) ;
			m_bodyScope = bodyScope ;
		}
		
		@Override
		public SymbolScope getBodyScope() {
			return m_bodyScope;
		}
		
		@Override
		public MethodParameter createParameter( int i, String name ) { return RepresentationInferenceKie.this.createMethodParameter(this, i, name) ; }

		@Override
		public MethodParameter parameter(int position) { return RepresentationInferenceKie.this.parameterOf(this, position) ; }
		@Override
		public MethodParameter parameter(String name) { return RepresentationInferenceKie.this.parameterOf(this, name) ; }

		@Override
		public void returns(RepresentationNode representationNode) { RepresentationInferenceKie.this.returns( this, representationNode ) ; }

		@Override
		public boolean equals(Object obj) {
			if ( null == obj ) return false ;
			if ( this == obj ) return true ;
			if ( !(obj instanceof MethodSymbol) ) return false ;
			return super.equals(obj) ;
		}
	}

	public class MethodParameterFact extends VariableSymbolFact implements MethodParameter {
		private final MethodSymbol m_method ;
		private final int m_position ;

		public MethodParameterFact(MethodSymbol method, int position, String name) {
			super(method.getBodyScope(),name) ;
			m_method = method ;
			m_position = position ;
		}

		@Override
		public MethodSymbol getMethod() { return m_method; }

		@Override
		public int getPosition() { return m_position; }

		@Override public String toString() { return getClass().getSimpleName() + " : " + m_method.getName() + " #" + m_position ; }

		@Override
		public boolean equals(Object obj) {
			if ( null == obj ) return false ;
			if ( this == obj ) return true ;
			if ( !(obj instanceof MethodParameter) ) return false ;
			MethodParameter methodParameter = (MethodParameter)obj ;
			return super.equals(obj) && m_method.equals(methodParameter.getMethod()) && m_position == methodParameter.getPosition() ;
		}
		
	}

	public class VariableSymbolFact extends SymbolFact implements VariableSymbol {
		
		public VariableSymbolFact(SymbolScope scope, String name) {
			super(scope, name);
		}
		
		@Override
		public VariableSymbol isAssigned( RepresentationNode source ) { return RepresentationInferenceKie.this.isAssignedFrom(this, source); }
		
		@Override
		public boolean equals(Object obj) {
			if ( null == obj ) return false ;
			if ( this == obj ) return true ;
			if ( !(obj instanceof VariableSymbol) ) return false ;
			return super.equals(obj) ;
		}
	}

}

