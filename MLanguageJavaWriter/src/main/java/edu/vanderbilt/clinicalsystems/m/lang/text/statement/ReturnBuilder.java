package edu.vanderbilt.clinicalsystems.m.lang.text.statement;

import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.VOID;

import com.sun.codemodel.JBlock;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Nothing;
import edu.vanderbilt.clinicalsystems.m.lang.text.CommandJavaStatementBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.JavaExpression;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBuilderClassContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaExpressionBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.SymbolUsage;

public class ReturnBuilder extends CommandJavaStatementBuilder {

	private final SymbolUsage m_symbolUsage;

	public ReturnBuilder( RoutineJavaBuilderClassContext builderContext, SymbolUsage symbolUsage, RoutineJavaExpressionBuilder expressionBuilder ) {
		super( builderContext, expressionBuilder ) ;
		m_symbolUsage = symbolUsage ;
	}

	@Override protected Builder<JBlock> analyze( CommandType commandType, ExpressionList expressionList, Block innerBlock ) {
		JavaExpression<?> firstExpression = expr( expressionList.elements().iterator().next() );
		m_symbolUsage.scopeReturns( firstExpression.representation() );
		return (b)->b._return( firstExpression.expr() ) ;
	}
	
	@Override protected Builder<JBlock> analyze( CommandType commandType, Nothing nothing, Block innerBlock ) {
		m_symbolUsage.scopeReturns( VOID.supplier() ) ;
		return (b)->b._return() ;
	}
	
	
}
