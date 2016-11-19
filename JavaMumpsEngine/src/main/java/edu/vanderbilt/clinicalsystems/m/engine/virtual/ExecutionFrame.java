package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.ErrorCode;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.AssignmentHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.CallHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.ExecHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.InputOutputHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.OutputHandler;
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
import edu.vanderbilt.clinicalsystems.m.lang.text.CommandParser;
import edu.vanderbilt.clinicalsystems.m.lang.text.CommandParserFactory;

public class ExecutionFrame extends StandardExecutor implements Executor, Evaluator, AutoCloseable {
	
	private final NodeMap m_root ;
	private final GlobalContext m_globalContext ;
	private final Map<String,Node> m_locals = new HashMap<String, Node>();
	private InputOutputDevice m_currentInputOutputDevice = null;

	public ExecutionFrame( NodeMap root, GlobalContext globalContext ) {
		m_root = root ;
		m_globalContext = globalContext ;
	}
	
	public ExecutionFrame( NodeMap root, GlobalContext globalContext, InputOutputDevice inputOutputDevice ) {
		m_root = root ;
		m_globalContext = globalContext ;
		m_currentInputOutputDevice = inputOutputDevice ;
	}
	
	@Override public void close() {
		/* nothing yet */
	}
	
	public InputOutputDevice detachInputOutputDevice() {
		return attachInputOutputDevice(null) ;
	}
	
	public InputOutputDevice attachInputOutputDevice( InputOutputDevice inputOutputDevice ) {
		InputOutputDevice detachedInputOutputDevice = m_currentInputOutputDevice ;
		m_currentInputOutputDevice = inputOutputDevice ;
		return detachedInputOutputDevice ;
	}
	
	public ExecutionFrame createFrame() {
		return new ExecutionFrame(m_root, m_globalContext, m_currentInputOutputDevice) ;
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
		case SET:     handler = new AssignmentHandler (this) ; break ;
		case DO:      handler = new CallHandler       (this) ; break ;
		case EXECUTE: handler = new ExecHandler       (this) ; break ;
		case QUIT:    handler = new ReturnHandler     (this) ; break ;
		case READ:    handler = new InputOutputHandler(this) ; break ;
		case WRITE:   handler = new OutputHandler     (this) ; break ;
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

	public InputOutputDevice inputOutputDevice() {
		return m_currentInputOutputDevice ;
	}
	
	private Constant resultOrException( Constant result ) throws EngineException {
		if ( null == result  )
			throwException();
		return result ;
	}
	
	public List<? extends Command> interpret( String unparsedCode ) throws EngineException {
		ServiceLoader<CommandParserFactory> serviceLoader = ServiceLoader.load( CommandParserFactory.class ) ;
		CommandParserFactory commandParserFactory = serviceLoader.iterator().next() ;
		CommandParser commandParser = commandParserFactory.createCommandParser() ;
		Reader reader = new StringReader(unparsedCode);
		
		try {
			return commandParser.parseCommandSequence( reader );
		} catch (IOException e) {
			throw new EngineException(ErrorCode.CANNOT_PARSE,"code",unparsedCode);
		}
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
				try { return Constant.from( findNode( variable ).value() ) ; }
				catch ( EngineException ex ) { caughtException(ex) ; return null ; }
			}
			
		}) ) ;
	}

}
