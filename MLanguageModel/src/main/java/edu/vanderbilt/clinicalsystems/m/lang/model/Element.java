package edu.vanderbilt.clinicalsystems.m.lang.model;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public interface Element {

	void write( RoutineWriter writer ) throws RoutineWriterException ; 
	
}
