package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.ErrorCode;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.Element;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Assignment;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Destination;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Nothing;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinSystemVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.IndirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;

public class ExecutionFrame implements Executor, Evaluator {
	
	private final NodeMap m_root ;
	private final GlobalContext m_globalContext ;
	private final Map<String,Node> m_locals = new HashMap<String, Node>(); 

	private Constant m_result = null ;
	private EngineException m_exception = null ; 

	public ExecutionFrame( NodeMap root, GlobalContext globalContext ) {
		m_root = root ;
		m_globalContext = globalContext ;
	}
	
	@Override public Constant result() { return m_result ; }

	@Override public EngineException error() { return m_exception ; }
	
	protected ExecutionResult caughtException( EngineException ex ) {
		m_exception = ex ;
		return ExecutionResult.ERROR ;
	}
	
	protected void throwException() throws EngineException {
		if ( null == m_exception )
			throw new RuntimeException( "cannot throw EngineException; none were caught" ) ;
		throw m_exception ;
	}

	@Override
	public ExecutionResult execute( Command command ) {
		m_result = null ;
		if ( null != m_exception )
			return ExecutionResult.ERROR ;
		
		if ( null != command.condition() )
			try {
				if ( !evaluate( command.condition() ).toBoolean() )
					return ExecutionResult.CONTINUE;
			} catch ( EngineException ex ) {
				return caughtException(ex);
			}
		
		switch ( command.commandType() ) {
		case SET:
			return command.argument().visit( new ArgumentInterpreter<ExecutionResult>() {
				@Override public ExecutionResult visitAssignmentList(AssignmentList assignmentList) { return apply( assignmentList ) ; }
			} );
		case DO:
			return command.argument().visit(  new ArgumentInterpreter<ExecutionResult>() {
				@Override public ExecutionResult visitNothing(Nothing nothing) {
					return super.visitNothing(nothing) ;
				}
				@Override public ExecutionResult visitTaggedRoutineCall(TaggedRoutineCall taggedRoutineCall) {
					try { return execute( taggedRoutineCall ) ; } catch ( EngineException ex ) { return caughtException(ex); }
				}
			} );
		case QUIT:
			return command.argument().visit(  new ArgumentInterpreter<ExecutionResult>() {
				@Override public ExecutionResult visitNothing(Nothing nothing) {
					return ExecutionResult.QUIT ;
				}
				@Override public ExecutionResult visitExpressionList(ExpressionList expressionList) {
					try { m_result = evaluate( expressionList.elements() ) ; return ExecutionResult.QUIT ; } catch ( EngineException ex ) { return caughtException(ex); }
				}
			} );
		default:
			throw new UnsupportedOperationException("command type \"" + command.commandType() + "\" not supported") ;
		}
	}

	private static class ArgumentInterpreter<R> implements Argument.Visitor<R> {
		@Override public R visitArgument(Argument argument) {
			throw new UnsupportedOperationException( "argument type \"" + argument.getClass().getSimpleName() + "\" not supported" );
		}
	}
	
	private ExecutionResult execute( TaggedRoutineCall taggedRoutineCall ) throws EngineException {
		String tagName = taggedRoutineCall.tagName();
		String routineName = taggedRoutineCall.routineName();
		
		Routine routine = m_globalContext.compiledRoutine( routineName ) ;
		if ( null == routine )
			throw new EngineException(ErrorCode.MISSING_ROUTINE,"routine",routineName) ;
		
		Iterator<RoutineElement> elementIterator = routine.findTagByName( tagName == null ? routineName : tagName ) ;
		if ( !elementIterator.hasNext() )
			throw new EngineException(ErrorCode.MISSING_TAG,"tag",tagName,"routine",routineName) ;
		
		ExecutionResult result = ExecutionResult.CONTINUE ;
		while ( ExecutionResult.CONTINUE == result && elementIterator.hasNext() ) {
			RoutineElement element = elementIterator.next() ;
			if ( element instanceof Command)
				result = execute( (Command)element );
		}
		return result ;
	}
	
	private ExecutionResult apply( AssignmentList assignments ) {
		ExecutionResult result = ExecutionResult.CONTINUE ; 
		for (Assignment assignment : assignments.elements())
			if ( (result=apply( assignment )) != ExecutionResult.CONTINUE )
				break ;
		return result ;
	}
	
	private ExecutionResult apply( Assignment assignment ) {
		return assignment.destination().visit( new Destination.Visitor<ExecutionResult>() {
			
			@Override public ExecutionResult visitElement(Element element) {
				throw new UnsupportedOperationException(element.getClass().getSimpleName() + " not supported for assignment") ;
			}
			
			@Override public ExecutionResult visitVariableReference(VariableReference variable) {
				try { findNode( variable ).assign( evaluate( assignment.source() ).value() ) ; return ExecutionResult.CONTINUE ; }
				catch ( EngineException ex ) { return caughtException(ex) ; }
			}

			@Override public ExecutionResult visitBuiltinFunctionCall(BuiltinFunctionCall builtinFunctionCall) {
				throw new UnsupportedOperationException(builtinFunctionCall.getClass().getSimpleName() + " not supported for assignment") ;
			}
			
		});
	}
	
	private Node findNode( VariableReference variable ) throws EngineException {
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
	
	public Constant evaluate( Iterable<Expression> expressions ) throws EngineException {
		Constant result = null ;
		for ( Expression expression : expressions )
			result = evaluate(expression) ;
		return result ;
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
