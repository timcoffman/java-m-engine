package edu.vanderbilt.clinicalsystems.m.lang.text.statement;

import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.BOOLEAN;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.NUMERIC;

import java.util.Optional;
import java.util.function.Supplier;

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
import edu.vanderbilt.clinicalsystems.m.lang.text.Representation;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBlockBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBuilderClassContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaExpressionBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.SymbolUsage;
public class ForLoopBuilder extends CommandJavaStatementBuilder {

	private final RoutineJavaBlockBuilder m_blockBuilder;
	private final SymbolUsage m_outerSymbolUsage ;

	public ForLoopBuilder( RoutineJavaBuilderClassContext builderContext, SymbolUsage outerSymbolUsage, RoutineJavaExpressionBuilder expressionBuilder ) {
		super( builderContext, expressionBuilder ) ;
		m_blockBuilder = new RoutineJavaBlockBuilder( context(), outerSymbolUsage );
		m_outerSymbolUsage = outerSymbolUsage ;
	}

	@Override protected Builder<JBlock> analyze( CommandType commandType, Nothing nothing, Block innerBlock ) {
		Builder<JBlock> loopBuilder = m_blockBuilder.analyze( innerBlock.elements().iterator() ) ;
		return (b)->{

			JWhileLoop loop = b._while( JExpr.TRUE ) ;
			loopBuilder.build( loop.body() ) ;
			
		};
	}
	
	@Override protected Builder<JBlock> analyze( CommandType commandType, LoopDefinition loopDefinition, Block innerBlock ) {
		JavaExpression<?> start = expr(loopDefinition.start());
		String loopSymbol = context().symbolForIdentifier( loopDefinition.destination().variableName() );
		Supplier<Optional<Representation>> loopVarRep = m_outerSymbolUsage.impliedRepresentation(loopSymbol) ;

		JavaExpression<?> loopStep = expr(loopDefinition.step()) ;
		
		Optional<JavaExpression<?>> loopStop = Optional.ofNullable(loopDefinition.stop()).map( (e)->expr(e,NUMERIC) ) ;

		final Builder<JBlock> loopBuilder = m_blockBuilder.analyze( innerBlock.elements().iterator() ) ;
		return (b)->{
			
			JForLoop loop = b._for() ;
			
			JavaExpression<JVar> loopVar = new JavaExpression<JVar>( loop.init( context().typeFor(loopVarRep.get().get()), loopSymbol, start.expr() ), loopVarRep ) ;
			
			if ( loopStop.isPresent() ) {
				loop.test( new JavaExpression<JExpression>( JOp.gte( loopVar.expr(), loopStop.get().expr() ), BOOLEAN.supplier() ).expr() ) ;
			}

			if ( loopStep.expr() instanceof Constant && ((Constant)loopStep.expr()).representsNumber(1) )
				loop.update( loopVar.expr().incr() );
			else if ( loopDefinition.step() instanceof Constant && ((Constant)loopDefinition.step()).representsNumber(-1) )
				loop.update( loopVar.expr().decr() );
			else
				loop.update( loopVar.expr().assignPlus( loopStep.expr() ) );

			
			loopBuilder.build( loop.body() ) ;
			
		};
	}
	
}
