package edu.vanderbilt.clinicalsystems.m.lang.text;

import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.BOOLEAN;

import java.util.Iterator;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JExpression;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.Comment;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.Tag;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaExpressionBuilder.SymbolUsageListener;
import edu.vanderbilt.clinicalsystems.m.lang.text.statement.ExecBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.statement.ForLoopBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.statement.GenericBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.statement.IfElseBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.statement.ReturnBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.statement.VariableBuilder;

public class RoutineJavaBlockBuilder extends RoutineJavaBuilder {
	private JClass m_outerClass ;
	private final JBlock m_block ;
	private final SymbolUsage m_outerSymbolUsage ; 
	private final SymbolUsage m_symbolUsage ; 
	private final RoutineJavaExpressionBuilder m_expressionBuilder ;
	
	public RoutineJavaBlockBuilder( RoutineJavaBuilderContext builderContext, SymbolUsage outerSymbolUsage, JBlock block, JClass outerClass ) {
		super(builderContext) ;
		m_outerClass = outerClass ;
		m_outerSymbolUsage = outerSymbolUsage ;
		m_block = block ;
		m_symbolUsage = new SymbolUsage() ;
		SymbolUsageListener listener = new SymbolUsageListener() {
			@Override public void usedAs(JExpression e, Representation representation) { m_symbolUsage.usedAs(e, representation); }
		};
		m_expressionBuilder = new RoutineJavaExpressionBuilder( context(), m_outerClass, listener ) ;
	}

	public SymbolUsage symbolUsage() { return m_symbolUsage ; }
	
	public void build( Iterator<RoutineElement> elementIterator) {
		
		while ( elementIterator.hasNext() ) {
			RoutineElement element = elementIterator.next();
			
			if ( element instanceof Tag ) {
				
				build( (Tag)element );
				
			} else if ( element instanceof Comment ) {
				
				build( (Comment)element );
				
			} else if ( element instanceof Command ) {
				
				build( (Command)element );
				
			} else {
				
				/* no comment */
				
			}
			
			if ( endOfMethod( element ) )
				break ;
		}
	}

	public void build( Tag tag ) {
		if ( !tag.parameterNames().iterator().hasNext() )
			m_block.label( context().symbolForIdentifier(tag.name()) );
	}	
	
	public void build( Comment comment ) {
		/* no comment */
	}	
	
	public void build( Command command ) {
		Expression condition = command.condition();
		if ( null != condition ) {
			JConditional conditional = m_block._if( expr(condition,BOOLEAN).expr() ) ;
			RoutineJavaBlockBuilder conditionalBlockBuilder = new RoutineJavaBlockBuilder( context(), m_symbolUsage, conditional._then(), m_outerClass ) ;
			conditionalBlockBuilder.build( command.commandType(), command.argument(), command.block() ) ;
		} else {
			build( command.commandType(), command.argument(), command.block() ) ;
		}
		
		m_outerSymbolUsage.importUndeclared( m_symbolUsage );
	}	
	
	private CommandJavaStatementBuilder createStatementBuilder( CommandType commandType ) {
		switch ( commandType ) {
		case DO:
		case GOTO:
			return new ExecBuilder( context(), m_outerSymbolUsage, m_block, m_expressionBuilder ) ;
		case FOR:
			return new ForLoopBuilder( context(), m_outerSymbolUsage, m_block, m_expressionBuilder ) ;
		
		case IF:
		case ELSE:
			return new IfElseBuilder( context(), m_outerSymbolUsage, m_block, m_expressionBuilder ) ;
			
		case SET:
		case MERGE:
		case NEW:
		case KILL:
			return new VariableBuilder( context(), m_outerSymbolUsage, m_block, m_expressionBuilder ) ;
			
		case QUIT:
			return new ReturnBuilder( context(), m_block, m_expressionBuilder ) ;
			
		case USE:
		default:
//			throw new UnsupportedOperationException( "command type \"" + commandType + "\" not supported" ) ;	
			return new GenericBuilder( context(), m_block, m_expressionBuilder ) ;
		}
	}
	
	public void build( CommandType commandType, Argument argument, Block block ) {
		CommandJavaStatementBuilder statementBuilder = createStatementBuilder( commandType ) ;
		statementBuilder.build(commandType, argument, block);
	}
	
	
	public JavaExpression<?> nullExpr() {
		return m_expressionBuilder.build( null );
	}
	
	public JavaExpression<?> expr( Expression expression ) {
		return m_expressionBuilder.build( expression  ) ;
	}
	
	public JavaExpression<?> expr( Expression expression, Representation representation ) {
		return m_expressionBuilder.build(expression, representation);
	}
}