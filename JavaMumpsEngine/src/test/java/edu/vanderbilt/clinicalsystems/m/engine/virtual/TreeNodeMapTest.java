package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class TreeNodeMapTest {

	private TreeNodeMap m_treeNodeMap ;
	
	@Before
	public void configureDatabase() {
		m_treeNodeMap = new TreeNodeMap() ;
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void cannotRetrieveAbsentKey() {
		m_treeNodeMap.at( "missing key" ) ;
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void cannotAddKeyTwice() {
		m_treeNodeMap.create( "present key" ) ;
		m_treeNodeMap.create( "present key" ) ;
	}

	@Test(expected=IllegalArgumentException.class)
	public void cannotDropAbsentKey() {
		m_treeNodeMap.drop( "missing key" ) ;
	}
	
	@Test
	public void canDropKey() {
		m_treeNodeMap.create( "present key" ) ;
		m_treeNodeMap.drop( "present key" ) ;
		assertThat( m_treeNodeMap.isEmpty(), equalTo(true) ) ;
	}
	
	@Test
	public void canCreateAndRetrieveKey() {
		Node node = m_treeNodeMap.create( "present key" ) ;
		assertThat( m_treeNodeMap.at("present key"), equalTo(node) ) ;
	}
	
	@Test
	public void canCreateTree() {
		Node node = m_treeNodeMap.create( "present key" ) ;
		node.create("a").assign("value") ;
		node.create("b").assign("value") ;
		assertThat( m_treeNodeMap.at("present key"), equalTo(node) ) ;
		assertThat( m_treeNodeMap.at("present key").at("a"), equalTo( node.at("a") ) ) ;
		assertThat( m_treeNodeMap.at("present key").at("b"), equalTo( node.at("b") ) ) ;
	}
	
	private static final String[] TEST_STRINGS = new String[] {
		"",
		"-7.1", "-1", "0", "1", "7.1",
		 String.valueOf( Character.toChars(0) ),
		 String.valueOf( Character.toChars(7) ),
		 "\n", " ", "  ", "/", 
		 "0!", "0..0", "0.0.0", ":1",
		 "@",
		 "A", "Z", "[", "\\", "]", "^", "`",
		 "a", "aa", "z", "zz", "~",
		 String.valueOf( Character.toChars(127) )
	} ;
	
	@Test
	public void canCreateKeys() {
		for ( int i = 0; i < TEST_STRINGS.length ; ++i ) {
			String key = TEST_STRINGS[i];
			
			Node node = m_treeNodeMap.create( key ) ;
			
			assertThat( m_treeNodeMap.firstKey(), equalTo( key ) ) ;
			assertThat( m_treeNodeMap.lastKey(), equalTo( key ) ) ;
			
			assertThat( m_treeNodeMap.at( key ), equalTo(node) ) ;
			m_treeNodeMap.drop( key );
		}
	} ;
	
	@Test
	public void canCollateKeys() {
		for ( int i = 0; i < TEST_STRINGS.length ; ++i )
			m_treeNodeMap.create( TEST_STRINGS[ TEST_STRINGS.length-1-i ] ) ; // create in reverse order
		
		int i = 0 ;
		String key = m_treeNodeMap.firstKey() ;
		assertThat( "key #" + i + " matches", key, equalTo( TEST_STRINGS[i] ) ) ;
		while ( i < TEST_STRINGS.length ) { 
			assertThat( "key #" + i + " matches", key, equalTo( TEST_STRINGS[i] ) ) ;
			++i ;
			key = m_treeNodeMap.keyFollowing(key) ;
		}
	}
	
	@Test
	public void canIdentifyEqualEmptyTrees() {
		Node node1 = m_treeNodeMap.create( "k1" ) ;
		Node node2 = m_treeNodeMap.create( "k2" ) ;
		assertThat( node1, equalTo(node2) ) ;
		assertThat( node2, equalTo(node1) ) ;
	}
	
	@Test
	public void canIdentifyEqualTreesWithSameEmptyKeys() {
		Node node1 = m_treeNodeMap.create( "k1" ) ;
		node1.create("a") ;
		Node node2 = m_treeNodeMap.create( "k2" ) ;
		node2.create("a") ;
		assertThat( node1, equalTo(node2) ) ;
		assertThat( node2, equalTo(node1) ) ;
	}
	
	@Test
	public void canIdentifyDIfferentTreesWithEmptyKeys() {
		Node node1 = m_treeNodeMap.create( "k1" ) ;
		node1.create("a") ;
		Node node2 = m_treeNodeMap.create( "k2" ) ;
		node2.create("b") ;
		assertThat( node1, not(equalTo(node2)) ) ;
		assertThat( node2, not(equalTo(node1)) ) ;
	}
	
	@Test
	public void canIdentifyDifferentTreesWithSomeEmptyKeys() {
		Node node1 = m_treeNodeMap.create( "k1" ) ;
		node1.create("a").assign("v") ;
		Node node2 = m_treeNodeMap.create( "k2" ) ;
		node2.create("b") ;
		assertThat( node1, not(equalTo(node2)) ) ;
		assertThat( node2, not(equalTo(node1)) ) ;
	}
	
	@Test
	public void canIdentifyEqualTreesWithEqualKeys() {
		Node node1 = m_treeNodeMap.create( "k1" ) ;
		node1.create("a").assign("v") ;
		Node node2 = m_treeNodeMap.create( "k2" ) ;
		node2.create("a").assign("v") ;
		assertThat( node1, equalTo(node2) ) ;
	}
	
	@Test
	public void canIdentifyEqualTreesWithUnequalKeys() {
		Node node1 = m_treeNodeMap.create( "k1" ) ;
		node1.create("a").assign("v1") ;
		Node node2 = m_treeNodeMap.create( "k2" ) ;
		node2.create("a").assign("v2") ;
		assertThat( node1, not(equalTo(node2)) ) ;
	}
	
}
