package edu.vanderbilt.clinicalsystems.m.lang.text;

public class ExpressionANTLRParserFactory implements RoutineParserFactory {
	
	@Override public RoutineParser createRoutineParser() {
		return new RoutineANTLRParser() ;
	}
	
}
