package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.antlr.v4.runtime.Token;

import edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction;
import edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle;
import edu.vanderbilt.clinicalsystems.m.lang.RoutineAccess;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Assignment;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.FunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.TagReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.AssignmentListContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.DeclarationListContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.ExpressionContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.ExpressionListContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.LoopDefinitionContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.TaggedRoutineCallContext;

public class Converter {

	private final String m_routineName ;
	
	public Converter(String routineName) {
		m_routineName = routineName ;
	}
	
	public static final List<Expression> EMPTY_EXPRESSION_LIST = Collections.emptyList() ;
	public static final List<Assignment> EMPTY_ASSIGNMENT_LIST = Collections.emptyList() ;
	
	public List<Expression> asList( ExpressionListContext expressionListCtx ) {
		if ( null == expressionListCtx )
			return EMPTY_EXPRESSION_LIST ;
		return StreamSupport.stream(expressionListCtx.expression().spliterator(), false)
				.map( (e)->e.result )
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
	
	
	
	public VariableReference createVariableReference( Scope scope, ReferenceStyle referenceStyle, Token nameToken, ExpressionListContext expressionListCtx ) {
	  	return new VariableReference( scope, referenceStyle, nameToken.getText(), asList(expressionListCtx) );
	}
	
	public RoutineFunctionCall createRoutineFunctionCall( Scope scope, ReferenceStyle referenceStyle, Token tagNameToken, Token routineNameToken, ExpressionListContext expressionListCtx ) {
		String routineName = routineNameToken == null ? m_routineName : routineNameToken.getText();
		RoutineAccess routineAccess = routineName.equals(m_routineName) ? RoutineAccess.IMPLICIT : RoutineAccess.EXPLICIT ;
	  	TagReference tagReference = new TagReference( Scope.LOCAL, ReferenceStyle.DIRECT, tagNameToken.getText(), routineName, routineAccess ) ;
	  	return new RoutineFunctionCall( tagReference, FunctionCall.Returning.SOME_VALUE, asList(expressionListCtx) );
	}
	
	public BuiltinFunctionCall createBuiltinFunctionCall( Token nameToken, ExpressionListContext expressionListCtx ) {
	  	BuiltinFunction builtinFunction = BuiltinFunction.valueOfSymbol( nameToken.getText() ) ;
	  	return new BuiltinFunctionCall( builtinFunction, asList(expressionListCtx) );
	}
	
	public Argument argumentFrom( AssignmentListContext assignmentListCtx ) {
		return new AssignmentList( asList(assignmentListCtx) );
	}
	
	public Argument argumentFrom( DeclarationListContext declarationListCtx ) {
		
	}
	
	public Argument argumentFrom( LoopDefinitionContext loopDefinitionCtx ) {
		
	}
	
	public Argument argumentFrom( TaggedRoutineCallContext taggedRoutineCallListCtx ) {
		
	}
	
	public Argument argumentFrom( ExpressionListContext expressionListCtx ) {
		return new ExpressionList( asList(expressionListCtx) ) ;
	}
	
	public Argument argumentFrom( ExpressionContext expressionCtx ) {
		return new ExpressionList( expressionCtx.result ) ;
	}
	
}
