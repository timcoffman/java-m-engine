package edu.vanderbilt.clinicalsystems.m.lang.text.statement;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JConditional;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Nothing;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.text.CommandJavaStatementBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.Representation;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBlockBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBuilderContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaExpressionBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.SymbolUsage;

public class IfElseBuilder extends CommandJavaStatementBuilder {

	private final SymbolUsage m_outerSymbolUsage ;
	
	public IfElseBuilder( RoutineJavaBuilderContext builderContext, SymbolUsage outerSymbolUsage, JBlock block, RoutineJavaExpressionBuilder expressionBuilder ) {
		super( builderContext, block, expressionBuilder ) ;
		m_outerSymbolUsage = outerSymbolUsage ;
	}

	@Override protected void build( CommandType commandType, ExpressionList expressionList, Block block ) {
		expect( CommandType.IF, commandType, expressionList ) ;
		Expression firstExpression = expressionList.elements().iterator().next();
		JConditional conditional = block()._if( expr(firstExpression, Representation.BOOLEAN ).expr() ) ;
		RoutineJavaBlockBuilder conditionalBlockBuilder = new RoutineJavaBlockBuilder( context(), m_outerSymbolUsage, conditional._then(), outerClass() ) ;
		conditionalBlockBuilder.build( block.elements().iterator() ) ;
	}
	
	@Override protected void build( CommandType commandType, Nothing nothing, Block block ) {
		expect( CommandType.ELSE, commandType, nothing ) ;
		JConditional conditional = getContext( JConditional.class ) ;
		RoutineJavaBlockBuilder conditionalBlockBuilder = new RoutineJavaBlockBuilder( context(), m_outerSymbolUsage, conditional._else(), outerClass() ) ;
		conditionalBlockBuilder.build( block.elements().iterator() ) ;
	}
	
}
