package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class IndirectVariableReference extends VariableReference {

	private final Expression m_variableNameProducer ;

	public IndirectVariableReference( Expression variableNameProducer ) {
		super(ReferenceStyle.INDIRECT ) ;
		m_variableNameProducer = variableNameProducer ;
	}
	
	public IndirectVariableReference( Expression variableNameProducer, List<Expression> keys ) {
		super( ReferenceStyle.INDIRECT, keys ) ;
		m_variableNameProducer = variableNameProducer ;
	}
	
	public Expression variableNameProducer() { return m_variableNameProducer; }

	@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitIndirectVariableReference(this); }

	@Override protected String unformattedVariableNameRepresentation() {
		return m_variableNameProducer.unformattedRepresentation() ;
	}

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write( this ) ;
	}
}
