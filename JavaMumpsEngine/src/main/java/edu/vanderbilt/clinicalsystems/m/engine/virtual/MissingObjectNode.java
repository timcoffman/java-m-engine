package edu.vanderbilt.clinicalsystems.m.engine.virtual;



public class MissingObjectNode implements Node {

	private static final MissingObjectNode MISSING_OBJECT_NODE = new MissingObjectNode() ;
	
	public static MissingObjectNode instance() { return MISSING_OBJECT_NODE ; }
	
	@Override public String value() { return null ; }

	@Override public void assign(String newValue) { throw new UnsupportedOperationException("node does not support assignment") ; }

	@Override public boolean isEmpty() { return true ; }

	@Override public String keyPreceding(String key) { return null ; }
	@Override public String keyFollowing(String key) { return null ; }
	
	protected <T> T subscriptModificationNotSupported() { throw new UnsupportedOperationException("node does not support modifying subscripts") ; }
	
	@Override public String firstKey() { return null ; }
	@Override public String lastKey() { return null ; }
	
	@Override public Node get(String key) { return subscriptModificationNotSupported() ; }
	@Override public void merge(NodeMap srcNodeMap) { subscriptModificationNotSupported() ; }
	@Override public Node create(String key) { return subscriptModificationNotSupported() ; }
	@Override public void insert(String key, Node node) { subscriptModificationNotSupported() ; }
	@Override public void drop(String key) { subscriptModificationNotSupported() ; }
	@Override public void dropAll() { subscriptModificationNotSupported() ; }
	
	@Override public Node at(String key) { throw new IllegalArgumentException("no such key \"" + key + "\"") ; }
	
	@Override public Object toObject(Class<?> ofType) { return null ; }
}
