package edu.vanderbilt.clinicalsystems.m.lang.text;

import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeFunctionType.VALUE_INDEX;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.BOOLEAN;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.DECIMAL;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.INTEGER;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.NATIVE;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.NUMERIC;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.STRING;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;

public class RoutineJavaExpressionBuilder extends RoutineJavaBuilder<RoutineJavaBuilderClassContext> {
	
	private final SymbolUsage m_symbolUsage ;
	private final JavaExpression<?> m_nullExpr ;

	public interface SymbolUsageListener {
		void usedAs( String symbol, Supplier<Optional<Representation>> representation ) ;
	}
	
	public RoutineJavaExpressionBuilder( RoutineJavaBuilderClassContext builderContext, SymbolUsage symbolUsage ) {
		super(builderContext) ;
		m_symbolUsage = symbolUsage ;
		Method initialValueMethod = env().methodFor( NativeValueTypes.INITIAL_VALUE ) ;
		m_nullExpr = JavaInvocation.builder(builderContext).invoke(initialValueMethod).acceptingNothing().build() ;
	}

	public JavaExpression<?> build( Expression expression ) {
		return build( expression, ()->Optional.empty() ) ;
	}
	
	public JavaExpression<?> build( Expression expression, Representation expectedRepresentation ) {
		return build( expression, expectedRepresentation.supplier() ) ;
	}
	
	public JavaExpression<?> build( Expression expression, Supplier<Optional<Representation>> expectedRepresentation ) {
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
				m_symbolUsage.usedAs(symbol, expectedRepresentation);
				Supplier<Optional<Representation>> representation = m_symbolUsage.impliedRepresentation(symbol) ;
				return JavaExpression.from( JExpr.ref( symbol ), representation );
			}
			
			@Override
			public JavaExpression<?> visitIndirectVariableReference( IndirectVariableReference variable ) {
				JavaExpression<?> target = JavaInvocation.builder(context())
						.on(VariableContext.class)
						.invoke("lookup")
						.accepting( java.lang.String.class )
						.supplying( (r)->build( variable.variableNameProducer(), STRING ) ) 
						.build() ;
				return applyKeys(variable,target) ;
			}

			private JavaExpression<?> literalInteger( Constant constant ) {
				long longValue = constant.toLong() ;
				if ( longValue < Integer.MAX_VALUE && longValue > Integer.MIN_VALUE )
					return JavaExpression.from( JExpr.lit( (int)constant.toLong() ), INTEGER );
				else
					return JavaExpression.from( JExpr.lit( constant.toLong() ), INTEGER );
			}
			
			private JavaExpression<?> literalDecimal( Constant constant ) {
				return JavaExpression.from( JExpr.lit( constant.toDouble() ), DECIMAL );
			}
			
			@Override
			public JavaExpression<?> visitConstant(Constant constant) {
				if ( constant.representsNull() )
					return m_nullExpr ;
				
				switch ( expectedRepresentation.get().orElse(NATIVE) ) {
				case STRING:
					return JavaExpression.from( JExpr.lit( constant.value() ), STRING );
				case BOOLEAN:
					return JavaExpression.from( JExpr.lit( constant.toBoolean() ), BOOLEAN );
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
				case NATIVE:
				default:
					if ( constant.representsInteger() )
						return literalInteger( constant );
					else if ( constant.representsNumber() )
						return literalDecimal( constant ); 
					else
						return JavaExpression.from( JExpr.lit( constant.value() ), STRING );
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
					return buildOp( operation, JOp::plus, NUMERIC, NUMERIC, NUMERIC ) ;
				case SUBTRACT:
					return buildOp( operation, JOp::minus, NUMERIC, NUMERIC, NUMERIC ) ;
				case MULTIPLY:
					return buildOp( operation, JOp::mul, NUMERIC, NUMERIC, NUMERIC ) ;
				case DIVIDE:
					return buildOp( operation, JOp::div, DECIMAL, DECIMAL, DECIMAL ) ;
				case DIVIDE_INT:
					return buildOp( operation, JOp::div, INTEGER, INTEGER, INTEGER ) ;
				case MODULO:
					return buildOp( operation, JOp::mod, NUMERIC, NUMERIC, NUMERIC ) ;
				case EQUALS:
					return buildOp( operation, JOp::eq, NATIVE, NATIVE, NATIVE) ;
				case NOT_EQUALS:
					return buildOp( operation, JOp::ne, NATIVE, NATIVE, NATIVE) ;
				case GREATER_THAN:
					return buildOp( operation, JOp::gt, BOOLEAN, NUMERIC, NUMERIC ) ;
				case NOT_GREATER_THAN:
					return buildOp( operation, JOp::lte, BOOLEAN, NUMERIC, NUMERIC ) ;
				case LESS_THAN:
					return buildOp( operation, JOp::lt, BOOLEAN, NUMERIC, NUMERIC ) ;
				case NOT_LESS_THAN:
					return buildOp( operation, JOp::gte, BOOLEAN, NUMERIC, NUMERIC ) ;
				case AND:
					return buildOp( operation, JOp::cand, BOOLEAN, BOOLEAN, BOOLEAN ) ;
				case OR:
					return buildOp( operation, JOp::cor, BOOLEAN, BOOLEAN, BOOLEAN ) ;
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
					return buildOp( operation, JOp::minus, NUMERIC, NUMERIC ) ;
				case NOT:
					return buildOp( operation, JOp::not, BOOLEAN, BOOLEAN ) ;
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
				String symbol = context().symbolForIdentifier(tagName);
				
				JavaInvocation invocation ;
				if ( (null == routineName && m_symbolUsage.declared(symbol,true) || context().outerClassName().equals( context().symbolForIdentifier(routineName) ) ) ) {
					/* unspecified library and found internal, or specified internal */
					
					List<Representation> parameterRepresentations = StreamSupport.stream(functionCall.arguments().spliterator(),false).map( (expr)->NATIVE ).collect( Collectors.toList() ) ;
					m_symbolUsage.usedAs(symbol, expectedRepresentation);
					Supplier<Optional<Representation>> returningRepresentation = m_symbolUsage.impliedRepresentation(symbol) ;
					invocation = new JavaInvocation( JExpr.invoke( symbol ), returningRepresentation, parameterRepresentations, null, context() );
					
				} else {
					/* expected external */
					
					Method method = env().methodFor(routineName, tagName ) ;
					if ( null != method ) {
						invocation = JavaInvocation.builder(context()).invoke(method).build();
					} else if ( null != routineName ) {
						List<Representation> parameterRepresentations = StreamSupport.stream(functionCall.arguments().spliterator(),false).map( (expr)->NATIVE ).collect( Collectors.toList() ) ;
						invocation = new JavaInvocation( codeModel().ref( routineName ).staticInvoke( symbol ), NATIVE, parameterRepresentations, null, context() );
					} else {
						List<Representation> parameterRepresentations = StreamSupport.stream(functionCall.arguments().spliterator(),false).map( (expr)->NATIVE ).collect( Collectors.toList() ) ;
						invocation = new JavaInvocation( JExpr.invoke( symbol ), NATIVE, parameterRepresentations, null, context() );
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
	
	private JavaInvocation builtinExpr( OperatorType symbol, JavaExpression<?> argument1, JavaExpression<?> argument2 ) {
		return JavaInvocation.builder(context())
				.invoke( env().methodFor( symbol ) )
				.supplying( (r)->argument1, (r)->argument2 )
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
				Supplier<Optional<Representation>> trueRep = trueResult.representation();
				Supplier<Optional<Representation>> falseRep = falseResult.representation();
				Supplier<Optional<Representation>> representation = ()->trueRep.get().flatMap(
						(t)->falseRep.get().flatMap(
							(f)->Optional.of(t.commonRepresentation(f)
							)
						)
					);
				return JavaExpression.from( JOp.cond( testExpression.expr(), trueResult.expr(), falseResult.expr() ), representation );
			}
			
			
		}) ;
	}

	public KeyApplier keyApplier( VariableReference variable ) {
		return new KeyApplierImpl(variable) ;
	}

	public JavaExpression<?> applyKeys( VariableReference variable, JavaExpression<?> target ) {
		return keyApplier(variable).apply(target) ;
	}
	
	public interface KeyApplier {
		boolean hasKeys() ;
		JavaExpression<?> apply( JavaExpression<?> target );
	}
	
	private class KeyApplierImpl implements KeyApplier {
		private List<JavaExpression<?>> m_keys;
		public KeyApplierImpl( VariableReference variable ) {
			m_keys = StreamSupport.stream(variable.keys().spliterator(),true).map( (e)->build(e,STRING) ).collect( Collectors.toList() ) ;
		}
		@Override
		public boolean hasKeys() { return !m_keys.isEmpty() ; }
		@Override
		public JavaExpression<?> apply( JavaExpression<?> target ) {
			return m_keys.stream().reduce( target, (t,k)->
				JavaInvocation.builder(context())
					.on( t )
					.invoke( env().methodFor(VALUE_INDEX) )
					.supplying( (r)->k )
					.build()
			) ;
		}
	}
}