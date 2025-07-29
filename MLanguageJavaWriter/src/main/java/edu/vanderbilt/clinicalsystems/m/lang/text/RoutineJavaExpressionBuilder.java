package edu.vanderbilt.clinicalsystems.m.lang.text;

import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeFunctionType.VALUE_INDEX;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.BOOLEAN;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.DECIMAL;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.INTEGER;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.NATIVE;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.NUMERIC;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.STRING;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.ConditionalExpression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.IndirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.MatchPattern;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.UnaryOperation;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;
import edu.vanderbilt.clinicalsystems.m.text.repr.MethodParameter;
import edu.vanderbilt.clinicalsystems.m.text.repr.MethodSymbol;
import edu.vanderbilt.clinicalsystems.m.text.repr.OperationNode;
import edu.vanderbilt.clinicalsystems.m.text.repr.RepresentationInference.SymbolScopeSearchStrategy;
import edu.vanderbilt.clinicalsystems.m.text.repr.RepresentationNode;
import edu.vanderbilt.clinicalsystems.m.text.repr.SymbolScope;
import edu.vanderbilt.clinicalsystems.m.text.repr.VariableSymbol;

public class RoutineJavaExpressionBuilder extends RoutineJavaBuilder<RoutineJavaBuilderClassContext> {
	
	private final SymbolScope m_outerSymbolScope ;
	private final JavaExpression<?> m_nullExpr ;

	public interface SymbolScopeListener {
		void usedAs( String symbol, Supplier<Optional<Representation>> representation ) ;
	}
	
	public RoutineJavaExpressionBuilder( RoutineJavaBuilderClassContext builderContext, SymbolScope outerSymbolScope ) {
		super(builderContext) ;
		m_outerSymbolScope = outerSymbolScope ;
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
			
			private VariableSymbol findOrCreateVariableSymbol( SymbolScope scope, String name ) {
				return
					m_outerSymbolScope.variableSymbolFor(name,SymbolScopeSearchStrategy.ENCLOSING)
					.orElseGet( ()->scope.createVariable(name) )
					;
			}
			
			@Override
			public JavaExpression<?> visitDirectVariableReference( DirectVariableReference variable ) {
				variableUsedAs(m_outerSymbolScope, variable, expectedRepresentation);
				
				VariableSymbol symbol = findOrCreateVariableSymbol(m_outerSymbolScope, variable.variableName());
				JavaExpression<?> target = JavaExpression.from( JExpr.ref( symbol.getName() ), symbol );
				return applyKeys(variable, target) ;
			}
			
			@Override
			public JavaExpression<?> visitIndirectVariableReference( IndirectVariableReference variable ) {
				JavaExpression<?> target = JavaInvocation.builder(context())
						.on(VariableContext.class)
						.invoke("lookup")
						.accepting( java.lang.String.class )
						.supplying( build( variable.variableNameProducer(), STRING ) ) 
						.build() ;
				return applyKeys(variable,target) ;
			}

			
			@Override
			public JavaExpression<?> visitBuiltinVariableReference( BuiltinVariableReference variable ) {
				JavaExpression<?> target = JavaInvocation.builder(context())
						.invoke( env().methodFor(variable.builtinVariable()) )
						.build();
				return applyKeys( variable, target ) ;
			}

			private JavaExpression<?> literalInteger( Constant constant ) {
				long longValue = constant.toLong() ;
				if ( longValue < Integer.MAX_VALUE && longValue > Integer.MIN_VALUE )
					return JavaExpression.from( JExpr.lit( (int)constant.toLong() ), env().representationInference().createConstantValue((int)constant.toLong(), INTEGER) );
				else
					return JavaExpression.from( JExpr.lit( constant.toLong() ), env().representationInference().createConstantValue(constant.toLong(), INTEGER) );
			}
			
			private JavaExpression<?> literalDecimal( Constant constant ) {
				return JavaExpression.from( JExpr.lit( constant.toDouble() ), env().representationInference().createConstantValue(constant.toDouble(), DECIMAL) );
			}
			
			@Override
			public JavaExpression<?> visitConstant(Constant constant) {
				if ( constant.representsNull() )
					return m_nullExpr ;
				
				switch ( expectedRepresentation.get().orElse(NATIVE) ) {
				case STRING:
					return JavaExpression.from( JExpr.lit( constant.value() ), env().representationInference().createConstantValue(constant.value(), STRING) );
				case BOOLEAN:
					return JavaExpression.from( JExpr.lit( constant.toBoolean() ), env().representationInference().createConstantValue(constant.toBoolean(), BOOLEAN) );
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
						return JavaExpression.from( JExpr.lit( constant.value() ), env().representationInference().createConstantValue(constant.value(), STRING) );
				}
			}

			@Override public JavaExpression<?> visitMatchPattern( MatchPattern matchPattern ) {
				return JavaExpression.from( JExpr.lit( matchPattern.toString() ), env().representationInference().createConstantValue(matchPattern.toString(), STRING) ); /* whoah, really? */
			}

			private JavaExpression<?> buildOp( BinaryOperation operation, BiFunction<JExpression,JExpression,JExpression> f, Representation producingRep, Representation lhsRep, Representation rhsRep) {
				JavaExpression<?> lhs = build( operation.leftHandSide(), lhsRep ) ;
				JavaExpression<?> rhs = build( operation.rightHandSide(), rhsRep ) ;
				JExpression expr = f.apply(lhs.expr(),rhs.expr()) ;
				JavaExpression<JExpression> result = JavaExpression.from(expr, lhs.representationNode().combinesWith( rhs.representationNode() ) );
				if ( null != lhsRep ) {
					lhs.representationNode().usedAs( lhsRep ) ;
				}
				if ( null != rhsRep ) {
					rhs.representationNode().usedAs( rhsRep ) ;
				}
				if ( null != producingRep ) {
					result.representationNode().usedAs( producingRep ) ;
				}
				return result ;
			}
			
			private JavaExpression<?> buildOp( UnaryOperation operation, Function<JExpression,JExpression> f, Representation producingRep, Representation operandRep) {
				JavaExpression<?> operand = build( operation.operand(), operandRep ) ;
				JExpression expr = f.apply(operand.expr()) ;
				return JavaExpression.from(expr, operand.representationNode().transformedInto( producingRep ) ) ;
			}
			
			private JavaExpression<?> buildIndirection( Expression operand ) {
				JavaExpression<?> target = JavaInvocation.builder(context())
						.on(VariableContext.class)
						.invoke("lookup")
						.accepting( java.lang.String.class )
						.supplying( build( operand, STRING ) )
						.build() ;
				return target ;
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
				case INDIRECTION:
					return buildIndirection( operation.operand() ) ; 
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
				
				SymbolScope classScope = m_outerSymbolScope.enclosingClass().getDeclarationScope() ;
				int numberOfArguments = StreamSupport.stream(functionCall.arguments().spliterator(),true).collect(Collectors.counting()).intValue() ;
				Optional<MethodSymbol> methodSymbol = classScope.methodSymbolFor( symbol, numberOfArguments ) ;

				List<JavaExpression<?>> parameters = new ArrayList<>() ;
				List<RepresentationNode> parameterRepresentationNodes = new ArrayList<>() ;
				for (Expression arg : functionCall.arguments()) {
					JavaExpression<?> expr = build(arg) ;
					parameters.add( expr ) ;
					parameterRepresentationNodes.add( expr.representationNode() ) ;
				}
				
				JavaInvocation invocation ;
				if ( null == routineName && methodSymbol.isPresent() ) {
					/* unspecified library and found internal, or specified internal */
					
					int position = 0 ;
					for ( RepresentationNode representationNode : parameterRepresentationNodes ) {  
						MethodParameter methodParameter = methodSymbol.get().parameter(position++) ;
						methodParameter.isAssigned( representationNode );
					}
					invocation = new JavaInvocation( JExpr.invoke( symbol ), methodSymbol.get(), parameterRepresentationNodes, null, context() );
					
				} else {
					/* expected external */
					
					Method method = env().methodFor(routineName, tagName ) ;
					if ( null != method ) {
						invocation = JavaInvocation.builder(context()).invoke(method).accepting(numberOfArguments).build();
					} else if ( null != routineName ) {
						invocation = new JavaInvocation( codeModel().ref( routineName ).staticInvoke( symbol ), env().representationInference().createUnknownNode(), parameterRepresentationNodes, null, context() );
					} else {
						invocation = new JavaInvocation( JExpr.invoke( symbol ), env().representationInference().createUnknownNode(), parameterRepresentationNodes, null, context() );
					}
					
				}

				for (JavaExpression<?> parameter : parameters)
					invocation.appendArgument( parameter ) ;
				
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
			invocation.appendArgument( build(arg) ) ;
		return invocation ;
	}
	
	private JavaInvocation builtinExpr( OperatorType symbol, JavaExpression<?> argument1, JavaExpression<?> argument2 ) {
		return JavaInvocation.builder(context())
				.invoke( env().methodFor( symbol ) )
				.supplying( argument1, argument2 )
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
				OperationNode operationNode = trueResult.representationNode().or( falseResult.representationNode() ) ;
				return JavaExpression.from( JOp.cond( testExpression.expr(), trueResult.expr(), falseResult.expr() ), operationNode );
			}
			
			
		}) ;
	}

	public KeyApplier keyApplier( VariableReference variable) {
		return new KeyApplierImpl(variable) ;
	}

	public JavaExpression<?> applyKeys( VariableReference variable, JavaExpression<?> target ) {
		return keyApplier(variable).apply(target) ;
	}
	
	public interface KeyApplier {
		boolean hasKeys() ;
		JavaExpression<?> apply( JavaExpression<?> target );
	}
	
	private static class KeySpec {
		private final JavaExpression<?> m_key ;
		public KeySpec(JavaExpression<?> key) { Objects.requireNonNull(key); m_key = key; }
		public JavaExpression<?> key() { return m_key; }
		public @Override String toString() { return "[" + m_key.toString() + "]"; } 
	}
	
	private class KeyApplierImpl implements KeyApplier {
		private final List<KeySpec> m_keySpecs = new ArrayList<>();
		public KeyApplierImpl( VariableReference variable ) {
			Iterator<Expression> i = variable.keys().iterator() ;
			while ( i.hasNext() ) {
				Expression expression = i.next();
				m_keySpecs.add( new KeySpec(
						build(expression,STRING)
				)) ;
			}
		}
		@Override
		public boolean hasKeys() { return !m_keySpecs.isEmpty() ; }
		@Override
		public JavaExpression<?> apply( JavaExpression<?> target ) {
			if ( hasKeys() ) {
				target.representationNode().usedAs(NATIVE);
			}
			for ( KeySpec keySpec : m_keySpecs )
				target = JavaInvocation.builder(context())
					.on( target )
					.invoke( env().methodFor(VALUE_INDEX) )
					.accepting( String.class )
					.supplying( keySpec.key() )
					.build()
					;
			return target ;
		}
	}
}