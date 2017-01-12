package edu.vanderbilt.clinicalsystems.m.lang.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class Tag implements RoutineElement {

	private static final long serialVersionUID = 1L;
	
	private final String m_name ;
	private final List<ParameterName> m_parameterNames = new ArrayList<ParameterName>();
	
	public Tag( String name ) {
		m_name = name ;
	}
	
	public Tag( String name, List<ParameterName> parameterNames ) {
		m_name = name ;
		m_parameterNames.addAll( parameterNames ) ;
	}
	
	@Override
	public Object hint( String hintName ) { return null ; }
	
	public String name() { return m_name ; }
	public Iterable<ParameterName> parameterNames() { return m_parameterNames ; }

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this) ;
	}

	@Override
	public String toString() {
		return m_name + "(" + m_parameterNames.stream().map(Object::toString).collect(Collectors.joining(",")) + "):";
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) return true ;
		if ( null == obj ) return false ;
		if ( !(obj instanceof Tag) ) return false ;
		Tag tag = (Tag)obj ;
		return
			m_name.equals( tag.m_name )
			&& m_parameterNames.equals( tag.m_parameterNames )
			;
	}

	
}
