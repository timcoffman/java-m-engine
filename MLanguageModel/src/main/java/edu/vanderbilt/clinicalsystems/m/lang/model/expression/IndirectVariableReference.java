package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.List;

import edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class IndirectVariableReference extends VariableReference {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Expression m_variableNameProducer ;

	public IndirectVariableReference( Expression variableNameProducer ) {
		super(ReferenceStyle.INDIRECT ) ;
		m_variableNameProducer = variableNameProducer ;
	}
	
	public IndirectVariableReference( Expression variableNameProducer, List<Expression> keys ) {
		super( ReferenceStyle.INDIRECT, keys ) ;
		m_variableNameProducer = variableNameProducer ;
	}
	
	@Override
	protected IndirectVariableReference copyWithKeys( List<Expression> keys ) {
		return new IndirectVariableReference( m_variableNameProducer, keys ) ;
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
	
	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) return true ;
		if ( !super.equals(this)) return false ;
		if ( !(obj instanceof IndirectVariableReference) ) return false ;
		IndirectVariableReference variable = (IndirectVariableReference)obj ;
		return
			m_variableNameProducer.equals( variable.m_variableNameProducer )
			;
	}

}
