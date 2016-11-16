package edu.vanderbilt.clinicalsystems.m.engine;

public class ConnectionError extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ConnectionError() { }
	public ConnectionError(String message, Throwable cause) { super(message, cause); }
	public ConnectionError(String message) { super(message); }
	public ConnectionError(Throwable cause) { super(cause); }

	
	
}
