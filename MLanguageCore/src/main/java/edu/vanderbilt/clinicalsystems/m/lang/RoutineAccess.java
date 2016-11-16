package edu.vanderbilt.clinicalsystems.m.lang;

public enum RoutineAccess {
	EXPLICIT(true),
	IMPLICIT(false),
	LOCAL(false);
	
	private RoutineAccess( boolean requiresRoutineName ) { }
}