package edu.vanderbilt.clinicalsystems.m.engine;

public class MissingRoutineException extends Exception {
	private static final long serialVersionUID = 1L;

	public MissingRoutineException() { super(); }
	public MissingRoutineException(String message, Throwable cause) { super(message, cause); }
	public MissingRoutineException(String message) { super(message); }
	public MissingRoutineException(Throwable cause) { super(cause); }

}
