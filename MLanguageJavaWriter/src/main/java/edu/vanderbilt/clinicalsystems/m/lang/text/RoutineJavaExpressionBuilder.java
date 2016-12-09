package edu.vanderbilt.clinicalsystems.m.lang.text;

import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeFunctionType.VALUE_INDEX;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.ANY;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.BOOLEAN;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.DECIMAL;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.INTEGER;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.NUMERIC;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.STRING;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JOp;

import edu.vanderbilt.clinicalsystems.m.core.VariableContext;
import edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeValueTypes;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction;
import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BinaryOperation;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.ConditionalExpression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.IndirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.UnaryOperation;

public class RoutineJavaExpressionBuilder extends RoutineJavaBuilder {
	
	private final JClass m_outerClass ;
	
	private final SymbolUsageListener m_listener ;
	private final JavaExpression<?> m_nullExpr ;

	public interface SymbolUsageListener {
		void usedAs( JExpression e, Representation representation ) ;
		
		default void usedAs( JavaExpression<?> e ) { usedAs( e.expr(), e.representation() ) ; }
	}
	
	public RoutineJavaExpressionBuilder( RoutineJavaBuilderContext builderContext, JClass outerClass, SymbolUsageListener listener ) {
		super(builderContext) ;
		m_outerClass = outerClass ;
		m_listener = listener ;
		Method initialValueMethod = env().methodFor( NativeValueTypes.INITIAL_VALUE ) ;
		m_nullExpr = JavaInvocation.builder(builderContext).invoke(initialValueMethod).acceptingNothing().build() ;
	}

	public JClass outerClass() { return m_outerClass ; }
	
	public JavaExpression<?> build( Expression expression ) {
		return build( expression, ANY ) ;
	}
	
	public JavaExpression<?> build( Expression expression, Representation representation ) {
		if ( null == expression )
			return m_nullExpr ;
		
		JavaExpression<?> e = expression.visit( new Expression.Visitor<JavaExpression<?>>() {

			@Override public JavaExpression<?> visitExpression(Expression expression) {
				throw new UnsupportedOperationException( "expression type \"" + expression.getClass() + "\" not supported" ) ;	
			}

			@Override
			public JavaExpression<?> visitConditional( ConditionalExpression conditional ) {
				throw new UnsupportedOperationException( "expression type \"" + expression.getClass() + "\" not supported" ) ;	
			}
			
			@Override
			public JavaExpression<?> visitDirectVariableReference( DirectVariableReference variable ) {
				String symbol = context().symbolForIdentifier(variable.variableName());
				return JavaExpression.from( JExpr.ref( symbol ), representation );
			}
			
			@Override
			public JavaExpression<?> visitIndirectVariableReference( IndirectVariableReference variable ) {
				JavaExpression<?> target = JavaInvocation.builder(context())
						.on(VariableContext.class)
						.invoke("lookup")
						.accepting( java.lang.String.class )
						.supplying( build( variable.variableNameProducer(), STRING ) ) 
						.build() ;

				Iterator<Expression> keyIterator = variable.keys().iterator();
				while ( keyIterator.hasNext() )
					target = JavaInvocation.builder(context())
						.on( target )
						.invoke( env().methodFor(VALUE_INDEX) )
						.supplying( build( keyIterator.next() ) )
						.build();
				return target ;
			}

			private JavaExpression<?> literalInteger( Constant constant ) {
				long longValue = constant.toLong() ;
				if ( longValue < Integer.MAX_VALUE && longValue > Integer.MIN_VALUE )
					return JavaExpression.from( JExpr.lit( (int)constant.toLong() ), Representation.INTEGER );
				else
					return JavaExpression.from( JExpr.lit( constant.toLong() ), Representation.INTEGER );
			}
			
			private JavaExpression<?> literalDecimal( Constant constant ) {
				return JavaExpression.from( JExpr.lit( constant.toDouble() ), Representation.DECIMAL );
			}
			
			@Override
			public JavaExpression<?> visitConstant(Constant constant) {
				if ( constant.representsNull() )
					return m_nullExpr ;
				
				switch ( representation ) {
				case STRING:
					return JavaExpression.from( JExpr.lit( constant.value() ), representation );
				case BOOLEAN:
					return JavaExpression.from( JExpr.lit( constant.toBoolean() ), representation );
				case INTEGER:
					return literalInteger( constant );
				case DECIMAL:
					return literalDecimal( constant ) ;
				case NUMERIC:
					if ( constant.representsInteger() ) {
						return literalInteger( constant );
					} else {
						return literalDecimal( constant );
					}
				case ANY:
				default:
					if ( constant.representsInteger() )
						return literalInteger( constant );
					else if ( constant.representsNumber() )
						return literalDecimal( constant ); 
					else
						return JavaExpression.from( JExpr.lit( constant.value() ), representation );
				}
			}

			private JavaExpression<?> buildOp( BinaryOperation operation, BiFunction<JExpression,JExpression,JExpression> f, Representation producingRep, Representation lhsRep, Representation rhsRep) {
				JavaExpression<?> lhs = build( operation.leftHandSide(), lhsRep ) ;
				JavaExpression<?> rhs = build( operation.rightHandSide(), rhsRep ) ;
				JExpression expr = f.apply(lhs.expr(),rhs.expr()) ;
				return JavaExpression.from(expr, producingRep) ;
			}
			
			private JavaExpression<?> buildOp( UnaryOperation operation, Function<JExpression,JExpression> f, Representation producingRep, Representation operandRep) {
				JavaExpression<?> operand = build( operation.operand(), operandRep ) ;
				JExpression expr = f.apply(operand.expr()) ;
				return JavaExpression.from(expr, producingRep) ;
			}
			
			@Override
			public JavaExpression<?> visitBinaryOperation( BinaryOperation operation ) {
				switch (operation.operator()) {
				case CONCAT:
					return buildOp( operation, JOp::plus, STRING, STRING, STRING ) ;
				case ADD:
					return buildOp( operation, JOp::plus, ANY, NUMERIC, NUMERIC ) ;
				case SUBTRACT:
					return buildOp( operation, JOp::minus, ANY, NUMERIC, NUMERIC ) ;
				case MULTIPLY:
					return buildOp( operation, JOp::mul, ANY, NUMERIC, NUMERIC ) ;
				case DIVIDE:
					return buildOp( operation, JOp::div, ANY, DECIMAL, DECIMAL ) ;
				case DIVIDE_INT:
					return buildOp( operation, JOp::div, ANY, INTEGER, INTEGER ) ;
				case MODULO:
					return buildOp( operation, JOp::mod, ANY, NUMERIC, NUMERIC ) ;
				case EQUALS:
					return buildOp( operation, JOp::eq, ANY, ANY, ANY) ;
				case NOT_EQUALS:
					return buildOp( operation, JOp::ne, ANY, ANY, ANY) ;
				case GREATER_THAN:
					return buildOp( operation, JOp::gt, ANY, NUMERIC, NUMERIC ) ;
				case NOT_GREATER_THAN:
					return buildOp( operation, JOp::lte, ANY, NUMERIC, NUMERIC ) ;
				case LESS_THAN:
					return buildOp( operation, JOp::lt, ANY, NUMERIC, NUMERIC ) ;
				case NOT_LESS_THAN:
					return buildOp( operation, JOp::gte, ANY, NUMERIC, NUMERIC ) ;
				case AND:
					return buildOp( operation, JOp::cand, ANY, BOOLEAN, BOOLEAN ) ;
				case OR:
					return buildOp( operation, JOp::cor, ANY, BOOLEAN, BOOLEAN ) ;
				default:
					return builtinExpr( operation.operator(), build( operation.leftHandSide() ), build( operation.rightHandSide() ) ) ;
				}
			}

			@Override
			public JavaExpression<?> visitUnaryOperation( UnaryOperation operation ) {
				switch ( operation.operator() ) {
				case ADD:
					return build( operation.operand() ) ;
				case SUBTRACT:
					return buildOp( operation, JOp::minus, ANY, NUMERIC ) ;
				case NOT:
					return buildOp( operation, JOp::not, ANY, BOOLEAN ) ;
				default:
					throw new UnsupportedOperationException( "operator type \"" + operation.operator() + "\" not supported" ) ;	
				}
			}
			
			@Override
			public JavaExpression<?> visitRoutineFunctionCall(RoutineFunctionCall functionCall) {
				String routineName = functionCall.tagReference().routineName();

				String tagName ;
				if ( null == functionCall.tagReference().tagName() ) {
					tagName = context().mainMethodName() ;
				} else {
					tagName = functionCall.tagReference().tagName() ; 
				}
				
				JavaInvocation invocation ;
				if ( m_outerClass.name().equals( routineName ) ) {

					List<Representation> parameterRepresentations = StreamSupport.stream(functionCall.arguments().spliterator(),false).map( (expr)->ANY ).collect( Collectors.toList() ) ;
					invocation = new JavaInvocation( JExpr.invoke( context().symbolForIdentifier(tagName) ), ANY, parameterRepresentations, null, context() );

				} else {
					
					Method method = env().methodFor(routineName, tagName ) ;
					if ( null != method ) {
						invocation = JavaInvocation.builder(context()).invoke(method).build();
					} else {
						List<Representation> parameterRepresentations = StreamSupport.stream(functionCall.arguments().spliterator(),false).map( (expr)->ANY ).collect( Collectors.toList() ) ;
						if ( null != routineName )
							invocation = new JavaInvocation( codeModel().ref( routineName ).staticInvoke( context().symbolForIdentifier(tagName) ), ANY, parameterRepresentations, null, context() );
						else
							invocation = new JavaInvocation( JExpr.invoke( context().symbolForIdentifier(tagName) ), ANY, parameterRepresentations, null, context() );
					}
					
				}
				
				for (Expression arg : functionCall.arguments())
					invocation.appendArgument( (r)->build(arg,r) ) ;
				return invocation ;
			}
			
			@Override
			public JavaExpression<?> visitBuiltinFunctionCall(BuiltinFunctionCall functionCall) {
				switch ( functionCall.builtinFunction() ) {
				case SELECT:
					return conditionalExpr( functionCall.arguments().iterator() ) ;
				default:
					return builtinExpr( functionCall.builtinFunction(), functionCall.arguments(), false ) ;
				}
			}
			
			
		}) ;
		
		m_listener.usedAs(e);
		
		return e ;
	}
	
	private JavaInvocation builtinExpr( BuiltinFunction symbol, Iterable<Expression> arguments, boolean forAssignment ) {
		int numberOfParameters = StreamSupport.stream(arguments.spliterator(),false).collect( Collectors.counting() ).intValue() ;
		JavaInvocation invocation = JavaInvocation.builder(context())
				.invoke( env().methodFor( symbol, numberOfParameters, forAssignment ) )
				.accepting( numberOfParameters )
				.build();
		for (Expression arg : arguments)
			invocation.appendArgument( (r)->build(arg,r) ) ;
		return invocation ;
	}
	
	private JavaInvocation builtinExpr( OperatorType symbol, JavaExpression<?> ... arguments ) {
		return JavaInvocation.builder(context())
				.invoke( env().methodFor( symbol ) )
				.supplying( arguments )
				.build();
	}
	
	private JavaExpression<?> conditionalExpr( Iterator<Expression> expressionIterator ) {
		if ( !expressionIterator.hasNext() )
			return m_nullExpr ;
		
		return expressionIterator.next().visit( new Expression.Visitor<JavaExpression<?>>() {

			@Override public JavaExpression<?> visitExpression(Expression expression) {
				return build( expression );
			}
			
			@Override public JavaExpression<?> visitConditional(ConditionalExpression conditional) {
				JavaExpression<?> testExpression = build( conditional.condition(), BOOLEAN );
				JavaExpression<?> trueResult = build(conditional.expression());
				JavaExpression<?> falseResult = conditionalExpr(expressionIterator);
				Representation representation = JavaExpression.both( trueResult.representation(), falseResult.representation() ) ;
				return JavaExpression.from( JOp.cond( testExpression.expr(), trueResult.expr(), falseResult.expr() ), representation );
			}
			
			
		}) ;
	}

}