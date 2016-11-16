package edu.vanderbilt.clinicalsystems.m.lang.text;

import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.Comment;
import edu.vanderbilt.clinicalsystems.m.lang.model.InlineBlock;
import edu.vanderbilt.clinicalsystems.m.lang.model.MultilineBlock;
import edu.vanderbilt.clinicalsystems.m.lang.model.ParameterName;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.Tag;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Assignment;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.DeclarationList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.LoopDefinition;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Nothing;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BinaryOperation;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.InvalidExpression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.TagReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.UnaryOperation;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;

public interface RoutineWriter {

	void write( Routine routine ) throws RoutineWriterException ;
	
	void write( InlineBlock block ) throws RoutineWriterException ;
	void write( MultilineBlock block ) throws RoutineWriterException ;
	
	void write( Tag tag ) throws RoutineWriterException ;
	void write( ParameterName parameterName );

	void write( Command command ) throws RoutineWriterException ;

	void write( Comment comment ) throws RoutineWriterException ;
	
	/* arguments */
	void write( Nothing nothing ) throws RoutineWriterException ;
	void write( AssignmentList assignment ) throws RoutineWriterException ;
	void write( LoopDefinition loopDefinition ) throws RoutineWriterException;
	void write( DeclarationList declaration ) throws RoutineWriterException ;
	void write( ExpressionList expressions ) throws RoutineWriterException ;
	void write( TaggedRoutineCall taggedRoutineCall ) throws RoutineWriterException ;
	void write( Assignment assignment ) throws RoutineWriterException ;

	/* expressions */
	void write( InvalidExpression invalidExpression ) throws RoutineWriterException ;
	void write( RoutineFunctionCall routineFunctionCall ) throws RoutineWriterException ;
	void write( BuiltinFunctionCall builtinFunctionCall ) throws RoutineWriterException ;
	void write( BinaryOperation operation ) throws RoutineWriterException ;
	void write( UnaryOperation operation ) throws RoutineWriterException ;
	void write( VariableReference variable) throws RoutineWriterException ;
	void write( Constant constant ) throws RoutineWriterException ;

	void write(TagReference tag) throws RoutineWriterException ;

}
