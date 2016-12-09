package edu.vanderbilt.clinicalsystems.m.engine.virtual.node.builtin;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import edu.vanderbilt.clinicalsystems.m.engine.virtual.ReadOnlyNode;

public class BuiltinHorologNode extends ReadOnlyNode {
	private static final LocalDate EPOCH = LocalDate.of(1840, 12, 31) ;
	private static final LocalTime MIDNIGHT = LocalTime.of(0, 0, 0) ; 
	
	@Override
	protected String readOnlyValue() {
		long days = ChronoUnit.DAYS.between(EPOCH, LocalDate.now()) ;
		long seconds = ChronoUnit.SECONDS.between(MIDNIGHT, LocalTime.now()) ;
		return Long.toString(days) + "," + Long.toString(seconds) ;
	}
	
}