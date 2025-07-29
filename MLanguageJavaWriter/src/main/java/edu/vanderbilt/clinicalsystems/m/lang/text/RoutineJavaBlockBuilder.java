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
import edu.vanderbilt.clinicalsystems.m.text.repr.SymbolScope;

public class RoutineJavaBlockBuilder extends RoutineJavaBuilder<RoutineJavaBuilderClassContext> {
	private final SymbolScope m_outerSymbolScope ; 
	
	public RoutineJavaBlockBuilder( RoutineJavaBuilderClassContext builderContext, SymbolScope outerSymbolScope ) {
		super(builderContext) ;
		m_outerSymbolScope = outerSymbolScope ;
	}

	public Builder<JBlock> analyze( Iterator<RoutineElement> elementIterator ) {
		SymbolScope symbolScope = env().representationInference().createScope( m_outerSymbolScope, "block-scope:" + m_outerSymbolScope.description() );
		
		RoutineJavaExpressionBuilder expressionBuilder = new RoutineJavaExpressionBuilder( context(), symbolScope ) ;

		List<Builder<JBlock>> elementBuilders = new ArrayList<>();
		
		while ( elementIterator.hasNext() ) {
			RoutineElement element = elementIterator.next();
			
			if ( element instanceof Tag ) {
				
				elementBuilders.add( analyzeTag( (Tag)element ) );
				
			} else if ( element instanceof Comment ) {
				
				elementBuilders.add( analyzeComment( (Comment)element ) );
				
			} else if ( element instanceof Command ) {
				
				elementBuilders.add( analyzeCommand( symbolScope, expressionBuilder, (Command)element ) );
				
			} else {
				
				/* no comment */
				
			}
			
			if ( endOfMethod( element ) )
				break ;
		}
		
		return (b)->build( symbolScope, expressionBuilder, elementBuilders, b ) ;
	}

	private void build( SymbolScope symbolScope, RoutineJavaExpressionBuilder expressionBuilder, List<Builder<JBlock>> elementBuilders, JBlock block ) {
		System.out.println( "") ;
//		env().representationInference().print( symbolScope, System.out);
//		env().representationInference().print( System.out);
		
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
	
	public Builder<JBlock> analyzeCommand( SymbolScope symbolScope, RoutineJavaExpressionBuilder expressionBuilder, Command command ) {
		Builder<JBlock> blockBuilder = analyzeCommand( symbolScope, expressionBuilder, command.commandType(), command.argument(), command.block() ) ;
		
		Expression condition = command.condition();
		if ( null != condition ) {
			RoutineJavaBlockBuilder conditionalBlockBuilder = new RoutineJavaBlockBuilder( context(), symbolScope ) ;
			Builder<JBlock> conditionalBuilder = conditionalBlockBuilder.analyzeCommand( symbolScope, expressionBuilder, command.commandType(), command.argument(), command.block() ) ;
			
			return (b)->{
				conditionalBuilder.build(b);
				JConditional conditional = b._if( expressionBuilder.build(condition,BOOLEAN).expr() ) ;
				blockBuilder.build( conditional._then() ) ;
			} ;
			
		} else {
			
			return blockBuilder ;
		}
		
	}	
	
	private CommandJavaStatementBuilder createStatementBuilder( SymbolScope symbolScope, RoutineJavaExpressionBuilder expressionBuilder, CommandType commandType ) {
		switch ( commandType ) {
		case DO:
		case GOTO:
			return new ExecBuilder( context(), symbolScope, expressionBuilder ) ;
		case FOR:
			return new ForLoopBuilder( context(), symbolScope, expressionBuilder ) ;
		
		case IF:
		case ELSE:
			return new IfElseBuilder( context(), symbolScope, expressionBuilder ) ;
			
		case SET:
		case MERGE:
		case NEW:
		case KILL:
			return new VariableBuilder( context(), symbolScope, expressionBuilder ) ;
			
		case QUIT:
			return new ReturnBuilder( context(), symbolScope, expressionBuilder ) ;
			
		case EXECUTE:
			return new ParseBuilder( context(), symbolScope, expressionBuilder ) ;
			
		case USE:
		default:
//			throw new UnsupportedOperationException( "command type \"" + commandType + "\" not supported" ) ;	
			return new GenericBuilder( context(), expressionBuilder ) ;
		}
	}
	
	public Builder<JBlock> analyzeCommand( SymbolScope symbolScope, RoutineJavaExpressionBuilder expressionBuilder, CommandType commandType, Argument argument, Block innerBlock ) {
		CommandJavaStatementBuilder statementBuilder = createStatementBuilder( symbolScope, expressionBuilder, commandType ) ;
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