package edu.vanderbilt.clinicalsystems.m.lang.model;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class ParameterName implements Element {

	private final String m_name ;
	
	public ParameterName( String name ) {
		m_name = name ;
	}
	
	public String name() { return m_name ; }

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this) ;
	}

	@Override
	public String toString() { return m_name ; }
	
}
