package edu.vanderbilt.clinicalsystems.m.lang.text.statement;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JDoLoop;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JTryBlock;

import edu.vanderbilt.clinicalsystems.m.core.lib.HaltCondition;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Nothing;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCallList;
import edu.vanderbilt.clinicalsystems.m.lang.text.CommandJavaStatementBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.JavaExpression;
import edu.vanderbilt.clinicalsystems.m.lang.text.JavaInvocation;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBlockBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBuilderClassContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaExpressionBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.SymbolUsage;

public class ExecBuilder extends CommandJavaStatementBuilder {

	private final SymbolUsage m_outerSymbolUsage;

	public ExecBuilder( RoutineJavaBuilderClassContext builderContext, SymbolUsage outerSymbolUsage, RoutineJavaExpressionBuilder expressionBuilder ) {
		super( builderContext, expressionBuilder ) ;
		m_outerSymbolUsage = outerSymbolUsage ;
	}
	
	@Override protected Builder<JBlock> analyze( CommandType commandType, TaggedRoutineCallList taggedRoutineCallList, Block innerBlock ) {
		if ( CommandType.DO == commandType ) {
			
			List<Builder<JBlock>> execBuilders = StreamSupport.stream(taggedRoutineCallList.elements().spliterator(),false)
				.map( this::analyze )
				.collect( Collectors.toList() )
				;
			return (b)->execBuilders.forEach( (e)->e.build(b) );
		
		} else if ( CommandType.GOTO == commandType ) {
			
			Builder<JBlock> execBuilder = analyze( taggedRoutineCallList.elements().iterator().next() );
			return (b)->{
				JTryBlock tryBlock = b._try();
				execBuilder.build( tryBlock.body() );
				tryBlock._finally()._throw( JExpr._new( codeModel().ref(HaltCondition.class) ).arg( JExpr.lit("return from GOTO") ) ) ;
			} ;
			
		} else {
			
			return unexpected( commandType, taggedRoutineCallList );
		}
	}
	
	private Builder<JBlock> analyze(TaggedRoutineCall taggedRoutineCall ) {
		String routineName = taggedRoutineCall.tagReference().routineName();
		
		String tagName ;
		if ( null == taggedRoutineCall.tagReference().tagName() ) {
			tagName = context().mainMethodName() ;
		} else {
			tagName = taggedRoutineCall.tagReference().tagName() ; ; 
		}
		
		String methodSymbol = context().symbolForIdentifier(tagName);
		if ( outerClassName().equals( routineName ) ) {
			/*******************************
			 * find the actual method here *
			 *******************************/
			List<JavaExpression<?>> arguments = StreamSupport.stream(taggedRoutineCall.arguments().spliterator(),false).map( this::expr ).collect( Collectors.toList() ) ;
			return (b)->{
				JInvocation invocation = b.invoke( methodSymbol ) ;
				arguments.forEach( (a)->invocation.arg(a.expr()));
			} ;

		} else {
			
			Method method = env().methodFor(routineName, tagName ) ;
			if ( null != method ) {
				
				List<JavaExpression<?>> arguments = StreamSupport.stream(taggedRoutineCall.arguments().spliterator(),false).map( this::expr ).collect( Collectors.toList() ) ;
				return JavaInvocation.builder(context())
						.invoke(method)
						.supplying( arguments )
						::build ;
				
			} else {
				List<JavaExpression<?>> arguments = StreamSupport.stream(taggedRoutineCall.arguments().spliterator(),false).map( this::expr ).collect( Collectors.toList() ) ;
				if ( null != routineName )
					return (b)->{
						JInvocation invocation = b.staticInvoke( codeModel().ref( routineName ), methodSymbol ) ;
						arguments.forEach( (a)->invocation.arg(a.expr()) );
					} ;
				else
					return (b)->{
						JInvocation invocation = b.invoke( methodSymbol ) ;
						arguments.forEach( (a)->invocation.arg(a.expr()));
					} ;
			}
			
		}
		
	}

	@Override protected Builder<JBlock> analyze( CommandType commandType, Nothing nothing, Block innerBlock ) {
		RoutineJavaBlockBuilder blockBuilder = new RoutineJavaBlockBuilder( context(), m_outerSymbolUsage ) ;
		Builder<JBlock> bodyBuilder = blockBuilder.analyze( innerBlock.elements().iterator() ) ;
		return (b)->build(bodyBuilder, b);
	}
	
	private void build( Builder<JBlock> bodyBuilder, JBlock block ) {
		JDoLoop loop = block._do( JExpr.FALSE );
		bodyBuilder.build( loop.body() );
	}
	
}
