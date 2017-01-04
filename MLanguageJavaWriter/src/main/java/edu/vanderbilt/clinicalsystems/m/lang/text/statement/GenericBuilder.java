package edu.vanderbilt.clinicalsystems.m.lang.text.statement;

import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.ReadWriteCodeType.NEWLINE;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.ReadWriteCodeType.PAGEFEED;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.INTEGER;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.STRING;

import java.util.List;
import java.util.function.Function;
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
import edu.vanderbilt.clinicalsystems.m.lang.text.Representation;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBuilderClassContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaExpressionBuilder;
public class GenericBuilder extends CommandJavaStatementBuilder {

	public GenericBuilder( RoutineJavaBuilderClassContext builderContext, RoutineJavaExpressionBuilder expressionBuilder ) {
		super( builderContext, expressionBuilder ) ;
	}
	
	@Override protected Builder<JBlock> analyze( CommandType commandType, ExpressionList expressionList, Block innerBlock ) {
		List<Function<Representation,JavaExpression<?>>> arguments = analyze(expressionList.elements()) ;
		return JavaInvocation.builder(context())
				.invoke( env().methodFor(commandType) )
				.supplying( arguments )
				::build
				;
	}
	

	@Override protected Builder<JBlock> analyze( CommandType commandType, InputOutputList inputOutputList, Block innerBlock ) {
		
		List<Function<Representation,JavaExpression<?>>> arguments = StreamSupport.stream(inputOutputList.elements().spliterator(),false).map( (io)->{
			return io.visit( new InputOutput.Visitor<Function<Representation,JavaExpression<?>>>() {

				@Override
				public Function<Representation,JavaExpression<?>> visitInputOutput(InputOutput inputOutput) {
					return (r)->JavaExpression.from( JExpr.lit(inputOutput.toString()), STRING ) ;
				}
				
				@Override
				public Function<Representation,JavaExpression<?>> visitCarriageReturnCommand( CarriageReturnCommand carriageReturnCommand ) {
					return (r)->JavaInvocation.builder(context())
							.invoke(env().methodFor(NEWLINE))
							.acceptingNothing()
							.build() ;
				}

				@Override
				public Function<Representation,JavaExpression<?>> visitPageFeedCommand( PageFeedCommand pageFeedCommand ) {
					return (r)->JavaInvocation.builder(context())
							.invoke(env().methodFor(PAGEFEED))
							.acceptingNothing()
							.build() ;
				}

				@Override
				public Function<Representation,JavaExpression<?>> visitColumnCommand( ColumnCommand columnCommand ) {
					JavaExpression<?> columnCount = JavaExpression.from( JExpr.lit(columnCommand.column()), INTEGER ) ;
					return (r)->JavaInvocation.builder(context())
							.invoke(env().methodFor(PAGEFEED))
							.supplying( (dc)->columnCount )
							.build() ;
				}

				@Override
				public Function<Representation,JavaExpression<?>> visitInputOutputVariable(InputOutputVariable inputOutputVariable) {
					return (r)->expr( inputOutputVariable.variable(), r ) ;
				}

				@Override
				public Function<Representation,JavaExpression<?>> visitOutputExpression(OutputExpression outputExpression) {
					return (r)->expr( outputExpression.expression(), r ) ; 
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
