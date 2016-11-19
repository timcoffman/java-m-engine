package edu.vanderbilt.clinicalsystems.m.engine.virtual.handler;

import java.io.IOException;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.ErrorCode;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.ExecutionFrame;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.Node;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;

public class InputOutputHandler extends OutputHandler {

	public InputOutputHandler(ExecutionFrame frame) {
		super(frame);
	}
	
	@Override
	protected ExecutionResult readWrite(VariableReference variable) {
		try {
			Node node = frame().findNode(variable) ;
			try {
				String value = frame().inputOutputDevice().input( null, null ) ;
				node.assign( value ) ;
			} catch (IOException ex) {
				throw( new EngineException(ErrorCode.INPUT_ERROR, new String[] { }, ex) ) ;
			}
			
			return ExecutionResult.CONTINUE ;
		} catch ( EngineException ex ) {
			return caughtException(ex) ;
		}
	}

}
