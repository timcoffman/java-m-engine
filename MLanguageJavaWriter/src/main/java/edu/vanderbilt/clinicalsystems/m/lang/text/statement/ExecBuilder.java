package edu.vanderbilt.clinicalsystems.m.lang.text.statement;

import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.ANY;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JDoLoop;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JTryBlock;

import edu.vanderbilt.clinicalsystems.m.core.lib.HaltCondition;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Nothing;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCallList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.text.CommandJavaStatementBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.JavaInvocation;
import edu.vanderbilt.clinicalsystems.m.lang.text.Representation;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBlockBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBuilderContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaExpressionBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.SymbolUsage;

public class ExecBuilder extends CommandJavaStatementBuilder {

	private final SymbolUsage m_outerSymbolUsage;

	public ExecBuilder( RoutineJavaBuilderContext builderContext, SymbolUsage outerSymbolUsage, JBlock block, RoutineJavaExpressionBuilder expressionBuilder ) {
		super( builderContext, block, expressionBuilder ) ;
		m_outerSymbolUsage = outerSymbolUsage ;
	}
	
	@Override protected void build( CommandType commandType, TaggedRoutineCallList taggedRoutineCallList, Block block ) {
		if ( CommandType.DO == commandType ) {
			
			for ( TaggedRoutineCall taggedRoutineCall : taggedRoutineCallList.elements() )
				invoke( taggedRoutineCall, block() ) ;
		
		} else if ( CommandType.GOTO == commandType ) {
			
			JTryBlock tryBlock = block()._try();
			invoke( taggedRoutineCallList.elements().iterator().next(), tryBlock.body() );
			tryBlock._finally()._throw( JExpr._new( codeModel().ref(HaltCondition.class) ).arg( JExpr.lit("return from GOTO") ) ) ;
			
		} else {
			
			unexpected( commandType, taggedRoutineCallList );
		}
	}
	
	private void invoke(TaggedRoutineCall taggedRoutineCall, JBlock block) {
		String routineName = taggedRoutineCall.tagReference().routineName();
		
		String tagName ;
		if ( null == taggedRoutineCall.tagReference().tagName() ) {
			tagName = context().mainMethodName() ;
		} else {
			tagName = taggedRoutineCall.tagReference().tagName() ; ; 
		}
		
		JavaInvocation invocation ;
		if ( outerClass().name().equals( routineName ) ) {

			List<Representation> parameterRepresentations = StreamSupport.stream(taggedRoutineCall.arguments().spliterator(),false).map( (expr)->ANY ).collect( Collectors.toList() ) ;
			invocation = new JavaInvocation( block.invoke( context().symbolForIdentifier(tagName) ), ANY, parameterRepresentations, null, context() );

		} else {
			
			Method method = env().methodFor(routineName, tagName ) ;
			if ( null != method )
				invocation = JavaInvocation.builder(context()).invoke(method).build( block );
			else {
				List<Representation> parameterRepresentations = StreamSupport.stream(taggedRoutineCall.arguments().spliterator(),false).map( (expr)->ANY ).collect( Collectors.toList() ) ;
				if ( null != routineName )
					invocation = new JavaInvocation( block.staticInvoke( codeModel().ref( routineName ), context().symbolForIdentifier(tagName) ), ANY, parameterRepresentations, null, context() );
				else
					invocation = new JavaInvocation( block.invoke( context().symbolForIdentifier(tagName) ), ANY, parameterRepresentations, null, context() );
			}
			
		}
		
		for (Expression arg : taggedRoutineCall.arguments())
			invocation.appendArgument( (r)->expr(arg,r) ) ;
	}

	@Override protected void build( CommandType commandType, Nothing nothing, Block block ) {
		JDoLoop loop = block()._do( JExpr.FALSE );
		RoutineJavaBlockBuilder blockBuilder = new RoutineJavaBlockBuilder( context(), m_outerSymbolUsage, loop.body(), outerClass() ) ;
		blockBuilder.build( block.elements().iterator() ) ;
	}
	
}
