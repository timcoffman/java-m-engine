package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import edu.vanderbilt.clinicalsystems.m.engine.virtual.Installer.TargetInstanceResolver;

public class Connection extends StandardExecutionFrame {

	public Connection( NodeMap root, GlobalContext globalContext ) {
		super(root, globalContext) ;
		installDefaultTargetInstanceResolver() ;
	}
		
	public Connection( GlobalContext globalContext, InputOutputDevice inputOutputDevice ) {
		super(globalContext, inputOutputDevice) ;
		installDefaultTargetInstanceResolver() ;
	}

	private void installDefaultTargetInstanceResolver() {
		TargetInstanceResolver nullResolver = (t,r,f)->null ;
		setLocalProperty( "target-instance-resolver", nullResolver ) ;
	}
}
