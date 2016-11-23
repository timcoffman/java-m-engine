package edu.vanderbilt.clinicalsystems.m.engine.virtual;

public interface NodeMap {

	Node at(String key);
	Node create(String key);
	Node get(String key);
	void drop(String key);
	
	void dropAll();

	void merge( NodeMap nodeMap ) ;
	
	void insert(String key,Node node);
	
	boolean isEmpty();

	String keyPreceding(String key);
	String keyFollowing(String key);

	String firstKey();
	String lastKey();

}