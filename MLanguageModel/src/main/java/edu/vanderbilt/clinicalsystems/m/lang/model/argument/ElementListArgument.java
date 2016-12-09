package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.vanderbilt.clinicalsystems.m.lang.model.Element;

public abstract class ElementListArgument<E extends Element> extends Argument {

	private static final long serialVersionUID = 1L;
	
	protected final List<E> m_elements = new ArrayList<E>();

	public ElementListArgument() { }
	public ElementListArgument( List<? extends E> elements ) { append(elements) ; }

	public void append(E element) { check(element) ; m_elements.add( element ) ; }
	public void append(List<? extends E> elements) { elements.forEach( (e)->check(e) ); m_elements.addAll( elements ) ; }
	
	public void append(ElementListArgument<? extends E> elementList) { elementList.elements().forEach( (e)->append(e) ); }

	public Iterable<E> elements() { return m_elements ; }

	protected void check( E element ) throws IllegalArgumentException {
		if ( null == element )
			throw new IllegalArgumentException( "element cannot be null" ) ;
	}
	
	@Override
	protected String unformattedRepresentation() {
		return m_elements.stream().map((e)->e.toString()).collect( Collectors.joining(", ") ) ;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) return true ;
		if ( null == obj ) return false ;
		if ( !(obj instanceof ElementListArgument) ) return false ;
		ElementListArgument<?> elementListArgument = (ElementListArgument<?>)obj ;
		return m_elements.equals( elementListArgument.m_elements ) ;
	}
}