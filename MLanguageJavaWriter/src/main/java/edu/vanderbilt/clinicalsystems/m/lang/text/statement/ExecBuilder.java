package edu.vanderbilt.clinicalsystems.m.lang.text.statement;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.text.CommandJavaStatementBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.JavaExpression;
import edu.vanderbilt.clinicalsystems.m.lang.text.JavaInvocation;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBlockBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBuilderClassContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaExpressionBuilder;
import edu.vanderbilt.clinicalsystems.m.text.repr.MethodParameter;
import edu.vanderbilt.clinicalsystems.m.text.repr.MethodSymbol;
import edu.vanderbilt.clinicalsystems.m.text.repr.RepresentationNode;
import edu.vanderbilt.clinicalsystems.m.text.repr.SymbolScope;

public class ExecBuilder extends CommandJavaStatementBuilder {

	private final SymbolScope m_outerSymbolScope;

	public ExecBuilder( RoutineJavaBuilderClassContext builderContext, SymbolScope outerSymbolScope, RoutineJavaExpressionBuilder expressionBuilder ) {
		super( builderContext, expressionBuilder ) ;
		m_outerSymbolScope = outerSymbolScope ;
	}
	
	@Override protected Builder<JBlock> analyze( CommandType commandType, TaggedRoutineCallList taggedRoutineCallList, Block innerBlock ) {
		// all DO and QUIT arguments may have CONDITIONS on them
		if ( CommandType.DO == commandType ) {
			
			List<Builder<JBlock>> execBuilders = StreamSupport.stream(taggedRoutineCallList.elements().spliterator(),false)
				.map( this::analyze )
				.collect( Collectors.toList() )
				;
			return (b)->execBuilders.forEach( (e)->e.build(b) );
		
		} else if ( CommandType.GOTO == commandType ) { // TODO: should handle multiple TRCs, just like CommandType.DO
			
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
		String symbol = context().symbolForIdentifier(tagName);
		
		SymbolScope classScope = m_outerSymbolScope.enclosingClass().getDeclarationScope() ;
		int numberOfArguments = StreamSupport.stream(taggedRoutineCall.arguments().spliterator(),true).collect(Collectors.counting()).intValue() ;
		Optional<MethodSymbol> methodSymbol = classScope.methodSymbolFor( symbol, numberOfArguments ) ;

		List<JavaExpression<?>> parameters = new ArrayList<JavaExpression<?>>() ;
		List<RepresentationNode> parameterRepresentationNodes = new ArrayList<RepresentationNode>() ;
		for (Expression arg : taggedRoutineCall.arguments()) {
			JavaExpression<?> expr = expr(arg) ;
			parameters.add( expr ) ;
			parameterRepresentationNodes.add( expr.representationNode() ) ;
		}
		
		if ( null == routineName && methodSymbol.isPresent() ) {
			/* unspecified library and found internal, or specified internal */
			
			/*******************************
			 * find the actual method here *
			 *******************************/
			int position = 0 ;
			for ( RepresentationNode representationNode : parameterRepresentationNodes ) {  
				MethodParameter methodParameter = methodSymbol.get().parameter(position++) ;
				methodParameter.isAssigned( representationNode );
			}
			
			return (b)->{
				JInvocation invocation = b.invoke( symbol ) ;
				parameters.forEach( (a)->invocation.arg(a.expr()));
			} ;
			
		} else {
			
			Method method = env().methodFor(routineName, tagName ) ;
			if ( null != method ) {
				
				List<JavaExpression<?>> arguments = analyze( taggedRoutineCall.arguments() ) ;
				return JavaInvocation.builder(context())
						.invoke(method)
						.supplying( arguments )
						::build ;
				
			} else {
				if ( null != routineName )
					return (b)->{
						JInvocation invocation = b.staticInvoke( codeModel().ref( routineName ), symbol ) ;
						parameters.forEach( (a)->invocation.arg(a.expr()) );
					} ;
				else
					return (b)->{
						JInvocation invocation = b.invoke( symbol ) ;
						parameters.forEach( (a)->invocation.arg(a.expr()));
					} ;
			}
			
		}
		
	}

	@Override protected Builder<JBlock> analyze( CommandType commandType, Nothing nothing, Block innerBlock ) {
		RoutineJavaBlockBuilder blockBuilder = new RoutineJavaBlockBuilder( context(), m_outerSymbolScope ) ;
		Builder<JBlock> bodyBuilder = blockBuilder.analyze( innerBlock.elements().iterator() ) ;
		return (b)->build(bodyBuilder, b);
	}
	
	private void build( Builder<JBlock> bodyBuilder, JBlock block ) {
		JDoLoop loop = block._do( JExpr.FALSE );
		bodyBuilder.build( loop.body() );
	}
	
}
