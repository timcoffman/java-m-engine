package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.util.List;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;


public interface ExecutionFrame extends Executor, Evaluator, AutoCloseable {

	NodeMap root(Scope scope);
	ExecutionFrame parentFrame();
	GlobalContext globalContext();

	boolean hasLocalProperty( String name ) ;
	void setLocalProperty(String name, Object value) ;
	<T> T getLocalProperty(String name, Class<T> ofType) ;
	<T> T getProperty(String name, Class<T> ofType) ;
	
	ExecutionFrame createChildFrame() ;
	
	InputOutputDevice inputOutputDevice();

	Node findNode(VariableReference variable) throws EngineException ;
	
	boolean hasLocalNode( String variableName ) ;
	Node findLocalNode( String variableName ) ;
	Node createLocalNode(String variableName);
	Node insertLocalNode(String variableName, Node node );
	
	Node createNode(DirectVariableReference variable);
	
	List<? extends Command> interpretCommands(String unparsedCode) throws EngineException ;

	@Override void close() throws EngineException ; /* close()-ing can only cause EngineException */ 
}
