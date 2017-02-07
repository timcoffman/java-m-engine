package edu.vanderbilt.clinicalsystems.m.lang.text.statement;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.sun.codemodel.JBlock;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.text.CommandJavaStatementBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.JavaInvocation;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBuilderClassContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaExpressionBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.SymbolUsage;

public class ParseBuilder extends CommandJavaStatementBuilder {


	public ParseBuilder( RoutineJavaBuilderClassContext builderContext, SymbolUsage outerSymbolUsage, RoutineJavaExpressionBuilder expressionBuilder ) {
		super( builderContext, expressionBuilder ) ;
	}
	
	@Override protected Builder<JBlock> analyze( CommandType commandType, ExpressionList expressionList, Block innerBlock ) {
		List<Builder<JBlock>> execBuilders = StreamSupport.stream(expressionList.elements().spliterator(),false)
				.map( this::analyzeOneExecution )
				.collect( Collectors.toList() )
				;
			return (b)->execBuilders.forEach( (e)->e.build(b) );
	}
	
	private Builder<JBlock> analyzeOneExecution( Expression expression ) {
		return JavaInvocation.builder(context())
				.invoke( context().env().methodFor(CommandType.EXECUTE))
				.supplying( analyze(expression) )
				::build ;
	}
}
