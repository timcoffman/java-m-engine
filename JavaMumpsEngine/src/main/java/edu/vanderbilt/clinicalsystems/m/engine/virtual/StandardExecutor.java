package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;

public abstract class StandardExecutor implements Executor {

	protected EvaluationResult m_result = null;
	protected EngineException m_exception = null;

	@Override
	public EvaluationResult result() { return m_result ; }

	@Override
	public ExecutionResult producedResult(EvaluationResult result) { m_result = result ; return ExecutionResult.QUIT ; }

	@Override
	public EngineException error() { return m_exception ; }

	@Override
	public ExecutionResult caughtError(EngineException ex) {
		m_exception = ex ;
		return ExecutionResult.ERROR ;
	}

	@Override
	public StandardExecutor clear() {
		m_result = null ;
		m_exception = null ;
		return this ;
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
			return caughtError( executor.error() ) ;
		case QUIT:
			return producedResult( executor.result() ) ;
		default:
			return result ;
		}
	}
	
}
