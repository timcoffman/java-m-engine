package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import static org.hamcrest.CoreMatchers.equalTo;
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
	
}
