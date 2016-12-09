package edu.vanderbilt.clinicalsystems.m.lang.model;

import java.io.Serializable;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public interface Element extends Serializable {

	void write( RoutineWriter writer ) throws RoutineWriterException ; 
	
}
