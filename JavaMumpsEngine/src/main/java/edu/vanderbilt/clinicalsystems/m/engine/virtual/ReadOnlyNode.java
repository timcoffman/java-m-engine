package edu.vanderbilt.clinicalsystems.m.engine.virtual;


public abstract class ReadOnlyNode implements Node {

	@Override public String value() { return readOnlyValue() ; }
	
	protected abstract String readOnlyValue() ;

	@Override public void assign(String newValue) { throw new UnsupportedOperationException("node does not support assignment") ; }

	@Override public boolean isEmpty() { return true ; }

	@Override public String keyPreceding(String key) { return null ; }
	@Override public String keyFollowing(String key) { return null ; }
	
	protected <T> T subscriptsNotSupported() { throw new UnsupportedOperationException("node does not support subscripts") ; }
	
	@Override public String firstKey() { return subscriptsNotSupported() ; }
	@Override public String lastKey() { return subscriptsNotSupported() ; }
	
	@Override public Node get(String key) { return subscriptsNotSupported() ; }
	@Override public void merge(NodeMap srcNodeMap) { subscriptsNotSupported() ; }
	@Override public Node create(String key) { return subscriptsNotSupported() ; }
	@Override public void insert(String key, Node node) { subscriptsNotSupported() ; }
	@Override public Node at(String key) { return subscriptsNotSupported() ; }
	@Override public void drop(String key) { subscriptsNotSupported() ; }

	@Override public void dropAll() { /* no action */ }

	@Override
	public Object toObject(Class<?> ofType) {
		throw new UnsupportedOperationException( this + " cannot be converted to a " + ofType) ;
	}
}
