package edu.vanderbilt.clinicalsystems.m.lang.text.statement;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JConditional;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Nothing;
import edu.vanderbilt.clinicalsystems.m.lang.text.CommandJavaStatementBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.JavaExpression;
import edu.vanderbilt.clinicalsystems.m.lang.text.Representation;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBlockBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBuilderClassContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaExpressionBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.SymbolUsage;

public class IfElseBuilder extends CommandJavaStatementBuilder {

	private final SymbolUsage m_outerSymbolUsage ;
	
	public IfElseBuilder( RoutineJavaBuilderClassContext builderContext, SymbolUsage outerSymbolUsage, RoutineJavaExpressionBuilder expressionBuilder ) {
		super( builderContext, expressionBuilder ) ;
		m_outerSymbolUsage = outerSymbolUsage ;
	}

	@Override protected Builder<JBlock> analyze( CommandType commandType, ExpressionList expressionList, Block innerBlock ) {
		expect( CommandType.IF, commandType, expressionList ) ;
		JavaExpression<?> conditionalExpression = expr(expressionList.elements().iterator().next(), Representation.BOOLEAN );
		
		RoutineJavaBlockBuilder conditionalBlockBuilder = new RoutineJavaBlockBuilder( context(), m_outerSymbolUsage ) ;
		Builder<JBlock> conditionalBuilder = conditionalBlockBuilder.analyze( innerBlock.elements().iterator() ) ;
		return (b)->{
			
			JConditional conditional = b._if( conditionalExpression.expr() ) ;
			conditionalBuilder.build( conditional._then() ) ;
		};
	}
	
	@Override protected Builder<JBlock> analyze( CommandType commandType, Nothing nothing, Block innerBlock ) {
		expect( CommandType.ELSE, commandType, nothing ) ;
		RoutineJavaBlockBuilder conditionalBlockBuilder = new RoutineJavaBlockBuilder( context(), m_outerSymbolUsage ) ;
		Builder<JBlock> conditionalBuilder = conditionalBlockBuilder.analyze( innerBlock.elements().iterator() ) ;
		return (b)->{

			JConditional conditional = getContext( JConditional.class, b ) ;
			conditionalBuilder.build( conditional._else() ) ;

		};
	}
	
}
