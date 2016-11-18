package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;

public abstract class StandardExecutor implements Executor {

	protected Constant m_result = null;
	protected EngineException m_exception = null;

	@Override
	public Constant result() { return m_result ; }

	protected ExecutionResult produceResult(Constant result) { m_result = result ; return ExecutionResult.QUIT ; }

	@Override
	public EngineException error() { return m_exception ; }

	protected ExecutionResult caughtException(EngineException ex) {
		m_exception = ex ;
		return ExecutionResult.ERROR ;
	}

	protected void throwException() throws EngineException {
		if ( null == m_exception )
			throw new RuntimeException( "cannot throw EngineException; none were caught" ) ;
		throw m_exception ;
	}

	protected ExecutionResult delegateExecutionTo( Command command, Executor executor ) {
		ExecutionResult result = executor.execute(command) ;
		switch ( result ) {
		case ERROR:
			return caughtException( executor.error() ) ;
		case QUIT:
			return produceResult( executor.result() ) ;
		default:
			return result ;
		}
	}
	
}
