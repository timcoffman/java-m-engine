package edu.vanderbilt.clinicalsystems.m.lang.text;

import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.BOOLEAN;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JConditional;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.Comment;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.Tag;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.text.statement.ExecBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.statement.ForLoopBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.statement.GenericBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.statement.IfElseBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.statement.ParseBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.statement.ReturnBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.statement.VariableBuilder;

public class RoutineJavaBlockBuilder extends RoutineJavaBuilder<RoutineJavaBuilderClassContext> {
	private final SymbolUsage m_outerSymbolUsage ; 
	
	public RoutineJavaBlockBuilder( RoutineJavaBuilderClassContext builderContext, SymbolUsage outerSymbolUsage ) {
		super(builderContext) ;
		m_outerSymbolUsage = outerSymbolUsage ;
	}

	public Builder<JBlock> analyze( Iterator<RoutineElement> elementIterator ) {
		SymbolUsage symbolUsage = new SymbolUsage(m_outerSymbolUsage) ;
		
		RoutineJavaExpressionBuilder expressionBuilder = new RoutineJavaExpressionBuilder( context(), symbolUsage ) ;

		List<Builder<JBlock>> elementBuilders = new ArrayList<RoutineJavaBuilder.Builder<JBlock>>();
		
		while ( elementIterator.hasNext() ) {
			RoutineElement element = elementIterator.next();
			
			if ( element instanceof Tag ) {
				
				elementBuilders.add( analyzeTag( (Tag)element ) );
				
			} else if ( element instanceof Comment ) {
				
				elementBuilders.add( analyzeComment( (Comment)element ) );
				
			} else if ( element instanceof Command ) {
				
				elementBuilders.add( analyzeCommand( symbolUsage, expressionBuilder, (Command)element ) );
				
			} else {
				
				/* no comment */
				
			}
			
			if ( endOfMethod( element ) )
				break ;
		}
		
		return (b)->build( symbolUsage, expressionBuilder, elementBuilders, b ) ;
	}
	
	private void build( SymbolUsage symbolUsage, RoutineJavaExpressionBuilder expressionBuilder, List<Builder<JBlock>> elementBuilders, JBlock block ) {
		System.out.println( "") ;
		System.out.println( "---- (...) {") ;
		for ( String symbol : symbolUsage.symbols() ) {
			System.out.println( "\t\"" + symbol + "\": " + symbolUsage.describe(symbol) ) ;
		}
		System.out.println( "} /* ---- */") ;
		
		for ( Builder<JBlock> elementBuilder : elementBuilders ) {
			elementBuilder.build(block);
		}

	}
	
	public Builder<JBlock> analyzeTag( Tag tag ) {
		/* no analysis */
		return (b)->buildTag( tag, b ) ;
	}	

	private void buildTag( Tag tag, JBlock block ) {
		if ( !tag.parameterNames().iterator().hasNext() )
			block.label( context().symbolForIdentifier(tag.name()) );
	}	
	
	public Builder<JBlock> analyzeComment( Comment comment ) {
		/* no analysis */
		return (b)->buildComment( comment, b ) ;
	}	
	
	private void buildComment( Comment comment, JBlock block ) {
		/* no comment */
	}	
	
	public Builder<JBlock> analyzeCommand( SymbolUsage symbolUsage, RoutineJavaExpressionBuilder expressionBuilder, Command command ) {
		Builder<JBlock> blockBuilder = analyzeCommand( symbolUsage, expressionBuilder, command.commandType(), command.argument(), command.block() ) ;
		
		Expression condition = command.condition();
		if ( null != condition ) {
			RoutineJavaBlockBuilder conditionalBlockBuilder = new RoutineJavaBlockBuilder( context(), symbolUsage ) ;
			Builder<JBlock> conditionalBuilder = conditionalBlockBuilder.analyzeCommand( symbolUsage, expressionBuilder, command.commandType(), command.argument(), command.block() ) ;
			
			return (b)->{
				conditionalBuilder.build(b);
				JConditional conditional = b._if( expressionBuilder.build(condition,BOOLEAN).expr() ) ;
				blockBuilder.build( conditional._then() ) ;
			} ;
			
		} else {
			
			return blockBuilder ;
		}
		
	}	
	
	private CommandJavaStatementBuilder createStatementBuilder( SymbolUsage symbolUsage, RoutineJavaExpressionBuilder expressionBuilder, CommandType commandType ) {
		switch ( commandType ) {
		case DO:
		case GOTO:
			return new ExecBuilder( context(), symbolUsage, expressionBuilder ) ;
		case FOR:
			return new ForLoopBuilder( context(), symbolUsage, expressionBuilder ) ;
		
		case IF:
		case ELSE:
			return new IfElseBuilder( context(), symbolUsage, expressionBuilder ) ;
			
		case SET:
		case MERGE:
		case NEW:
		case KILL:
			return new VariableBuilder( context(), symbolUsage, expressionBuilder ) ;
			
		case QUIT:
			return new ReturnBuilder( context(), symbolUsage, expressionBuilder ) ;
			
		case EXECUTE:
			return new ParseBuilder( context(), symbolUsage, expressionBuilder ) ;
			
		case USE:
		default:
//			throw new UnsupportedOperationException( "command type \"" + commandType + "\" not supported" ) ;	
			return new GenericBuilder( context(), expressionBuilder ) ;
		}
	}
	
	public Builder<JBlock> analyzeCommand( SymbolUsage symbolUsage, RoutineJavaExpressionBuilder expressionBuilder, CommandType commandType, Argument argument, Block innerBlock ) {
		CommandJavaStatementBuilder statementBuilder = createStatementBuilder( symbolUsage, expressionBuilder, commandType ) ;
		return statementBuilder.analyze(commandType, argument, innerBlock);
	}
	
//	public JavaExpression<?> nullExpr() {
//		return m_expressionBuilder.build( null );
//	}
//	
//	public JavaExpression<?> expr( Expression expression ) {
//		return m_expressionBuilder.build( expression  ) ;
//	}
//	
//	public JavaExpression<?> expr( Expression expression, Representation representation ) {
//		return m_expressionBuilder.build(expression, representation);
//	}
}