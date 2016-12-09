package edu.vanderbilt.clinicalsystems.m.core.lib;

public class HaltCondition extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public HaltCondition() { super(); }
	public HaltCondition(String message, Throwable cause) { super(message, cause); }
	public HaltCondition(String message) { super(message); }
	public HaltCondition(Throwable cause) { super(cause); }
	
}
