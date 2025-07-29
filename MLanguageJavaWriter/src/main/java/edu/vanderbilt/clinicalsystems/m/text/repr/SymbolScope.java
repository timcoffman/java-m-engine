package edu.vanderbilt.clinicalsystems.m.text.repr;

import static edu.vanderbilt.clinicalsystems.m.text.repr.RepresentationInference.SymbolScopeSearchStrategy.IMMEDIATE;

import java.util.Optional;
import java.util.Set;

import edu.vanderbilt.clinicalsystems.m.text.repr.RepresentationInference.SymbolScopeSearchStrategy;

public interface SymbolScope {
	SymbolScope getEnclosingScope() ;
	String description();

	default <T extends VariableSymbol> Optional<T> variableSymbolFor(String name) { return variableSymbolFor(name, IMMEDIATE) ; }
	<T extends VariableSymbol> Optional<T> variableSymbolFor(String name,SymbolScopeSearchStrategy searchStrategy);
	
	default <T extends MethodSymbol> Optional<T> methodSymbolFor(String name, int numberOfParameters) { return methodSymbolFor(name, numberOfParameters, IMMEDIATE) ; }
	<T extends MethodSymbol> Optional<T> methodSymbolFor(String name,int numberOfParameters,SymbolScopeSearchStrategy searchStrategy);
	
	default <T extends ClassSymbol> Optional<T> classSymbolFor(String name) { return classSymbolFor(name, IMMEDIATE) ; }
	<T extends ClassSymbol> Optional<T> classSymbolFor(String name, SymbolScopeSearchStrategy searchStrategy);
	
	Set<Symbol> allSymbols();

	void returns(RepresentationNode representationNode);

	ClassSymbol createClass(String name);
	MethodSymbol createMethod(String name);
	VariableSymbol createVariable(String name);
	
	MethodSymbol enclosingMethod();
	ClassSymbol enclosingClass();
}

