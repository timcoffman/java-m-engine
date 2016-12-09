package edu.vanderbilt.clinicalsystems.m.lang;

public enum RoutineAccess {
	EXPLICIT(true),
	LOCAL(false);
	
	private RoutineAccess( boolean requiresRoutineName ) { }
}