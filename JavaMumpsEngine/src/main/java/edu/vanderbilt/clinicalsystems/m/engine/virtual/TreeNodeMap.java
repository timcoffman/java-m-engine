package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.util.TreeMap;

public class TreeNodeMap implements NodeMap {

	public interface Entry extends Comparable<Entry> {
		String key() ;
		Node node() ;
	}
	
	private TreeMap<String,Node> m_entries ;
	
	private TreeMap<String,Node> requireEntries() {
		if ( m_entries == null )
			m_entries = new TreeMap<String, Node>() ;
		return m_entries ;
	}

	private TreeMap<String,Node> expectEntries() {
		if ( null == m_entries )
			throw new IllegalArgumentException("no entries") ;
		return m_entries ;
	}

	private void validateKey( String key ) {
		if ( null == key )
			throw new IllegalArgumentException("null is not a valid key") ;
	}
	
	private void validateNode( Node node ) {
		if ( null == node )
			throw new IllegalArgumentException("null is not a valid node") ;
	}
	
	@Override
	public Node at( String key ) {
		validateKey(key);
		Node node = expectEntries().get( key ) ;
		if ( null == node )
			throw new IllegalArgumentException("no such key \"" + key + "\"") ;
		return node ;
	}
	
	@Override
	public void drop( String key ) {
		validateKey(key);
		if ( null == expectEntries().remove( key ) )
			throw new IllegalArgumentException("no such key \"" + key + "\"") ;
	}

	@Override
	public void dropAll() {
		if ( null != m_entries )
			m_entries.clear();
	}
	
	@Override
	public void merge( NodeMap srcNodeMap ) {
		if ( null == srcNodeMap || srcNodeMap.isEmpty() )
			return ;
		String lastKey = srcNodeMap.lastKey() ;
		String key = srcNodeMap.firstKey() ;
		do {
			Node srcNode = srcNodeMap.at(key) ;
			Node node = m_entries.containsKey(key) ? at(key) : create(key) ;
			node.assign( srcNode.value() );
			node.merge( srcNode );
		} while ( !lastKey.equals(key) ) ;
	}

	
	private Node createNode() { return new StandardNode() ; }
	
	@Override
	public Node create( String key ) {
		validateKey(key);
		Node node = createNode() ;
		validateNode(node);
		if ( null != requireEntries().put( key, node ) )
			throw new IllegalArgumentException("key \"" + key + "\" already present") ;
		return node ;
	}
	
	@Override
	public Node get( String key ) {
		validateKey(key);
		Node node = requireEntries().get( key );
		if ( null == node )
			node = create( key ) ;
		return node ;
	}
	
	@Override public boolean isEmpty() { return null == m_entries || m_entries.isEmpty() ; }
	
	@Override public String keyPreceding( String key ) {
		if ( null == key )
			return lastKey() ;
		return expectEntries().lowerKey (key) ;
	}
	@Override public String keyFollowing( String key ) {
		if ( null == key )
			return firstKey() ;
		return expectEntries().higherKey(key) ;
	}

	@Override public String firstKey() { return expectEntries().firstKey() ; }
	@Override public String  lastKey() { return expectEntries().lastKey() ; }
	
	@Override public boolean equals( Object obj ) {
		if ( null == obj ) return false ;
		if ( this == obj ) return true ;
		if ( !(obj instanceof NodeMap) ) return false ;
		NodeMap nodeMap = (NodeMap)obj ;
		if ( isEmpty() ) {
			if ( !nodeMap.isEmpty() ) return false ;
		} else {
			String lastKey = m_entries.lastKey() ;
			String key = m_entries.firstKey() ;
			do {
				if ( !m_entries.get(key).equals( nodeMap.at(key) ) ) return false ;
			} while ( !lastKey.equals(key) ) ;
		}
		return true ;
	}
}
