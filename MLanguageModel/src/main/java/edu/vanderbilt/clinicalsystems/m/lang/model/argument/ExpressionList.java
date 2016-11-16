package edu.vanderbilt.clinicalsystems.m.lang.model.argument;

import java.util.Arrays;
import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;


public class ExpressionList extends ElementListArgument<Expression> {

	public ExpressionList() {
		super() ;
	}
	
	public ExpressionList( Expression ... expressions ) {
		super( Arrays.asList(expressions) ) ;
	}

	public ExpressionList( List<? extends Expression> elements ) {
		super(elements) ;
	}

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	}

}
