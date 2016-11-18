package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.util.HashMap;
import java.util.Map;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.AssignmentHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.CallHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.ReturnHandler;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinSystemVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.IndirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;

public class ExecutionFrame extends StandardExecutor implements Executor, Evaluator, AutoCloseable {
	
	private final NodeMap m_root ;
	private final GlobalContext m_globalContext ;
	private final Map<String,Node> m_locals = new HashMap<String, Node>(); 

	public ExecutionFrame( NodeMap root, GlobalContext globalContext ) {
		m_root = root ;
		m_globalContext = globalContext ;
	}
	
	@Override public void close() {
		/* nothing yet */
	}
	
	public ExecutionFrame createFrame() {
		return new ExecutionFrame(m_root, m_globalContext) ;
	}
	
	@Override
	public ExecutionResult execute( Command command ) {
		m_result = null ;
		if ( null != m_exception )
			return ExecutionResult.ERROR ;
		
		if ( null != command.condition() ) {
			try {
				if ( !evaluate( command.condition() ).toBoolean() )
					return ExecutionResult.CONTINUE;
			} catch ( EngineException ex ) {
				return caughtException(ex);
			}
		}
		
		StandardExecutor handler ; 
		switch ( command.commandType() ) {
		case SET:  handler = new AssignmentHandler(this) ; break ;
		case DO:   handler = new CallHandler      (this) ; break ;
		case QUIT: handler = new ReturnHandler    (this) ; break ;
		default:
			throw new UnsupportedOperationException("command type \"" + command.commandType() + "\" not supported") ;
		}
		
		return delegateExecutionTo( command, handler ) ;
	}
	
	public GlobalContext globalContext() { return m_globalContext ; }
	
	public Node findNode( VariableReference variable ) throws EngineException {
		Node node = variable.visit( new VariableReference.Visitor<Node>() {
			
			@Override public Node visitVariableReference(VariableReference variable) {
				throw new UnsupportedOperationException(variable.getClass().getSimpleName() + " not supported for assignment") ;
			}
			
			@Override public Node visitIndirectVariableReference( IndirectVariableReference variable) {
				try { return m_root.get( evaluate( variable.variableNameProducer() ).toString() ) ; }
				catch ( EngineException ex ) { caughtException(ex) ; return null ; }
			}
			
			@Override public Node visitDirectVariableReference(DirectVariableReference variable) {
				if ( Scope.GLOBAL != variable.scope() )
					throw new UnsupportedOperationException(variable.scope() + " scope not supported for direct variable assignment") ;
				return m_root.get( variable.variableName() ) ;
			}
			
			@Override public Node visitBuiltinVariableReference(BuiltinVariableReference variable) {
				return m_root.get( variable.builtinVariable().canonicalSymbol() ) ;
			}
			
			@Override public Node visitBuiltinSystemVariableReference(BuiltinSystemVariableReference variable) {
				return m_root.get( variable.builtinSystemVariable().canonicalSymbol() ) ;
			}
		});
		if ( null == node )
			throwException() ;
		for ( Expression key : variable.keys() )
			node = node.get( evaluate(key).value() ) ;
		return node ;
	}
	
	private Constant resultOrException( Constant result ) throws EngineException {
		if ( null == result  )
			throwException();
		return result ;
	}
	
	@Override
	public Constant evaluate( Expression expression ) throws EngineException {
		return resultOrException( expression.visit( new Expression.Visitor<Constant>() {

			@Override public Constant visitExpression(Expression expression) {
				throw new UnsupportedOperationException(expression.getClass().getSimpleName() + " not supported") ;
			}

			@Override public Constant visitConstant(Constant constant) {
				return constant;
			}
			
			@Override public Constant visitDirectVariableReference(DirectVariableReference variable) {
				try { return new Constant( findNode( variable ).value() ) ; }
				catch ( EngineException ex ) { caughtException(ex) ; return null ; }
			}
			
		}) ) ;
	}

}
