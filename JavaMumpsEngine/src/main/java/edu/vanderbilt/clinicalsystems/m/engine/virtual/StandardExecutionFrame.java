package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.ErrorCode;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.AssignmentHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.CallHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.DeclarationHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.ExecHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.InputOutputHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.LoopHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.MergeHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.OutputHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.ReturnHandler;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BinaryOperation;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinSystemVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.IndirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.CommandParser;
import edu.vanderbilt.clinicalsystems.m.lang.text.CommandParserFactory;
import edu.vanderbilt.clinicalsystems.m.lang.text.ExpressionParser;
import edu.vanderbilt.clinicalsystems.m.lang.text.ExpressionParserFactory;

public class StandardExecutionFrame extends StandardExecutor implements ExecutionFrame {
	
	private final NodeMap m_root ;
	private final ExecutionFrame m_parent ;
	private final GlobalContext m_globalContext ;
	private final Map<String,Node> m_locals = new HashMap<String, Node>();
	private InputOutputDevice m_currentInputOutputDevice = null;

	public StandardExecutionFrame( NodeMap root, ExecutionFrame parent ) {
		this(parent,null) ;
	}
	
	public StandardExecutionFrame( NodeMap root, GlobalContext globalContext ) {
		this(globalContext,null) ;
	}
	
	public StandardExecutionFrame( ExecutionFrame parent, InputOutputDevice inputOutputDevice ) {
		Objects.requireNonNull(parent); 
		m_parent = parent ;
		m_globalContext = m_parent.globalContext() ;
		m_root = m_parent.root() ;
		m_currentInputOutputDevice = inputOutputDevice ;
	}
	
	public StandardExecutionFrame( GlobalContext globalContext, InputOutputDevice inputOutputDevice ) {
		Objects.requireNonNull(globalContext); 
		m_parent = null ;
		m_globalContext = globalContext ;
		m_root = m_globalContext.root() ;
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
	
	@Override public NodeMap root() { return m_root ; }
	
	@Override public ExecutionFrame parentFrame() { return m_parent ; }
	
	@Override
	public ExecutionFrame createChildFrame() {
		return new StandardExecutionFrame(this, m_currentInputOutputDevice) ;
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
		case NEW:     handler = new DeclarationHandler(this) ; break ;
		case SET:     handler = new AssignmentHandler (this) ; break ;
		case MERGE:   handler = new MergeHandler      (this) ; break ;
		case DO:      handler = new CallHandler       (this) ; break ;
		case FOR:     handler = new LoopHandler       (this) ; break ;
		case EXECUTE: handler = new ExecHandler       (this) ; break ;
		case QUIT:    handler = new ReturnHandler     (this) ; break ;
		case READ:    handler = new InputOutputHandler(this) ; break ;
		case WRITE:   handler = new OutputHandler     (this) ; break ;
		default:
			throw new UnsupportedOperationException("command type \"" + command.commandType() + "\" not supported") ;
		}
		
		return delegateExecutionTo( command, handler ) ;
	}
	
	@Override
	public GlobalContext globalContext() { return m_globalContext ; }
	
	private Node clearNode( Node node ) {
		node.assign(null);
		if ( !node.isEmpty() )
			for ( String key = node.firstKey() ; key != null ; key = node.keyFollowing(key) )
				clearNode( node.at(key) ) ;
		return node ;
	}
	
	@Override
	public Node createLocalNode( String variableName ) {
		Node node = m_locals.get( variableName ) ;
		if ( null == node )
			m_locals.put( variableName, node=new StandardNode() ) ;
		return clearNode(node) ;
	}
	
	@Override
	public Node createNode( DirectVariableReference variable ) {
		String variableName = variable.variableName();
		switch ( variable.scope() ) {
		case GLOBAL:
			return m_root.create( variableName ) ;
		case LOCAL:
			return createLocalNode( variableName ) ;
		default:
			throw new UnsupportedOperationException(variable.scope() + " scope not supported for direct variable creation") ;
		}
	}
	
	@Override public boolean hasLocalNode( String variableName ) { return m_locals.containsKey( variableName ) ; }
	@Override public Node findLocalNode( String variableName ) { return m_locals.get( variableName ) ; }

	@Override
	public Node findNode( VariableReference variable ) throws EngineException {
		Node node = variable.visit( new VariableReference.Visitor<Node>() {
			
			@Override public Node visitVariableReference(VariableReference variable) {
				throw new UnsupportedOperationException(variable.getClass().getSimpleName() + " not supported for assignment") ;
			}
			
			@Override public Node visitIndirectVariableReference( IndirectVariableReference variable) {
				try {
					String variableName = evaluate( variable.variableNameProducer() ).toString();
					return interpretExpression( variableName ).visit( new Expression.Visitor<Node>() {

						@Override public Node visitExpression(Expression expression) {
							caughtException( new EngineException(ErrorCode.SYNTAX_ERROR,"code",variableName) );
							return null ;
						}
						
						@Override
						public Node visitVariableReference( VariableReference variable) {
							try { return findNode( variable ); }
							catch ( EngineException ex ) { caughtException(ex) ; return null ; }
						}
					}) ;
				} catch ( EngineException ex ) { caughtException(ex) ; return null ; }
			}
			
			@Override public Node visitDirectVariableReference(DirectVariableReference variable) {
				String variableName = variable.variableName();
				switch ( variable.scope() ) {
				case GLOBAL:
					return m_root.get( variableName ) ;
				case LOCAL:
					if ( hasLocalNode( variableName) )
						return findLocalNode( variableName ) ;
					else if ( null == m_parent )
						return createLocalNode( variableName ) ;
					else
						return m_parent.findLocalNode( variableName ) ;
				default:
					throw new UnsupportedOperationException(variable.scope() + " scope not supported for direct variable assignment") ;
				}
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
	
	@Override
	public InputOutputDevice inputOutputDevice() {
		return m_currentInputOutputDevice ;
	}
	
	private Constant resultOrException( Constant result ) throws EngineException {
		if ( null == result  )
			throwException();
		return result ;
	}
	
	@Override
	public List<? extends Command> interpretCommands( String unparsedCode ) throws EngineException {
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
	
	public Expression interpretExpression( String unparsedCode ) throws EngineException {
		ServiceLoader<ExpressionParserFactory> serviceLoader = ServiceLoader.load( ExpressionParserFactory.class ) ;
		ExpressionParserFactory expressionParserFactory = serviceLoader.iterator().next() ;
		ExpressionParser expressionParser = expressionParserFactory.createExpressionParser() ;
		Reader reader = new StringReader(unparsedCode);
		
		try {
			return expressionParser.parseExpression( reader );
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
			
			private double requireDouble(Constant c) throws EngineException {
				try { return c.toDouble() ; }
				catch ( NumberFormatException ex ) { throw new EngineException(ErrorCode.NOT_A_NUMBER, "text", c.toString() ) ; } 
			}
			
			@Override public Constant visitBinaryOperation( BinaryOperation operation ) {
				try { 
				Constant lhs = evaluate( operation.leftHandSide()  ) ;
				Constant rhs = evaluate( operation.rightHandSide() ) ;
				switch ( operation.operator() ) {
					case ADD:
						return Constant.from( requireDouble(lhs) + requireDouble(rhs) ) ;
					case SUBTRACT:
						return Constant.from( requireDouble(lhs) + requireDouble(rhs) ) ;
					case MULTIPLY:
						return Constant.from( requireDouble(lhs) + requireDouble(rhs) ) ;
					case DIVIDE:
						return Constant.from( requireDouble(lhs) + requireDouble(rhs) ) ;
					case EQUALS:
						return Constant.from( lhs.equals(rhs) ) ;
					default:
						throw new EngineException(ErrorCode.UNSUPPORTED_FEATURE, "code", operation.operator().canonicalSymbol(), "feature", operation.operator().getClass().getSimpleName() ) ;
				}
				} catch ( EngineException ex ) { caughtException(ex) ; return null ; }
			}
						
			@Override public Constant visitBuiltinFunctionCall( BuiltinFunctionCall functionCall ) {
				FunctionHandler handler ;
				try { 
				switch ( functionCall.builtinFunction() ) {
				case ORDER:
					handler = new OrderFunctionHandler() ;
					break ;
				default:
					throw new EngineException(ErrorCode.UNSUPPORTED_FEATURE, "code", functionCall.builtinFunction().canonicalSymbol(), "feature", functionCall.builtinFunction().getClass().getSimpleName() ) ;
				}
				return handler.call( functionCall.arguments() );
				} catch ( EngineException ex ) { caughtException(ex) ; return null ; }
			}

			
		}) ) ;
	}

	private interface FunctionHandler {
		Constant call( Iterable<Expression> arguments ) throws EngineException ;
	}

	private class OrderFunctionHandler implements FunctionHandler {

		@Override public Constant call(Iterable<Expression> arguments) throws EngineException {
			Iterator<Expression> args = arguments.iterator();
			VariableReference subscriptedVar = (VariableReference)args.next();
			boolean forward = args.hasNext() ? evaluate(args.next()).representsNumber(1) : true ;

			if ( !subscriptedVar.keys().iterator().hasNext() )
				throw new EngineException(ErrorCode.SYNTAX_ERROR, "code", subscriptedVar.toString() ) ;

			Node node = findNode( subscriptedVar.parent() ) ;
			String key = evaluate( subscriptedVar.finalKey() ).value() ;
			String resultKey = forward ? node.keyFollowing(key) : node.keyPreceding(key) ;
			return Constant.from( resultKey );
		}
		
	}
}
