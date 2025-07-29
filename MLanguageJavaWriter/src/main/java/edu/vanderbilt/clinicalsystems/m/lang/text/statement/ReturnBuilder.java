package edu.vanderbilt.clinicalsystems.m.lang.text.statement;

import com.sun.codemodel.JBlock;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Nothing;
import edu.vanderbilt.clinicalsystems.m.lang.text.CommandJavaStatementBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.JavaExpression;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBuilderClassContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaExpressionBuilder;
import edu.vanderbilt.clinicalsystems.m.text.repr.SymbolScope;

public class ReturnBuilder extends CommandJavaStatementBuilder {

	private final SymbolScope m_symbolScope;

	public ReturnBuilder( RoutineJavaBuilderClassContext builderContext, SymbolScope symbolScope, RoutineJavaExpressionBuilder expressionBuilder ) {
		super( builderContext, expressionBuilder ) ;
		m_symbolScope = symbolScope ;
	}

	@Override protected Builder<JBlock> analyze( CommandType commandType, ExpressionList expressionList, Block innerBlock ) {
		JavaExpression<?> firstExpression = expr( expressionList.elements().iterator().next() );
		m_symbolScope.returns( firstExpression.representationNode() );
		return (b)->b._return( firstExpression.expr() ) ;
	}
	
	@Override protected Builder<JBlock> analyze( CommandType commandType, Nothing nothing, Block innerBlock ) {
		// nothing // m_symbolScope.returns( VOID ) ;
		return (b)->b._return() ;
	}
	
	
}
