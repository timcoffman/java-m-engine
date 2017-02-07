package edu.vanderbilt.clinicalsystems.m.lang.text.statement;

import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeCommandType.VALUE_ASSIGN;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeCommandType.VALUE_CLEAR;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.NATIVE;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.STRING;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;

import edu.vanderbilt.clinicalsystems.m.core.VariableContext;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
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
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBuilderClassContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaExpressionBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.SymbolUsage;

public class VariableBuilder extends CommandJavaStatementBuilder {

	private final SymbolUsage m_symbolUsage ;
	
	public VariableBuilder( RoutineJavaBuilderClassContext builderContext, SymbolUsage symbolUsage, RoutineJavaExpressionBuilder expressionBuilder ) {
		super( builderContext, expressionBuilder ) ;
		m_symbolUsage = symbolUsage ;
	}

	@Override protected Builder<JBlock> analyze( CommandType commandType, DeclarationList declarationList, Block innerBlock ) {
		expect( CommandType.NEW, commandType, declarationList);
		
		List<Builder<JBlock>> builders = StreamSupport.stream(declarationList.elements().spliterator(),false)
			.map( DirectVariableReference::variableName )
			.map( context()::symbolForIdentifier )
			.peek( m_symbolUsage::declaredAs ) // without representation
			.map( this::analyzeDeclaration )
			.collect( Collectors.toList() ) ;
		
		return (b)->builders.forEach( (builder)->builder.build(b) );
	}
	
	private Builder<JBlock> analyzeDeclaration( String symbol ) {
		m_symbolUsage.declaredAs( symbol ) ;
		
		Supplier<Optional<Representation>> rep = m_symbolUsage.impliedRepresentation(symbol) ;
		return (b)->{
			Representation representation = rep.get().orElse(Representation.NATIVE);
			JType type = context().typeFor( representation ) ;
			JExpression initialValue = context().initialValueFor( representation ) ;
			b.decl( JMod.NONE, type, symbol, initialValue );
		};
	}
	
	@Override protected Builder<JBlock> analyze( CommandType commandType, VariableList variableList, Block innerBlock ) {
		expect( CommandType.KILL, commandType, variableList);
		
		List<Builder<JBlock>> builders = StreamSupport.stream(variableList.elements().spliterator(),false)
				.map( this::analyzeReset )
				.collect( Collectors.toList() ) ;
			
			return (b)->builders.forEach( (builder)->builder.build(b) );
	}
	
	private Builder<JBlock> analyzeReset( VariableReference variable ) {
		variable.keys().forEach( (e)->expr(e,STRING) );
		return variable.visit( new VariableReference.Visitor<Builder<JBlock> >() {

			@Override
			public Builder<JBlock>  visitVariableReference( VariableReference variable) {
				throw new UnsupportedOperationException( "variable type \"" + variable.getClass() + "\" not supported for resetting" ) ;	
			}
			
			@Override
			public Builder<JBlock>  visitBuiltinVariableReference(BuiltinVariableReference variable) {
				JavaExpression<?> target = JavaInvocation.builder(context())
						.invoke( env().methodFor(variable.builtinVariable()) )
						.build();
				return JavaInvocation.builder(context())
					.on( applyKeys( variable, target ) )
					.invoke( env().methodFor(VALUE_CLEAR) )
					.acceptingNothing()
					::build
					;
			}
			
			@Override
			public Builder<JBlock> visitDirectVariableReference(DirectVariableReference variable) {
				String symbol = context().symbolForIdentifier(variable.variableName());
				Supplier<Optional<Representation>> representation = m_symbolUsage.impliedRepresentation(symbol) ;
				JavaExpression<? extends JAssignmentTarget> assignableTarget = JavaExpression.from( JExpr.ref( symbol ), representation ) ;
				
				KeyApplier keyApplier = keyApplier(variable) ;
				if ( keyApplier.hasKeys() ) {
					return JavaInvocation.builder(context())
						.invoke( env().methodFor( VALUE_CLEAR ) )
						.on( keyApplier.apply(assignableTarget) )
						.acceptingNothing()
						::build;
				}
				
				return (b)->{
					
					switch ( representation.get().get() ) {
					case NATIVE:
						JavaExpression<?> target = applyKeys( variable, assignableTarget ) ;
						JavaInvocation.builder(context())
							.invoke( env().methodFor( VALUE_CLEAR ) )
							.on( target )
							.acceptingNothing()
							.build( b );
					default:
						JExpression initialValue = context().initialValueFor( representation.get().get() ) ;
						b.assign( assignableTarget.expr() , initialValue ) ;
					}
					
				};
			}
			
			@Override
			public Builder<JBlock>  visitIndirectVariableReference(IndirectVariableReference variable) {
				
				JavaExpression<?> target = JavaInvocation.builder(context())
						.on(VariableContext.class)
						.invoke( "lookup" )
						.accepting( java.lang.String.class )
						.supplying( (r)->expr(variable.variableNameProducer(), STRING) )
						.build();
				return JavaInvocation.builder(context())
						.on( applyKeys( variable, target ) )
						.invoke( env().methodFor(VALUE_CLEAR) )
						.acceptingNothing()
						::build;
			}
			
		}) ;

	}

	@Override protected Builder<JBlock> analyze( CommandType commandType, AssignmentList assignmentList, Block innerBlock ) {
		if ( CommandType.SET == commandType )
			return analyzeAssignments( assignmentList ) ;
		else if ( CommandType.MERGE == commandType )
			return analyzeMerges( assignmentList ) ;
		else
			return unexpected(commandType, assignmentList) ;
	}
	
	private Builder<JBlock> analyzeVariableAssignment( VariableReference variable, Expression source ) {
		return variable.visit( new VariableReference.Visitor<Builder<JBlock> >() {

			@Override
			public Builder<JBlock>  visitVariableReference( VariableReference variable) {
				throw new UnsupportedOperationException( "variable type \"" + variable.getClass() + "\" not supported for assignment" ) ;	
			}
			
			@Override
			public Builder<JBlock>  visitBuiltinVariableReference(BuiltinVariableReference variable) {
				JavaExpression<?> target = JavaInvocation.builder(context())
						.invoke( env().methodFor(variable.builtinVariable()) )
						.build();
				target = applyKeys( variable, target ) ;
				return JavaInvocation.builder(context())
					.on( target )
					.invoke( env().methodFor(VALUE_ASSIGN) )
					.supplying( (r)->expr(source,r) )
					::build
					;
			}
			
			@Override
			public Builder<JBlock>  visitDirectVariableReference(DirectVariableReference variable) {
				// fails to allow for assigning to indexed variable leaves
				variableUsedAs(m_symbolUsage, variable, expr(source).representation()); // native repr
				
				String symbol = context().symbolForIdentifier(variable.variableName());
				Supplier<Optional<Representation>> representation = m_symbolUsage.impliedRepresentation( symbol ) ;
				
				return (b)->{
					JavaExpression<? extends JAssignmentTarget> assignableTarget = JavaExpression.from( JExpr.ref( symbol ), representation.get().orElse(Representation.NATIVE) ) ;
					switch ( assignableTarget.representation().get().get() ) {
					case NATIVE:
						JavaExpression<?> target = applyKeys( variable, assignableTarget ) ;
						JavaInvocation.builder(context())
							.on( target )
							.invoke( env().methodFor(VALUE_ASSIGN) )
							.supplying( (r)->expr(source,r) )
							.build(b);
					default:
						b.assign( assignableTarget.expr() , expr(source).expr() ) ;
					}
				} ;
			}
			
			@Override
			public Builder<JBlock>  visitIndirectVariableReference(IndirectVariableReference variable) {
				
				JavaExpression<?> target = JavaInvocation.builder(context())
						.on(VariableContext.class)
						.invoke( "lookup" )
						.accepting( java.lang.String.class )
						.supplying( (r)->expr(variable.variableNameProducer(), STRING) )
						.build();
				target = applyKeys( variable, target ) ;
				return JavaInvocation.builder(context())
						.on( target )
						.invoke( env().methodFor(VALUE_ASSIGN) )
						.supplying( (r)->expr(source,r) )
						::build;
			}
			
		}) ;
	}

	private Builder<JBlock> analyzeAssignments( AssignmentList assignmentList ) {
		
		List<Builder<JBlock>> builders = StreamSupport.stream(assignmentList.elements().spliterator(),false)
				.map( this::analyzeAssignment )
				.collect( Collectors.toList() ) ;
			
		return (b)->builders.forEach( (builder)->builder.build(b) );
	}
	
	private static final String MULTIPLE_ASSIGNMENT_TEMP_VARIABLE_NAME = "__multipleAssignmentTemp" ;
	
	private Builder<JBlock> analyzeAssignment( Assignment assignment ) {
		final Expression source = assignment.source() ;
		
		if ( assignment.destinations().size() == 1 ) {
			return analyzeAssignment( assignment.destinations().get(0), source ) ;
		} else if ( assignment.destinations().isEmpty() ) {
			return (b)->{} ; /* nothing */
		} else {
			DirectVariableReference tempVariable = new DirectVariableReference(Scope.TRANSIENT, MULTIPLE_ASSIGNMENT_TEMP_VARIABLE_NAME);
			Builder<JBlock> tempAssignment ;
			if ( m_symbolUsage.declared(MULTIPLE_ASSIGNMENT_TEMP_VARIABLE_NAME) ) {
				tempAssignment = analyzeAssignment( Destination.wrap( tempVariable ), source ) ;
			} else {
				tempAssignment = analyzeDeclaration( MULTIPLE_ASSIGNMENT_TEMP_VARIABLE_NAME ) ;
			}
			return assignment.destinations().stream()
				.map( (destination)->analyzeAssignment( destination, source ) )
				.reduce( tempAssignment, (a,x)->buildInSequence(a,x) )
				;
		}
		
	}
	
	private Builder<JBlock> analyzeAssignment( Destination<?> destination, Expression source ) {
		return destination.visit( new Destination.Visitor<Builder<JBlock>>() {

			@Override public Builder<JBlock> visitElement(Element element) {
				throw new UnsupportedOperationException( "destination type \"" + destination.getClass() + "\" not supported" ) ;	
			}

			@Override public Builder<JBlock> visitVariableReference( VariableReference variable) {
				return analyzeVariableAssignment(variable, source);
			}

			@Override public Builder<JBlock> visitBuiltinFunctionCall( BuiltinFunctionCall functionCall) {
				List<Function<Representation, JavaExpression<?>>> arguments = analyze(functionCall.arguments());
				
				return (b)->
					JavaInvocation.builder(context())
						.invoke( env().methodFor( functionCall.builtinFunction(), arguments.size(), true ) )
						.supplying( arguments )
						.buildAnd(b)
							.invoke( env().methodForFunctionAssignment() )
							.supplying( (r)->expr( source, r ) )
							.build();
			}
			
		}) ;
	}
	
	private Builder<JBlock> analyzeVariableMerge( VariableReference variable, Expression source ) {
		return variable.visit( new VariableReference.Visitor<Builder<JBlock>>() {

			@Override
			public Builder<JBlock> visitVariableReference( VariableReference variable) {
				throw new UnsupportedOperationException( "variable type \"" + variable.getClass() + "\" not supported for merge" ) ;	
			}
			
			@Override
			public Builder<JBlock> visitDirectVariableReference(DirectVariableReference variable) {
				String symbol = context().symbolForIdentifier(variable.variableName());
				JavaExpression<?> target = JavaExpression.from( JExpr.ref( symbol ), NATIVE ) ;
					
				target = applyKeys( variable, target ) ;
				return JavaInvocation.builder(context())
						.on( target )
						.invoke( "merge" )
						.supplying( (r)->expr(source,r) )
						::build
						;
			}
			
			@Override
			public Builder<JBlock> visitIndirectVariableReference(IndirectVariableReference variable) {
				JavaExpression<?> target = JavaInvocation.builder(context())
						.on(VariableContext.class)
						.invoke( "lookup" )
						.accepting( java.lang.String.class )
						.supplying( (r)->expr(variable.variableNameProducer(), STRING) )
						.build();
				target = applyKeys( variable, target ) ;
				return JavaInvocation.builder(context())
						.on( target )
						.invoke( env().methodFor(VALUE_ASSIGN) )
						.supplying( (r)->expr(source,r) )
						::build;
			}
			
		}) ;
	}
	
	private Builder<JBlock> analyzeMerges( AssignmentList assignmentList ) {
		
		List<Builder<JBlock>> builders = StreamSupport.stream(assignmentList.elements().spliterator(),false)
				.map( this::analyzeMerge )
				.collect( Collectors.toList() ) ;
			
		return (b)->builders.forEach( (builder)->builder.build(b) );
	}
	
	private Builder<JBlock> analyzeMerge( Assignment assignment ) {
		final Expression source = assignment.source();

		if ( assignment.destinations().size() == 1 ) {
			return analyzeMerge( assignment.destinations().get(0), source ) ;
		} else if ( assignment.destinations().isEmpty() ) {
			return (b)->{} ; /* nothing */
		} else {
			DirectVariableReference tempVariable = new DirectVariableReference(Scope.TRANSIENT, "__multipleMergeTemp");
			Builder<JBlock> tempAssignment = analyzeMerge( Destination.wrap( tempVariable ), source ) ;
			return assignment.destinations().stream()
				.map( (destination)->analyzeMerge( destination, source ) )
				.reduce( tempAssignment, (a,x)->buildInSequence(a,x) )
				;
		}
}
	
	private Builder<JBlock> analyzeMerge( Destination<?> destination, Expression source ) {
		
		return destination.visit( new Destination.Visitor<Builder<JBlock>>() {

			@Override public Builder<JBlock> visitElement(Element element) {
				throw new UnsupportedOperationException( "destination type \"" + destination.getClass() + "\" not supported" ) ;	
			}

			@Override public Builder<JBlock> visitVariableReference( VariableReference variable) {
				return analyzeVariableMerge(variable, source);
			}
			
		}) ;
	}

}
