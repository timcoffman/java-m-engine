package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.io.IOException;

public interface InputOutputDevice {
	
	String input( Integer characterLimit, Integer timeout ) throws IOException ;
	
	void output( String text ) throws IOException ;
	void outputCarriageReturn() throws IOException ;
	void outputPageFeed() throws IOException ;
	void outputColumnMove( int column ) throws IOException ;
	
}
