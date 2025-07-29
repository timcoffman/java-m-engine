package edu.vanderbilt.clinicalsystems.m.text.repr;

import static edu.vanderbilt.clinicalsystems.m.text.repr.RepresentationInference.SymbolScopeSearchStrategy.IMMEDIATE;

import java.io.PrintStream;
import java.util.Optional;
import java.util.Set;

import edu.vanderbilt.clinicalsystems.m.lang.text.Representation;

public interface RepresentationInference {
	
	ConstantValue createConstantValue(Object value, Representation representation) ;

	SymbolScope createScope() ;
	SymbolScope createScope( SymbolScope enclosingScope, String description ) ;

	SymbolScope rootScope();
	
	ClassSymbol enclosingClass(SymbolScope scope);
	MethodSymbol enclosingMethod(SymbolScope scope);

	Set<RepresentationNode> scopeReturns(SymbolScope scope);
	Set<RepresentationNode> methodReturns(MethodSymbol method);
	Set<Representation> representsAtLeast(Symbol symbol);

	ClassSymbol createClassSymbol(SymbolScope scope, String name);
	MethodSymbol createMethodSymbol(SymbolScope scope, String name) ;
	MethodParameter createMethodParameter(MethodSymbol method, int i, String name);
	VariableSymbol createVariableSymbol(SymbolScope scope, String name) ;
	RepresentationNode createUnknownNode();

	void isDeclaredAs(RepresentationNode node, Representation representation);

	void isUsedAs(RepresentationNode node, Representation representation);
	
	VariableSymbol isAssignedFrom(VariableSymbol variable, RepresentationNode source) ;
	
	OperationNode isComparedWith(RepresentationNode lhs, RepresentationNode rhs) ;
	OperationNode areAlternatives(RepresentationNode node, RepresentationNode alternativeNode);
	OperationNode combines(RepresentationNode node, RepresentationNode withNode);
	OperationNode transforms(RepresentationNode node, Representation producingRepresentation);
	
	void returns(MethodSymbol method, RepresentationNode source) ;
	void returns(SymbolScope scope, RepresentationNode source) ;
	
	Representation representationFor(RepresentationNode source) ;

	public enum SymbolScopeSearchStrategy {
		IMMEDIATE,
		ENCLOSING
	}
	
	default <T extends VariableSymbol> Optional<T> variableSymbolFor(SymbolScope scope, String name ) { return variableSymbolFor(scope, name, IMMEDIATE ); }
	<T extends VariableSymbol> Optional<T> variableSymbolFor(SymbolScope scope, String name, SymbolScopeSearchStrategy searchStragtegy);
	
	default <T extends MethodSymbol> Optional<T> methodSymbolFor(SymbolScope scope, String name, int numberOfParameters ) { return methodSymbolFor(scope, name, numberOfParameters, IMMEDIATE ); }
	<T extends MethodSymbol> Optional<T> methodSymbolFor(SymbolScope scope, String name, int numberOfParameters, SymbolScopeSearchStrategy searchStragtegy);
	
	default <T extends ClassSymbol> Optional<T> classSymbolFor(SymbolScope scope, String name ) { return classSymbolFor(scope, name, IMMEDIATE ); }
	<T extends ClassSymbol> Optional<T> classSymbolFor(SymbolScope scope, String name, SymbolScopeSearchStrategy searchStragtegy);
	
	Set<Symbol> allSymbols(SymbolScope scope);

	MethodParameter parameterOf(MethodSymbol method, String name);
	MethodParameter parameterOf(MethodSymbol method, int position);
	
	Set<Object> rulesOn(RepresentationNode node);

	void print(PrintStream printStream);
	void print(SymbolScope scope, PrintStream printStream);
}
