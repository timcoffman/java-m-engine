package edu.vanderbilt.clinicalsystems.m.lang.text.statement;

import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.ReadWriteCodeType.COLUMN_ALIGN;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.ReadWriteCodeType.NEWLINE;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.ReadWriteCodeType.PAGEFEED;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.INTEGER;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.STRING;

import java.util.ArrayList;
import java.util.List;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;

import edu.vanderbilt.clinicalsystems.m.core.annotation.support.ReadWriteCodeType;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.CarriageReturnCommand;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ColumnCommand;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutput;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutputList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutputVariable;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.OutputExpression;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.PageFeedCommand;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.text.CommandJavaStatementBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.JavaExpression;
import edu.vanderbilt.clinicalsystems.m.lang.text.JavaInvocation;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBuilderContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaExpressionBuilder;
public class GenericBuilder extends CommandJavaStatementBuilder {

	public GenericBuilder( RoutineJavaBuilderContext builderContext, JBlock block, RoutineJavaExpressionBuilder expressionBuilder ) {
		super( builderContext, block, expressionBuilder ) ;
	}
	
	@Override protected void build( CommandType commandType, ExpressionList expressionList, Block block ) {
		JavaInvocation invocation = invoke(commandType) ;
		for ( Expression expression : expressionList.elements() )
			invocation.appendArgument( expr(expression) ) ;
	}

	@Override protected void build( CommandType commandType, InputOutputList inputOutputList, Block block ) {
		final List<JavaExpression<?>> inputOutputCommands = new ArrayList<JavaExpression<?>>() ;
		for ( InputOutput inputOutput : inputOutputList.elements() )
			inputOutputCommands.add( inputOutput.visit( new InputOutput.Visitor<JavaExpression<?>>() {

				@Override
				public JavaExpression<?> visitInputOutput(InputOutput inputOutput) {
					return JavaExpression.from( JExpr.lit(inputOutput.toString()), STRING ) ;
				}
				
				private JavaExpression<?> invocationFor( ReadWriteCodeType readWriteCodeType, JavaExpression<?> ... arguments ) {
					return JavaInvocation.builder(context())
							.invoke(env().methodFor(readWriteCodeType))
							.supplying(arguments)
							.build() ;
				}

				@Override
				public JavaExpression<?> visitCarriageReturnCommand( CarriageReturnCommand carriageReturnCommand ) {
					return invocationFor(NEWLINE);
				}

				@Override
				public JavaExpression<?> visitPageFeedCommand( PageFeedCommand pageFeedCommand ) {
					return invocationFor(PAGEFEED);
				}

				@Override
				public JavaExpression<?> visitColumnCommand( ColumnCommand columnCommand ) {
					return invocationFor(COLUMN_ALIGN, JavaExpression.from( JExpr.lit(columnCommand.column()), INTEGER ));
				}

				@Override
				public JavaExpression<?> visitInputOutputVariable(InputOutputVariable inputOutputVariable) {
					return expr( inputOutputVariable.variable() ) ;
				}

				@Override
				public JavaExpression<?> visitOutputExpression(OutputExpression outputExpression) {
					return expr(outputExpression.expression()) ; 
				}
				
			}) ) ;
		
		JavaInvocation.builder(context())
				.invoke( env().methodFor(commandType) )
				.accepting( java.lang.Object[].class)
				.supplying( inputOutputCommands.toArray( new JavaExpression<?>[0] ) )
				.build( block() );
		
	}
	
	private JavaInvocation invoke(CommandType commandType) {
		return JavaInvocation.builder(context())
				.invoke( env().methodFor(commandType) )
				.build( block() );
	}
	
}
