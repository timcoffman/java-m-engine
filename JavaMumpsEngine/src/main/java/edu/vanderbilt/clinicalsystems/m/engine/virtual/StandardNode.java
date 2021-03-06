package edu.vanderbilt.clinicalsystems.m.engine.virtual;

public class StandardNode extends TreeNodeMap implements Node {

	private String m_value = null ;
	
	@Override public String value() { return m_value ; }
	@Override public void assign(String newValue) { m_value = newValue ; }
	
	@Override public void merge(NodeMap srcNodeMap) {
		super.merge(srcNodeMap);
		if ( srcNodeMap instanceof Node )
			assign( ((Node)srcNodeMap).value() ) ;
	}
	
	@Override public boolean equals( Object obj ) {
		if ( null == obj ) return false ;
		if ( this == obj ) return true ;
		if ( !(obj instanceof Node) ) return false ;
		Node node = (Node)obj ;
		if ( null == m_value ) {
			if ( null != node.value() ) return false ;
		} else {
			if ( !m_value.equals( node.value() ) ) return false ;
		}
		return super.equals(obj) ;
	}
	
	@Override
	public Object toObject(Class<?> ofType) {
		throw new UnsupportedOperationException( this + " cannot be converted to a " + ofType) ;
	}
	
	
}
