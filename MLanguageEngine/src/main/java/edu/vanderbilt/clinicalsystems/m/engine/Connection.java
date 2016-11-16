package edu.vanderbilt.clinicalsystems.m.engine;

import java.io.Reader;
import java.io.Writer;

import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;

public interface Connection extends AutoCloseable {
	
	void submit( RoutineElement routineElement, Reader reader, Writer writer ) throws ConnectionError ;
	
	default
	void submit( RoutineElement routineElement ) throws ConnectionError
	{ submit(routineElement, null, null); }

}
