package edu.vanderbilt.clinicalsystems.m.engine;

public interface ConnectionFactory {
	
	Connection createConnection( String connectionString ) throws ConnectionError ;
	
}
