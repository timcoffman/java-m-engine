package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.ErrorCode;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.Installer.TargetInstanceResolver;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.AssignmentHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.CallHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.DeclarationHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.ExecHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.InputOutputHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.LoopHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.MergeHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.OutputHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.handler.ReturnHandler;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction;
import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BinaryOperation;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinSystemVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.IndirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.MatchPattern;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.TagReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.UnaryOperation;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.CommandParser;
import edu.vanderbilt.clinicalsystems.m.lang.text.CommandParserFactory;
import edu.vanderbilt.clinicalsystems.m.lang.text.ExpressionParser;
import edu.vanderbilt.clinicalsystems.m.lang.text.ExpressionParserFactory;

public class StandardExecutionFrame extends StandardExecutor implements ExecutionFrame {
	
	private Map<String,Object> m_properties = null ;
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
		m_currentInputOutputDevice = inputOutputDevice ;
	}
	
	public StandardExecutionFrame( GlobalContext globalContext, InputOutputDevice inputOutputDevice ) {
		Objects.requireNonNull(globalContext); 
		m_parent = null ;
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
	
	@Override public NodeMap root( Scope scope ) { return m_globalContext.root(scope) ; }
	
	@Override public ExecutionFrame parentFrame() { return m_parent ; }

	
	private Map<String,Object> requireProperties() {
		if ( null == m_properties )
			m_properties = new HashMap<String, Object>() ;
		return m_properties ;
	}
	
	@Override
	public void setLocalProperty(String name, Object value) {
		Objects.requireNonNull(value) ;
		requireProperties().put( name, value ) ;
	}
	
	@Override
	public <T> T getProperty(String name, Class<T> ofType) {
		if ( hasLocalProperty(name) )
			return getLocalProperty( name, ofType );
		else if ( null != m_parent )
			return m_parent.getProperty( name, ofType ) ;
		else
			return null ;
	}
	
	@Override
	public <T> T getLocalProperty(String name, Class<T> ofType) {
		if ( hasLocalProperty(name) )
			return ofType.cast( m_properties.get(name) );
		else
			return null ;
	}
	
	@Override
	public boolean hasLocalProperty(String name ) {
		return null != m_properties && m_properties.containsKey(name) ;
	}
	
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
				if ( !evaluate( command.condition() ).toConstant().toBoolean() )
					return ExecutionResult.CONTINUE;
			} catch ( EngineException ex ) {
				return caughtError(ex);
			}
		}
		
		Executor handler ; 
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
	public Node insertLocalNode( String variableName, Node node ) {
		m_locals.put( variableName, node ) ;
		return node ;
	}
	
	@Override
	public Node createNode( DirectVariableReference variable ) {
		String variableName = variable.variableName();
		switch ( variable.scope() ) {
		case PERSISTENT:
			return root(variable.scope()).create( variableName ) ; // unsure if this is correct
		case TRANSIENT:
			return createLocalNode( variableName ) ; // unsure if this is correct
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
				throw new UnsupportedOperationException(variable.getClass().getSimpleName() + " not supported for node lookup") ;
			}
			
			@Override public Node visitIndirectVariableReference( IndirectVariableReference variable) {
				try {
					String variableName = evaluate( variable.variableNameProducer() ).toConstant().value();
					return interpretExpression( variableName ).visit( new Expression.Visitor<Node>() {

						@Override public Node visitExpression(Expression expression) {
							caughtError( new EngineException(ErrorCode.SYNTAX_ERROR,"code",variableName) );
							return null ;
						}
						
						@Override
						public Node visitVariableReference( VariableReference variable) {
							try { return findNode( variable ); }
							catch ( EngineException ex ) { caughtError(ex) ; return null ; }
						}
					}) ;
				} catch ( EngineException ex ) { caughtError(ex) ; return null ; }
			}
			
			@Override public Node visitDirectVariableReference(DirectVariableReference variable) {
				String variableName = variable.variableName();
				switch ( variable.scope() ) {
				case PERSISTENT:
					return root(variable.scope()).get( variableName ) ;  // unsure if this is correct
				case TRANSIENT: // unsure if this is correct
					if ( hasLocalNode( variableName) ) {
						return findLocalNode( variableName ) ;
					} else if ( null != m_parent ) {
						try {
							return m_parent.findNode( variable ) ;
						} catch ( EngineException ex ) { caughtError(ex) ; return null ; }
					} else {
						return m_globalContext.root(variable.scope()).get( variableName );
					}
				default:
					throw new UnsupportedOperationException(variable.scope() + " scope not supported for direct variable assignment") ;
				}
			}
			
			@Override public Node visitBuiltinVariableReference(BuiltinVariableReference variable) {
				return m_globalContext.root(Scope.TRANSIENT).get( variable.builtinVariable().canonicalSymbol() ) ;
			}
			
			@Override public Node visitBuiltinSystemVariableReference(BuiltinSystemVariableReference variable) {
				return m_globalContext.root(Scope.PERSISTENT).get( variable.builtinSystemVariable().canonicalSymbol() ) ;
			}
		});
		if ( null == node )
			throwException() ;
		for ( Expression key : variable.keys() )
			node = node.get( evaluate(key).toConstant().value() ) ;
		return node ;
	}
	
	@Override
	public InputOutputDevice inputOutputDevice() {
		return m_currentInputOutputDevice ;
	}
	
	private EvaluationResult resultOrException( EvaluationResult result ) throws EngineException {
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
	
	public MatchPattern interpretMatchPattern( String unparsedCode ) throws EngineException {
		ServiceLoader<ExpressionParserFactory> serviceLoader = ServiceLoader.load( ExpressionParserFactory.class ) ;
		ExpressionParserFactory expressionParserFactory = serviceLoader.iterator().next() ;
		ExpressionParser expressionParser = expressionParserFactory.createExpressionParser() ;
		Reader reader = new StringReader(unparsedCode);
		
		try {
			return expressionParser.parseMatchPattern( reader );
		} catch (IOException e) {
			throw new EngineException(ErrorCode.CANNOT_PARSE,"code",unparsedCode);
		}
	}
	
	@Override
	public EvaluationResult evaluate( Expression expression ) throws EngineException {
		return resultOrException( expression.visit( new Expression.Visitor<EvaluationResult>() {

			@Override public EvaluationResult visitExpression(Expression expression) {
				throw new UnsupportedOperationException(expression.getClass().getSimpleName() + " not supported") ;
			}

			@Override public EvaluationResult visitConstant(Constant constant) {
				return EvaluationResult.fromConstant(constant);
			}
			
			@Override public EvaluationResult visitVariableReference(VariableReference variable) {
				try { return EvaluationResult.fromConstant( Constant.from( findNode( variable ).value() ) ) ;
				} catch ( EngineException ex ) { caughtError(ex) ; return null ; }
			}
			
			@Override
			public EvaluationResult visitDirectVariableReference( DirectVariableReference variable) {
				switch ( variable.parameterPassMethod() ) {
				case BY_REFERENCE:
					try { return EvaluationResult.fromNode( findNode( variable ) ) ;
					} catch ( EngineException ex ) { caughtError(ex) ; return null ; }
				case BY_VALUE:
				default:
					return visitVariableReference(variable);
				}
			}

			private double requireDouble(Constant c) throws EngineException {
				try { return c.toDouble() ; }
				catch ( NumberFormatException ex ) { throw new EngineException(ErrorCode.NOT_A_NUMBER, "text", c.toString() ) ; } 
			}
			
			private boolean requireBoolean(Constant c) throws EngineException {
				try { return c.toBoolean() ; }
				catch ( NumberFormatException ex ) { throw new EngineException(ErrorCode.NOT_A_BOOLEAN, "text", c.toString() ) ; } 
			}
			
			private Constant evaluateBinaryOperation( OperatorType operatorType, Constant lhs, Constant rhs ) throws EngineException {
				switch ( operatorType ) {
				case CONCAT:
					return Constant.from( lhs.value() + rhs.value() ) ;
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
				case NOT_EQUALS:
					return Constant.from( !lhs.equals(rhs) ) ;
				case GREATER_THAN:
					return Constant.from( lhs.compareTo(rhs) > 0 ) ;
				case NOT_GREATER_THAN:
					return Constant.from( lhs.compareTo(rhs) <= 0 ) ;
				case LESS_THAN:
					return Constant.from( lhs.compareTo(rhs) < 0 ) ;
				case NOT_LESS_THAN:
					return Constant.from( lhs.compareTo(rhs) >= 0 ) ;
				default:
					throw new EngineException(ErrorCode.UNSUPPORTED_FEATURE, "code", operatorType.canonicalSymbol(), "feature", operatorType.getClass().getSimpleName() ) ;
				}
			}
			
			private Constant evaluateBinaryMatchOperation( OperatorType operatorType, Constant lhs, Expression rightHandSide ) throws EngineException {
				MatchPattern matchPattern = rightHandSide.visit( new Expression.Visitor<MatchPattern>() {

					@Override public MatchPattern visitExpression(Expression expression) {
						try {
							return interpretMatchPattern( evaluate(expression).toConstant().value() ) ;
						} catch ( EngineException ex ) { caughtError(ex) ; return null ; }
					}
					
					@Override public MatchPattern visitMatchPattern( MatchPattern matchPattern ) {
						return matchPattern ;
					}

				}) ;
				if ( null == matchPattern )
					throw new EngineException(ErrorCode.SYNTAX_ERROR, "code", rightHandSide.toString() );
				
				return Constant.from( new PatternMatcher(matchPattern).matches(lhs) ); 
			}
			
			@Override public EvaluationResult visitBinaryOperation( BinaryOperation operation ) {
				try { 
					Constant lhs = evaluate( operation.leftHandSide()  ).toConstant() ;
					if ( operation.operator() == OperatorType.MATCH )
						return EvaluationResult.fromConstant( evaluateBinaryMatchOperation(operation.operator(), lhs, operation.rightHandSide() )) ;
					Constant rhs = evaluate( operation.rightHandSide() ).toConstant() ;
					return EvaluationResult.fromConstant( evaluateBinaryOperation(operation.operator(), lhs, rhs)) ;
				} catch ( EngineException ex ) { caughtError(ex) ; return null ; }
			}

			private Constant evaluateUnaryOperation( OperatorType operatorType, Constant operand ) throws EngineException {
				switch ( operatorType ) {
				case ADD:
					return operand;
				case SUBTRACT:
					return Constant.from( - requireDouble(operand) ) ;
				case NOT:
					return Constant.from( ! requireBoolean(operand) ) ;
				default:
					throw new EngineException(ErrorCode.UNSUPPORTED_FEATURE, "code", operatorType.canonicalSymbol(), "feature", operatorType.getClass().getSimpleName() ) ;
				}
			}
			
			@Override public EvaluationResult visitUnaryOperation( UnaryOperation operation ) {
				try { 
					Constant operand = evaluate( operation.operand() ).toConstant();
					return EvaluationResult.fromConstant( evaluateUnaryOperation(operation.operator(), operand) ) ;
				} catch ( EngineException ex ) { caughtError(ex) ; return null ; }
			}
			
			@Override public EvaluationResult visitRoutineFunctionCall( RoutineFunctionCall functionCall ) {
				Long argumentCount = StreamSupport.stream(functionCall.arguments().spliterator(),false).collect(Collectors.counting());
				try {
					FunctionHandler handler = functionHandlerForTagReference( functionCall.tagReference(), argumentCount.intValue() );
					return handler.call( functionCall.arguments() ) ;
				} catch (EngineException ex) { caughtError(ex) ; return null ; }
			}
			
			@Override public EvaluationResult visitBuiltinFunctionCall( BuiltinFunctionCall functionCall ) {
				FunctionHandler handler ;
				try { 
				switch ( functionCall.builtinFunction() ) {
				case ORDER:
					handler = new OrderFunctionHandler() ;
					break ;
				case LENGTH:
					handler = new LengthFunctionHandler() ;
					break ;
				case PIECE:
					handler = new PieceFunctionHandler() ;
					break ;
				default:
					throw new EngineException(ErrorCode.UNSUPPORTED_FEATURE, "code", functionCall.builtinFunction().canonicalSymbol(), "feature", functionCall.builtinFunction().getClass().getSimpleName() ) ;
				}
				return handler.call( functionCall.arguments() );
				} catch ( EngineException ex ) { caughtError(ex) ; return null ; }
			}

			
		}) ) ;
	}

	private interface FunctionHandler {
		EvaluationResult call( Iterable<Expression> arguments ) throws EngineException ;
	}

	private class OrderFunctionHandler implements FunctionHandler {

		@Override public EvaluationResult call(Iterable<Expression> arguments) throws EngineException {
			Iterator<Expression> args = arguments.iterator();
			VariableReference subscriptedVar = (VariableReference)args.next();
			boolean forward = args.hasNext() ? evaluate(args.next()).toConstant().representsNumber(1) : true ;

			VariableReference parent = subscriptedVar.parent()
					.orElseThrow( ()->new EngineException(ErrorCode.SYNTAX_ERROR, "code", subscriptedVar.toString() ) ) ;
			Expression finalKey = subscriptedVar.finalKey()
					.orElseThrow( ()->new EngineException(ErrorCode.SYNTAX_ERROR, "code", subscriptedVar.toString() ) ) ;

			Node node = findNode( parent ) ;
			String key = evaluate( finalKey ).toConstant().value() ;
			String resultKey ;
			if ( node.isEmpty() ) {
				resultKey = "" ;
			} else if ( forward ) {
				resultKey = node.keyFollowing(key) ;
			} else {
				resultKey = node.keyPreceding(key) ;
			}
			return EvaluationResult.fromConstant( Constant.from( resultKey ) );
		}
		
	}
	
	private class LengthFunctionHandler implements FunctionHandler {
		
		@Override public EvaluationResult call(Iterable<Expression> arguments) throws EngineException {
			Iterator<Expression> args = arguments.iterator();
			String searched = evaluate(args.next()).toConstant().value() ;
			if ( args.hasNext() ) {
				/* two arguments */
				String sought = evaluate(args.next()).toConstant().value() ;
				int count = numberOfOccurrances( searched, sought ) ;
				return EvaluationResult.fromConstant( Constant.from( count ) ) ;
			} else {
				/* one argument */
				int length = stringLength( searched ) ;
				return EvaluationResult.fromConstant( Constant.from( length ) ) ;
			}
		}
	
		private int stringLength( String searched ) {
			return searched.length() ;
		}
		
		private int numberOfOccurrances( String searched, String sought ) {
			int count = 0 ;
			if ( !searched.isEmpty() ) {
				int position = 0 ;
				while ( position >= 0 ) {
					++count ;
					position = searched.indexOf(sought, position) ;
					if ( position >= 0 )
						position+=sought.length();
				}
			}
			return count ;
		}
		
	}
	
	private class PieceFunctionHandler implements FunctionHandler {
		
		@Override public EvaluationResult call(Iterable<Expression> arguments) throws EngineException {
			Iterator<Expression> args = arguments.iterator();
			String searched = evaluate(args.next()).toConstant().value() ;
			String sought = evaluate(args.next()).toConstant().value() ;
			long start = 1 ;
			long stop = 1 ;
			if ( args.hasNext() ) {
				start = evaluate(args.next()).toConstant().toLong() ;
				if ( args.hasNext() ) {
					stop = evaluate(args.next()).toConstant().toLong() ;
				} else {
					stop = start ;
				}
			}
			if ( sought.isEmpty() && stop-start > 0 )
				throw new EngineException( ErrorCode.ILLEGAL_ARGUMENT, "function", BuiltinFunction.PIECE.canonicalSymbol(), "argument", "delimiter", "value", sought ) ;
			
			String substring = nthSubstring( searched, sought, start-1, stop-1 );
			
			return EvaluationResult.fromConstant( Constant.from( substring ) ) ;
		}
	
		private String nthSubstring( String searched, String sought, long start, long stop ) {
			if ( stop < start )
				return "" ;
			
			/*         "ABC::DEF::GHI"
			 *          |  | |  | |  |
			 * start ...0  | 1  | 2  3...
			 *          |  |    |    |
			 * stop ...-1  0    1    2... 
			 */
			
			int startPosition = 0 ;
			int stopPosition = 0 ;
			int count = 0 ;
			while ( count <= stop ) {
				stopPosition = searched.indexOf( sought, stopPosition + sought.length() ) ;
				if ( stopPosition < 0 ) {
					stopPosition = searched.length() ;
					if ( count < start )
						startPosition = stopPosition ;
					break ;
				}
				if ( count < start )
					startPosition = stopPosition + sought.length() ;
				++count ;
			}
			
			return searched.substring(startPosition, stopPosition) ;
		}
	}
	
	private FunctionHandler functionHandlerForTagReference( TagReference tagRef, int argumentCount ) throws EngineException {
		CompiledRoutine compiledRoutine = globalContext().compiledRoutine( tagRef.routineName() );
		if ( null == compiledRoutine )
			throw new EngineException(ErrorCode.MISSING_ROUTINE, "routine", tagRef.routineName() ) ;
		
		CompiledTag compiledTag = compiledRoutine.compiledTag( tagRef.tagName(), argumentCount ) ;
		if ( null == compiledTag )
			throw new EngineException(ErrorCode.MISSING_TAG, "routine", compiledRoutine.name(), "tag", tagRef.tagName() ) ;
		
		return new TaggedRoutineFunctionHandler( compiledTag ) ;
	}
	
	private class TaggedRoutineFunctionHandler implements FunctionHandler {

		private final CompiledTag m_compiledTag;

		public TaggedRoutineFunctionHandler(CompiledTag compiledRoutine) {
			m_compiledTag = compiledRoutine ;
		}

		@Override
		public EvaluationResult call(Iterable<Expression> arguments) throws EngineException {
			List<EvaluationResult> argumentValues = new ArrayList<EvaluationResult>() ;
			for ( Expression expression : arguments )
				argumentValues.add( evaluate(expression) ) ;
			
			TargetInstanceResolver instanceResolver = getProperty("target-instance-resolver",TargetInstanceResolver.class) ;
			Object targetInstance = instanceResolver.resolve( null, m_compiledTag.compiledRoutine().name(), StandardExecutionFrame.this ) ;
			
			try ( ExecutionFrame callFrame = createChildFrame() ) {
				if ( null != targetInstance )
					callFrame.setLocalProperty("target-instance", targetInstance);
				
				ExecutionResult executionResult = m_compiledTag.execute(callFrame,argumentValues);
				switch ( executionResult ) {
				case ERROR:
					throw callFrame.error() ;
				case QUIT:
					EvaluationResult result = callFrame.result();
					if (null == result )
						throw new EngineException( ErrorCode.FUNCTION_DID_NOT_RETURN_VALUE, "tag", m_compiledTag.name(), "routine", m_compiledTag.compiledRoutine().name() ) ;
					return result ;
				default:
					throw new IllegalStateException( "function call cannot result in " + executionResult ) ;
				}
			}
		}
		
	}
}
