package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.util.Comparator;
import java.util.TreeMap;

public class TreeNodeMap implements NodeMap {
	
	private static final Comparator<? super String> COLLATOR = new MLanguageCollator() ;
	
	public interface Entry extends Comparable<Entry> {
		String key() ;
		Node node() ;
	}
	
	private TreeMap<String,Node> m_entries ;
	
	private TreeMap<String,Node> requireEntries() {
		if ( m_entries == null )
			m_entries = new TreeMap<String, Node>(COLLATOR) ;
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
	
	private int compareKeys( String k1, String k2 ) {
		if ( k1 == k2 ) {
			return 0 ;
		} else if ( null == k1 ) {
			return 1 ;
		} else if ( null == k2 ) {
			return -1 ;
		} else {
			return k1.compareTo( k2 ) ;
		}
			
	}
	
	@Override
	public void merge( NodeMap srcNodeMap ) {
		if ( null == srcNodeMap || srcNodeMap.isEmpty() || this == srcNodeMap )
			return ;
		String key = this.isEmpty() ? null : firstKey() ;
		String srcKey = srcNodeMap.firstKey() ;
		while ( key != null || srcKey != null ) {
			int c = compareKeys( key, srcKey ) ;
			if ( c < 0 ) {
				/* node only exists here */
				// advance the key
				key = keyFollowing(key) ;
			} else if ( c > 0 ) {
				/* node only exists in the source */
				Node srcNode = srcNodeMap.at(srcKey) ;
				Node node = create(srcKey);
				node.merge( srcNode );
				// advance the source key
				srcKey = srcNodeMap.keyFollowing(srcKey) ;
			} else {
				/* node exists both here and in the source */
				Node srcNode = srcNodeMap.at(srcKey) ;
				Node node = at(key);
				node.merge( srcNode );
				// advance both keys
				key = keyFollowing(key) ;
				srcKey = srcNodeMap.keyFollowing(srcKey) ;
			}
		} ;
	}

	
	protected Node createNode() { return new StandardNode() ; }
	
	@Override
	public Node create( String key ) {
		Node node = createNode() ;
		insert( key, node ) ;
		return node ;
	}
	
	@Override
	public void insert(String key,Node node) {
		validateKey(key);
		validateNode(node);
		if ( null != requireEntries().put( key, node ) )
			throw new IllegalArgumentException("key \"" + key + "\" already present") ;
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
		return expectEntries().lowerKey (key) ;
	}
	@Override public String keyFollowing( String key ) {
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
			String key = firstKey() ;
			String otherKey = nodeMap.isEmpty() ? null : nodeMap.firstKey() ;
			while ( key != null || otherKey != null ) {
				if ( 0 != compareKeys(key, otherKey) ) return false ;
				if ( !at(key).equals( nodeMap.at(otherKey) ) ) return false ;
				
				key=keyFollowing(key) ;
				otherKey=nodeMap.keyFollowing(otherKey) ;
			}
		}
		return true ;
	}
}
