package edu.vanderbilt.clinicalsystems.m.engine.virtual;


public class Connection extends ExecutionFrame {

	public Connection( NodeMap root, GlobalContext globalContext ) {
		super(root, globalContext) ;
	}
		
	public Connection( NodeMap root, GlobalContext globalContext, InputOutputDevice inputOutputDevice ) {
		super(root, globalContext, inputOutputDevice) ;
	}
	
}
