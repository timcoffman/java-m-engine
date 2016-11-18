package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.antlr.v4.runtime.Token;

import edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinSystemVariable;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinVariable;
import edu.vanderbilt.clinicalsystems.m.lang.Compatibility;
import edu.vanderbilt.clinicalsystems.m.lang.ParameterPassMethod;
import edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle;
import edu.vanderbilt.clinicalsystems.m.lang.RoutineAccess;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.ParameterName;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Assignment;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.DeclarationList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutput;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutputList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.LoopDefinition;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.VariableList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinSystemVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinVariableReferenceBase;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.FunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.IndirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.TagReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.AssignmentListContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.DeclarationListContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.ExpressionContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.ExpressionListContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.FormalArgumentListContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.InputOutputListContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.LoopDefinitionContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.ParameterListContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.TaggedRoutineCallContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.VariableListContext;

public class Converter {

	private final String m_routineName ;
	
	public Converter(String routineName) {
		m_routineName = routineName ;
	}
	
	public static final List<Expression> EMPTY_EXPRESSION_LIST = Collections.emptyList() ;
	public static final List<Assignment> EMPTY_ASSIGNMENT_LIST = Collections.emptyList() ;
	public static final List<VariableReference> EMPTY_VARIABLE_LIST = Collections.emptyList() ;
	public static final List<DirectVariableReference> EMPTY_DECLARATION_LIST = Collections.emptyList() ;
	public static final List<ParameterName> EMPTY_FORMAL_ARGUMENT_LIST = Collections.emptyList() ;
	public static final List<InputOutput> EMPTY_INPUT_OUTPUT_LIST = Collections.emptyList() ;
	
	public List<Expression> asList( ExpressionListContext expressionListCtx ) {
		if ( null == expressionListCtx )
			return EMPTY_EXPRESSION_LIST ;
		return StreamSupport.stream(expressionListCtx.expression().spliterator(), false)
				.map( (e)->e.result )
				.collect( Collectors.toList() )
				;
	}
	
	public List<Expression> asList( ParameterListContext expressionListCtx ) {
		if ( null == expressionListCtx )
			return EMPTY_EXPRESSION_LIST ;
		return StreamSupport.stream(expressionListCtx.expression().spliterator(), false)
				.map( (e)->e != null ? e.result : Constant.NULL )
				.collect( Collectors.toList() )
				;
	}
	
	public List<Assignment> asList( AssignmentListContext assignmentListCtx ) {
		if ( null == assignmentListCtx )
			return EMPTY_ASSIGNMENT_LIST ;
		return StreamSupport.stream(assignmentListCtx.assignment().spliterator(), false)
				.map( (a)->a.result )
				.collect( Collectors.toList() )
				;
	}
	
	public List<VariableReference> asList( VariableListContext variableListCtx ) {
		if ( null == variableListCtx )
			return EMPTY_VARIABLE_LIST ;
		return StreamSupport.stream(variableListCtx.variable().spliterator(), false)
				.map( (a)->a.result )
				.collect( Collectors.toList() )
				;
	}
	
	public List<DirectVariableReference> asList( DeclarationListContext declarationListCtx ) {
		if ( null == declarationListCtx )
			return EMPTY_DECLARATION_LIST ;
		return StreamSupport.stream(declarationListCtx.declaration().spliterator(), false)
				.map( (a)->a.result )
				.collect( Collectors.toList() )
				;
	}
	
	public List<ParameterName> asList( FormalArgumentListContext formalArgumentListCtx ) {
		if ( null == formalArgumentListCtx )
			return EMPTY_FORMAL_ARGUMENT_LIST ;
		return StreamSupport.stream(formalArgumentListCtx.formalArgument().spliterator(), false)
				.map( (a)->a.result )
				.collect( Collectors.toList() )
				;
	}
	
	public List<InputOutput> asList( InputOutputListContext inputOutputListCtx ) {
		if ( null == inputOutputListCtx )
			return EMPTY_INPUT_OUTPUT_LIST ;
		return StreamSupport.stream(inputOutputListCtx.inputOutput().spliterator(), false)
				.map( (a)->a.result )
				.collect( Collectors.toList() )
				;
	}
	
	public DirectVariableReference createDirectVariableReference( Scope scope, Token nameToken, ExpressionListContext expressionListCtx ) {
	  	return new DirectVariableReference( scope, nameToken.getText(), asList(expressionListCtx) );
	}
	
	public DirectVariableReference createDirectVariableReference( ParameterPassMethod parameterPassMethod, Scope scope, Token nameToken, ExpressionListContext expressionListCtx ) {
		return new DirectVariableReference( parameterPassMethod, scope, nameToken.getText(), asList(expressionListCtx) );
	}
	
	public BuiltinVariableReferenceBase createBuiltinVariableReference( Scope scope, Token nameToken ) {
		if ( Scope.GLOBAL == scope ) {
			BuiltinSystemVariable builtinVariable = BuiltinSystemVariable.valueOfSymbol( nameToken.getText(), Compatibility.EXTENSION ) ;
			return new BuiltinSystemVariableReference( builtinVariable, EMPTY_EXPRESSION_LIST ) ;
		} else {
			BuiltinVariable builtinVariable = BuiltinVariable.valueOfSymbol( nameToken.getText(), Compatibility.EXTENSION ) ;
			return new BuiltinVariableReference( builtinVariable, EMPTY_EXPRESSION_LIST ) ;
		}
	}
	
	public IndirectVariableReference createIndirectVariableReference( Expression nameProducer, ExpressionListContext expressionListCtx ) {
		return new IndirectVariableReference( nameProducer, asList(expressionListCtx) );
	}
	
	public RoutineFunctionCall createRoutineFunctionCall( Scope scope, ReferenceStyle referenceStyle, Token tagNameToken, Token routineNameToken, ParameterListContext parameterListCtx ) {
		String routineName = routineNameToken == null ? m_routineName : routineNameToken.getText();
		RoutineAccess routineAccess = routineName.equals(m_routineName) ? RoutineAccess.IMPLICIT : RoutineAccess.EXPLICIT ;
	  	TagReference tagReference = new TagReference( Scope.LOCAL, ReferenceStyle.DIRECT, tagNameToken.getText(), routineName, routineAccess ) ;
	  	return new RoutineFunctionCall( tagReference, FunctionCall.Returning.SOME_VALUE, asList(parameterListCtx) );
	}
	
	public BuiltinFunctionCall createBuiltinFunctionCall( Token nameToken, ParameterListContext parameterListCtx ) {
	  	BuiltinFunction builtinFunction = BuiltinFunction.valueOfSymbol( nameToken.getText(), Compatibility.EXTENSION ) ;
	  	return new BuiltinFunctionCall( builtinFunction, asList(parameterListCtx) );
	}
	
	public LoopDefinition createLoopDefinition( Token nameToken, Expression start, ExpressionContext stepByContext, ExpressionContext limitContext ) {
		DirectVariableReference destination = createDirectVariableReference(Scope.LOCAL, nameToken, null) ;
		Expression step = stepByContext == null ? null : stepByContext.result ;
		Expression stop = limitContext == null ? null : limitContext.result ;
		return new LoopDefinition(destination, start, stop, step) ;
	}
	
	public Argument argumentFrom( AssignmentListContext assignmentListCtx ) {
		return new AssignmentList( asList(assignmentListCtx) );
	}
	
	public Argument argumentFrom( VariableListContext variableListCtx ) {
		return new VariableList( asList(variableListCtx) ) ;
	}
	
	public Argument argumentFrom( DeclarationListContext declarationListCtx ) {
		return new DeclarationList( asList(declarationListCtx) ) ;
	}
	
	public Argument argumentFrom( LoopDefinitionContext loopDefinitionCtx ) {
		return loopDefinitionCtx.result ;
	}
	
	public Argument argumentFrom( TaggedRoutineCallContext taggedRoutineCallCtx ) {
		return taggedRoutineCallCtx.result ;
	}
	
	public Argument argumentFrom( ExpressionListContext expressionListCtx ) {
		return new ExpressionList( asList(expressionListCtx) ) ;
	}
	
	public Argument argumentFrom( InputOutputListContext inputOutputListCtx ) {
		return new InputOutputList( asList(inputOutputListCtx) ) ;
	}
	
	public Argument argumentFrom( ExpressionContext expressionCtx ) {
		return new ExpressionList( expressionCtx.result ) ;
	}
	
}
