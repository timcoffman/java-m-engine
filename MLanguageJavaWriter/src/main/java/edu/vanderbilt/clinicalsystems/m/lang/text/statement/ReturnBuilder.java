package edu.vanderbilt.clinicalsystems.m.lang.text.statement;

import com.sun.codemodel.JBlock;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Nothing;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.text.CommandJavaStatementBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBuilderContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaExpressionBuilder;

public class ReturnBuilder extends CommandJavaStatementBuilder {

	public ReturnBuilder( RoutineJavaBuilderContext builderContext, JBlock block, RoutineJavaExpressionBuilder expressionBuilder ) {
		super( builderContext, block, expressionBuilder ) ;
	}

	@Override protected void build( CommandType commandType, ExpressionList expressionList, Block block ) {
		Expression firstExpression = expressionList.elements().iterator().next();
		block()._return( expr( firstExpression ).expr() );
	}
	
	@Override protected void build( CommandType commandType, Nothing nothing, Block block ) {
		block()._return();
	}
	
}
