package edu.vanderbilt.clinicalsystems.m.lang.text.statement;

import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeCommandType.VALUE_ASSIGN;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeFunctionType.VALUE_INDEX;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.ANY;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.STRING;

import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

import edu.vanderbilt.clinicalsystems.m.core.VariableContext;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Element;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Assignment;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.DeclarationList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Destination;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.VariableList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.IndirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.CommandJavaStatementBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.JavaExpression;
import edu.vanderbilt.clinicalsystems.m.lang.text.JavaInvocation;
import edu.vanderbilt.clinicalsystems.m.lang.text.Representation;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBuilderContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaExpressionBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.SymbolUsage;

public class VariableBuilder extends CommandJavaStatementBuilder {

	private final SymbolUsage m_symbolUsage ;
	
	public VariableBuilder( RoutineJavaBuilderContext builderContext, SymbolUsage symbolUsage, JBlock block, RoutineJavaExpressionBuilder expressionBuilder ) {
		super( builderContext, block, expressionBuilder ) ;
		m_symbolUsage = symbolUsage ;
	}

	@Override protected void build( CommandType commandType, DeclarationList declarationList, Block block ) {
		expect( CommandType.NEW, commandType, declarationList);
		for ( DirectVariableReference variable : declarationList.elements() ) {
			String symbol = context().symbolForIdentifier(variable.variableName());
			JVar decl = block().decl( JMod.NONE, codeModel()._ref(valueClass()), symbol, nullExpr().expr() ) ;
			m_symbolUsage.declaredAs(decl, Representation.ANY);
		}
	}
	
	@Override protected void build( CommandType commandType, VariableList variableList, Block block ) {
		expect( CommandType.KILL, commandType, variableList);
		for ( VariableReference variable : variableList.elements() ) {
			buildVariableAssignment(variable, nullExpr() );
		}
	}

	@Override protected void build( CommandType commandType, AssignmentList assignmentList, Block block ) {
		if ( CommandType.SET == commandType )
			buildAssignments( commandType, assignmentList, block ) ;
		else if ( CommandType.MERGE == commandType )
			buildMerges( commandType, assignmentList, block ) ;
	}
	
	private JavaExpression<?> applyKeys( JavaExpression<?> target, VariableReference variable ) {
		Iterator<Expression> keyIterator = variable.keys().iterator();
		while ( keyIterator.hasNext() )
			target = JavaInvocation.builder(context())
				.on( target )
				.invoke( env().methodFor(VALUE_INDEX) )
				.supplying( expr(keyIterator.next()) )
				.build();
		return target ;
	}
	
	private void buildVariableAssignment( VariableReference variable, JavaExpression<?> source ) {
		variable.visit( new VariableReference.Visitor<Void>() {

			@Override
			public Void visitVariableReference( VariableReference variable) {
				throw new UnsupportedOperationException( "variable type \"" + variable.getClass() + "\" not supported for assignment" ) ;	
			}
			
			@Override
			public Void visitBuiltinVariableReference(BuiltinVariableReference variable) {
				JavaExpression<?> target = JavaInvocation.builder(context())
						.invoke( env().methodFor(variable.builtinVariable()) )
						.build();
				target = applyKeys( target, variable ) ;
				JavaInvocation.builder(context())
					.on( target )
					.invoke( env().methodFor(VALUE_ASSIGN) )
					.supplying( source )
					.build( block() );
				
				return null ;
			}
			
			@Override
			public Void visitDirectVariableReference(DirectVariableReference variable) {
				String symbol = context().symbolForIdentifier(variable.variableName());
				JavaExpression<? extends JAssignmentTarget> assignableTarget = JavaExpression.from( JExpr.ref( symbol ), source.representation() ) ;
				
				switch ( assignableTarget.representation() ) {
				case ANY:
					JavaExpression<?> target = applyKeys( assignableTarget, variable ) ;
					JavaInvocation.builder(context())
						.on( target )
						.invoke( env().methodFor(VALUE_ASSIGN) )
						.supplying( source )
						.build( block() );
					break ;
				default:
					block().assign( assignableTarget.expr() , source.expr() ) ;
					break ;
				}
				return null ;
			}
			
			@Override
			public Void visitIndirectVariableReference(IndirectVariableReference variable) {
				
				JavaExpression<?> target = JavaInvocation.builder(context())
						.on(VariableContext.class)
						.invoke( "lookup" )
						.accepting( java.lang.String.class )
						.supplying( expr(variable.variableNameProducer(), STRING) )
						.build();
				target = applyKeys( target, variable ) ;
				JavaInvocation.builder(context())
						.on( target )
						.invoke( env().methodFor(VALUE_ASSIGN) )
						.supplying( source )
						.build( block() );
				return null ;
			}
			
		}) ;
	}

	public void buildAssignments( CommandType commandType, AssignmentList assignmentList, Block block ) {
		for ( Assignment assignment : assignmentList.elements() ) {
			final JavaExpression<?> source = expr( assignment.source() );
			
			assignment.destination().visit( new Destination.Visitor<Void>() {

				@Override public Void visitElement(Element element) {
					throw new UnsupportedOperationException( "destination type \"" + assignment.destination().getClass() + "\" not supported" ) ;	
				}

				@Override public Void visitVariableReference( VariableReference variable) {
					buildVariableAssignment(variable, source);
					return null;
				}

				@Override public Void visitBuiltinFunctionCall( BuiltinFunctionCall functionCall) {
//						switch ( functionCall.builtinFunction() ) {
//						case PIECE:
//							
//						default:
//							throw new UnsupportedOperationException( "function type \"" + functionCall.builtinFunction() + "\" not supported" ) ;	
//						}
					
					// ! parameter for the source
					int numberOfParameters = 1 + StreamSupport.stream(functionCall.arguments().spliterator(),false).collect( Collectors.counting() ).intValue() ;
					
					JavaInvocation invocation = JavaInvocation.builder(context())
						.invoke( env().methodFor( functionCall.builtinFunction(), numberOfParameters, true ) )
						.accepting( numberOfParameters )
						.build( block() ) ;
					for (Expression arg : functionCall.arguments())
						invocation.appendArgument( (r)->expr(arg,r) ) ;
					invocation.appendArgument( source ) ;
					return null ;
				}
				
			}) ;
		}
	}
	
	private void buildVariableMerge( VariableReference variable, JavaExpression<?> source ) {
		variable.visit( new VariableReference.Visitor<Void>() {

			@Override
			public Void visitVariableReference( VariableReference variable) {
				throw new UnsupportedOperationException( "variable type \"" + variable.getClass() + "\" not supported for merge" ) ;	
			}
			
			@Override
			public Void visitDirectVariableReference(DirectVariableReference variable) {
				String symbol = context().symbolForIdentifier(variable.variableName());
				JavaExpression<?> target = JavaExpression.from( JExpr.ref( symbol ), ANY ) ;
					
				target = applyKeys( target, variable ) ;
				JavaInvocation.builder(context())
						.on( target )
						.invoke( "merge" )
						.supplying( source )
						.build( block() )
						;
				
				return null ;
			}
			
			@Override
			public Void visitIndirectVariableReference(IndirectVariableReference variable) {
				JavaExpression<?> target = JavaInvocation.builder(context())
						.on(VariableContext.class)
						.invoke( "lookup" )
						.accepting( java.lang.String.class )
						.supplying( expr(variable.variableNameProducer(), STRING) )
						.build();
				target = applyKeys( target, variable ) ;
				JavaInvocation.builder(context())
						.on( target )
						.invoke( env().methodFor(VALUE_ASSIGN) )
						.supplying( source )
						.build( block() );
				return null ;
			}
			
		}) ;
	}
	
	public void buildMerges( CommandType commandType, AssignmentList assignmentList, Block block ) {
		for ( Assignment assignment : assignmentList.elements() ) {
			final JavaExpression<?> source = expr(assignment.source());
			
			assignment.destination().visit( new Destination.Visitor<Void>() {

				@Override public Void visitElement(Element element) {
					throw new UnsupportedOperationException( "destination type \"" + assignment.destination().getClass() + "\" not supported" ) ;	
				}

				@Override public Void visitVariableReference( VariableReference variable) {
					buildVariableMerge(variable, source);
					return null;
				}
				
			}) ;
		}
	}

}
