package edu.vanderbilt.clinicalsystems.m.lang.text.statement;

import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.ReadWriteCodeType.NEWLINE;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.ReadWriteCodeType.PAGEFEED;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.INTEGER;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.STRING;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;

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
import edu.vanderbilt.clinicalsystems.m.lang.text.CommandJavaStatementBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.JavaExpression;
import edu.vanderbilt.clinicalsystems.m.lang.text.JavaInvocation;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBuilderClassContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaExpressionBuilder;
public class GenericBuilder extends CommandJavaStatementBuilder {

	public GenericBuilder( RoutineJavaBuilderClassContext builderContext, RoutineJavaExpressionBuilder expressionBuilder ) {
		super( builderContext, expressionBuilder ) ;
	}
	
	@Override protected Builder<JBlock> analyze( CommandType commandType, ExpressionList expressionList, Block innerBlock ) {
		List<JavaExpression<?>> arguments = analyze(expressionList.elements()) ;
		return JavaInvocation.builder(context())
				.invoke( env().methodFor(commandType) )
				.supplying( arguments )
				::build
				;
	}
	

	@Override protected Builder<JBlock> analyze( CommandType commandType, InputOutputList inputOutputList, Block innerBlock ) {
		
		List<JavaExpression<?>> arguments = StreamSupport.stream(inputOutputList.elements().spliterator(),false).map( (io)->{
			return io.visit( new InputOutput.Visitor<JavaExpression<?>>() {

				@Override
				public JavaExpression<?> visitInputOutput(InputOutput inputOutput) {
					return JavaExpression.from( JExpr.lit(inputOutput.toString()), env().representationInference().createConstantValue(inputOutput.toString(), STRING) ) ;
				}
				
				@Override
				public JavaExpression<?> visitCarriageReturnCommand( CarriageReturnCommand carriageReturnCommand ) {
					return JavaInvocation.builder(context())
							.invoke(env().methodFor(NEWLINE))
							.acceptingNothing()
							.build() ;
				}

				@Override
				public JavaExpression<?> visitPageFeedCommand( PageFeedCommand pageFeedCommand ) {
					return JavaInvocation.builder(context())
							.invoke(env().methodFor(PAGEFEED))
							.acceptingNothing()
							.build() ;
				}

				@Override
				public JavaExpression<?> visitColumnCommand( ColumnCommand columnCommand ) {
					JavaExpression<?> columnCount = JavaExpression.from( JExpr.lit(columnCommand.column()), env().representationInference().createConstantValue(columnCommand.column(), INTEGER) ) ;
					return JavaInvocation.builder(context())
							.invoke(env().methodFor(PAGEFEED))
							.supplying( columnCount )
							.build() ;
				}

				@Override
				public JavaExpression<?> visitInputOutputVariable(InputOutputVariable inputOutputVariable) {
					return expr( inputOutputVariable.variable() ) ;
				}

				@Override
				public JavaExpression<?> visitOutputExpression(OutputExpression outputExpression) {
					return expr( outputExpression.expression() ) ; 
				}
				
			} );
		} ).collect( Collectors.toList() );
		
		return JavaInvocation.builder(context())
				.invoke( env().methodFor(commandType) )
				.accepting( java.lang.Object[].class)
				.supplying( arguments )
				::build;
		
	}
	
}
