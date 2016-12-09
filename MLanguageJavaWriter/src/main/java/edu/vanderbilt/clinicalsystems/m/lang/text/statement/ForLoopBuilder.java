package edu.vanderbilt.clinicalsystems.m.lang.text.statement;

import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.BOOLEAN;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.NUMERIC;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JWhileLoop;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.LoopDefinition;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Nothing;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.text.CommandJavaStatementBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.JavaExpression;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBlockBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBuilderContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaExpressionBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.SymbolUsage;
public class ForLoopBuilder extends CommandJavaStatementBuilder {

	private final SymbolUsage m_outerSymbolUsage;

	public ForLoopBuilder( RoutineJavaBuilderContext builderContext, SymbolUsage outerSymbolUsage, JBlock block, RoutineJavaExpressionBuilder expressionBuilder ) {
		super( builderContext, block, expressionBuilder ) ;
		m_outerSymbolUsage = outerSymbolUsage ;
	}

	@Override protected void build( CommandType commandType, Nothing nothing, Block block ) {
		JWhileLoop loop = block()._while( JExpr.TRUE ) ;
		RoutineJavaBlockBuilder loopBlockBuilder = new RoutineJavaBlockBuilder( context(), m_outerSymbolUsage, loop.body(), outerClass() ) ;
		loopBlockBuilder.build( block.elements().iterator() ) ;
	}
	
	@Override protected void build( CommandType commandType, LoopDefinition loopDefinition, Block block ) {
		JForLoop loop = block()._for() ;
		
		JavaExpression<?> start = expr(loopDefinition.start());
		JavaExpression<JVar> loopVar = new JavaExpression<JVar>( loop.init( codeModel()._ref(start.type(env())), loopDefinition.destination().variableName(), start.expr() ), start.representation() ) ;
		
		if ( null != loopDefinition.stop() ) {
			loop.test( new JavaExpression<JExpression>( JOp.gte( loopVar.expr(), expr(loopDefinition.stop(),NUMERIC).expr() ), BOOLEAN ).expr() ) ;
		}

		if ( loopDefinition.step() instanceof Constant && ((Constant)loopDefinition.step()).representsNumber(1) )
			loop.update( loopVar.expr().incr() );
		else if ( loopDefinition.step() instanceof Constant && ((Constant)loopDefinition.step()).representsNumber(-1) )
			loop.update( loopVar.expr().decr() );
		else
			loop.update( loopVar.expr().assignPlus( expr(loopDefinition.step()).expr() ) );

		
		RoutineJavaBlockBuilder loopBlockBuilder = new RoutineJavaBlockBuilder( context(), m_outerSymbolUsage, loop.body(), outerClass() ) ;
		loopBlockBuilder.build( block.elements().iterator() ) ;
	}
	

}
