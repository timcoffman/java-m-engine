package edu.vanderbilt.clinicalsystems.m.lang.text;

public class CommandANTLRParserFactory implements RoutineParserFactory {
	
	@Override public RoutineParser createRoutineParser() {
		return new RoutineANTLRParser() ;
	}
	
}
