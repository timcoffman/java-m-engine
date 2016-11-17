package edu.vanderbilt.clinicalsystems.m.lang.text;

public class RoutineANTLRParserFactory implements RoutineParserFactory, CommandParserFactory, ExpressionParserFactory {
	
	@Override public RoutineParser createRoutineParser() {
		return new RoutineANTLRParser() ;
	}

	@Override public ExpressionParser createExpressionParser() {
		return new RoutineANTLRParser() ;
	}

	@Override public CommandParser createCommandParser() {
		return new RoutineANTLRParser() ;
	}
	
}
