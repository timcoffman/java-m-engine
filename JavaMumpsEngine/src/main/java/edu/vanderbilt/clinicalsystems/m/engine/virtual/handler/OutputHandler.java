package edu.vanderbilt.clinicalsystems.m.engine.virtual.handler;

import java.io.IOException;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.ErrorCode;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.CommandHandler;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.ExecutionFrame;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.CarriageReturnCommand;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ColumnCommand;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.FormatCommand;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutput;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutputList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutputVariable;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.OutputExpression;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.PageFeedCommand;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;

public class OutputHandler extends CommandHandler {

	public OutputHandler( ExecutionFrame frame ) {
		super(frame) ;
	}
	
	@Override protected ExecutionResult handle( ExpressionList expressionList, Block block ) throws EngineException {
		ExecutionResult result = ExecutionResult.CONTINUE ;
		for ( Expression expression : expressionList.elements() )
			if ( (result = write( expression )) != ExecutionResult.CONTINUE )
				break ;
		return result ;
	}
	
	@Override protected ExecutionResult handle( InputOutputList inputOutputList, Block block ) throws EngineException {
		ExecutionResult result = ExecutionResult.CONTINUE ;
		for ( InputOutput inputOutput : inputOutputList.elements() )
			if ( (result = process( inputOutput )) != ExecutionResult.CONTINUE )
				break ;
		return result ;
	}

	private ExecutionResult process( InputOutput inputOutput ) {
		return inputOutput.visit( new InputOutput.Visitor<ExecutionResult>() {

			@Override public ExecutionResult visitInputOutput(InputOutput inputOutput) {
				throw new UnsupportedOperationException( "input/output type \"" + inputOutput.getClass().getSimpleName() + "\" not supported" );
			}

			@Override public ExecutionResult visitInputOutputVariable( InputOutputVariable inputOutputVariable) {
				return readWrite( inputOutputVariable.variable() ) ;
			}

			@Override public ExecutionResult visitOutputExpression(OutputExpression outputExpression) {
				return write( outputExpression.expression()) ;
			}

			@Override public ExecutionResult visitFormatCommand(FormatCommand formatCommand) {
				return write( formatCommand.text() ) ;
			}
			
			@Override public ExecutionResult visitCarriageReturnCommand(CarriageReturnCommand carriageReturnCommand) {
				return writeCarriageReturn() ;
			}
			
			@Override public ExecutionResult visitPageFeedCommand(PageFeedCommand pageFeedCommand) {
				return writePageFeed() ;
			}
			
			@Override public ExecutionResult visitColumnCommand(ColumnCommand columnCommand) {
				return writeColumn( columnCommand.column() ) ;
			}
			
		}) ;
	}

	protected ExecutionResult write( Expression expression ) {
		try {
			return write( frame().evaluate( expression ).toConstant().value() ) ;
		} catch ( EngineException ex ) {
			return caughtError(ex) ;
		}
	}
	
	protected ExecutionResult readWrite( VariableReference variable ) {
		return write( variable ) ;
	}
	
	protected ExecutionResult write( String text ) {
		try {
			try {
				frame().inputOutputDevice().output( text ) ;
			} catch (IOException ex) {
				throw( new EngineException(ErrorCode.OUTPUT_ERROR, new String[] { "text", text }, ex) ) ;
			}
			
			return ExecutionResult.CONTINUE ;
		} catch ( EngineException ex ) {
			return caughtError(ex) ;
		}
	}
	
	protected ExecutionResult writeCarriageReturn( ) {
		try {
			try {
				frame().inputOutputDevice().outputCarriageReturn();
			} catch (IOException ex) {
				throw( new EngineException(ErrorCode.OUTPUT_ERROR, new String[] { "text", "<CR>" }, ex) ) ;
			}
			
			return ExecutionResult.CONTINUE ;
		} catch ( EngineException ex ) {
			return caughtError(ex) ;
		}
	}
	
	protected ExecutionResult writePageFeed( ) {
		try {
			try {
				frame().inputOutputDevice().outputPageFeed();
			} catch (IOException ex) {
				throw( new EngineException(ErrorCode.OUTPUT_ERROR, new String[] { "text", "<page feed>" }, ex) ) ;
			}
			
			return ExecutionResult.CONTINUE ;
		} catch ( EngineException ex ) {
			return caughtError(ex) ;
		}
	}
	
	protected ExecutionResult writeColumn( int column ) {
		try {
			try {
				frame().inputOutputDevice().outputColumnMove( column );
			} catch (IOException ex) {
				throw( new EngineException(ErrorCode.OUTPUT_ERROR, new String[] { "text", "<column #" + column + ">" }, ex) ) ;
			}
			
			return ExecutionResult.CONTINUE ;
		} catch ( EngineException ex ) {
			return caughtError(ex) ;
		}
	}
	
}
