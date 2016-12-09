package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.util.Iterator;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.DeclarationList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutputList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.LoopDefinition;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Nothing;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCallList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.VariableList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;

public abstract class CommandHandler extends StandardExecutor {

	private final ExecutionFrame m_frame ;
	
	public CommandHandler( ExecutionFrame frame ) {
		m_frame = frame ;
	}
	
	protected ExecutionFrame frame() { return m_frame ; }
	
	@Override public ExecutionResult execute( Command command ) {
		m_result = null ;
		if ( null != m_exception )
			return ExecutionResult.ERROR ;

		return command.argument().visit( new Argument.Visitor<ExecutionResult>() {
			@Override public ExecutionResult visitArgument(Argument argument) { return handle(argument,command.block()) ; }
			@Override public ExecutionResult visitNothing              (Nothing               nothing              ) { try { return handle(nothing              ,command.block()) ; } catch ( EngineException ex ) { return caughtException(ex); } }
			@Override public ExecutionResult visitLoopDefinition       (LoopDefinition        loopDefinition       ) { try { return handle(loopDefinition       ,command.block()) ; } catch ( EngineException ex ) { return caughtException(ex); } }
			@Override public ExecutionResult visitTaggedRoutineCallList(TaggedRoutineCallList taggedRoutineCallList) { try { return handle(taggedRoutineCallList,command.block()) ; } catch ( EngineException ex ) { return caughtException(ex); } }
			@Override public ExecutionResult visitAssignmentList       (AssignmentList        assignmentList       ) { try { return handle(assignmentList       ,command.block()) ; } catch ( EngineException ex ) { return caughtException(ex); } }
			@Override public ExecutionResult visitDeclarationList      (DeclarationList       declarationList      ) { try { return handle(declarationList      ,command.block()) ; } catch ( EngineException ex ) { return caughtException(ex); } }
			@Override public ExecutionResult visitVariableList         (VariableList          variableList         ) { try { return handle(variableList         ,command.block()) ; } catch ( EngineException ex ) { return caughtException(ex); } }
			@Override public ExecutionResult visitExpressionList       (ExpressionList        expressionList       ) { try { return handle(expressionList       ,command.block()) ; } catch ( EngineException ex ) { return caughtException(ex); } }
			@Override public ExecutionResult visitInputOutputList      (InputOutputList       inputOutputList      ) { try { return handle(inputOutputList      ,command.block()) ; } catch ( EngineException ex ) { return caughtException(ex); } }
		} );
	}

	protected ExecutionResult handle( Argument argument, Block block ) {
		throw new UnsupportedOperationException( "argument type \"" + argument.getClass().getSimpleName() + "\" not supported" );
	}
	
	protected ExecutionResult handle( Nothing               nothing              , Block block ) throws EngineException { return handle( (Argument)nothing              , block ) ; }
	protected ExecutionResult handle( LoopDefinition        loopDefinition       , Block block ) throws EngineException { return handle( (Argument)loopDefinition       , block ) ; }
	protected ExecutionResult handle( TaggedRoutineCallList taggedRoutineCallList, Block block ) throws EngineException { return handle( (Argument)taggedRoutineCallList, block ) ; }
	protected ExecutionResult handle( AssignmentList        assignmentList       , Block block ) throws EngineException { return handle( (Argument)assignmentList       , block ) ; }
	protected ExecutionResult handle( DeclarationList       declarationList      , Block block ) throws EngineException { return handle( (Argument)declarationList      , block ) ; }
	protected ExecutionResult handle( VariableList          variableList         , Block block ) throws EngineException { return handle( (Argument)variableList         , block ) ; }
	protected ExecutionResult handle( ExpressionList        expressionList       , Block block ) throws EngineException { return handle( (Argument)expressionList       , block ) ; }
	protected ExecutionResult handle( InputOutputList       inputOutputList      , Block block ) throws EngineException { return handle( (Argument)inputOutputList      , block ) ; }
	
	protected ExecutionResult executeElementsIn( Iterator<RoutineElement> elementIterator, ExecutionFrame frame ) throws EngineException {
			ExecutionResult result = ExecutionResult.CONTINUE ;
			while ( ExecutionResult.CONTINUE == result && elementIterator.hasNext() ) {
				RoutineElement element = elementIterator.next() ;
				if ( element instanceof Command )
					result = delegateExecutionTo( (Command)element, frame ) ;
			}
			
			return result ;
	}
	
	protected Constant evaluate( Iterable<Expression> expressions ) throws EngineException {
		Constant result = null ;
		for ( Expression expression : expressions )
			result = evaluate(expression) ;
		return result ;
	}
	
	protected Constant evaluate( Expression expression ) throws EngineException {
		return m_frame.evaluate(expression) ;
	}
	
}
