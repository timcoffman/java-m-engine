package edu.vanderbilt.clinicalsystems.epic.annotation;

import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.ARRAY_TYPE;

import java.io.PrintStream;

import edu.vanderbilt.clinicalsystems.epic.api.oo.EpicCommunicationFoundation;
import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.annotation.InjectRoutine;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;
import edu.vanderbilt.clinicalsystems.m.core.lib.ReadWrite;
import edu.vanderbilt.clinicalsystems.m.core.lib.Text;

@RoutineUnit
public class Tags {
	
	public Tags() {
	}
	
	
	public Tags( Object constructorParameter ) {
	}
	
	public void methodThatsNotATag() {
	}
	
	@RoutineTag
	public static void tagWithNoParameters() {
	}

	@RoutineTag
	public static void tagWithStringParameter( String x ) {
		ReadWrite.write(x);
	}

	@RoutineTag
	public static void tagWithIntegerParameter( int x ) {
		ReadWrite.write(x);
	}

	@RoutineTag
	public static void tagWithDoubleParameter( double x ) {
		ReadWrite.write(x);
	}

	@RoutineTag
	public static void tagWithValueParameter( Value x ) {
		ReadWrite.write(x);
	}

	@RoutineTag
	public static void tagWithOtherParameter( PrintStream x ) {
		ReadWrite.write(x);
	}
	
}
