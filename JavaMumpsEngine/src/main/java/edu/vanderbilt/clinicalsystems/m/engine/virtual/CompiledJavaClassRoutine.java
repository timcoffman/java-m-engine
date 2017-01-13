package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.ErrorCode;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.Executor.ExecutionResult;

class CompiledJavaClassRoutine implements CompiledRoutine {
	
	private final Class<?> m_type;
	private final String m_routineName ;
	
	public CompiledJavaClassRoutine(Class<?> type, String routineName) {
		m_type = type ;
		m_routineName = routineName  ;
	}
	
	@Override
	public String name() {
		return m_routineName ;
	}
	
	@Override public String compiledRepresentation() {
		return m_routineName + "<" + m_type.hashCode() + ">";
	}

	@Override
	public CompiledTag compiledTag(String tagName, int parameterCount) {
		for ( Method method : m_type.getDeclaredMethods() ) {
			String checkTagName = method.getName() ; /* perhaps pass the tag name through via the routine meta file */
			if ( checkTagName.equals( tagName ) && method.getParameterCount() == parameterCount) {
				return new CompiledJavaMethodTag(method,tagName);
			}
		}
		for ( Method method : m_type.getDeclaredMethods() ) {
			String checkTagName = method.getName() ; /* perhaps pass the tag name through via the routine meta file */
			if ( checkTagName.equals( tagName ) && method.getParameterCount() > parameterCount) {
				return new CompiledJavaMethodTag(method,tagName);
			}
		}
		for ( Method method : m_type.getDeclaredMethods() ) {
			String checkTagName = method.getName() ; /* perhaps pass the tag name through via the routine meta file */
			if ( checkTagName.equals( tagName ) ) {
				return new CompiledJavaMethodTag(method,tagName);
			}
		}
		return null ;
	}

	private class CompiledJavaMethodTag implements CompiledTag {

		private final Method m_method ;
		private final String m_tagName;

		public CompiledJavaMethodTag(Method method, String tagName) {
			m_method = method ;
			m_tagName = tagName ;
		}
		
		@Override
		public CompiledRoutine compiledRoutine() {
			return CompiledJavaClassRoutine.this ;
		}
		
		@Override
		public String name() {
			return m_tagName ;
		}

		@Override
		public List<String> parameterNames() {
			return Arrays.stream(m_method.getParameters()).map(Parameter::getName).collect(Collectors.toList()) ;
		}

		@Override
		public ExecutionResult execute( ExecutionFrame frame, List<EvaluationResult> arguments ) {
			if ( Modifier.isNative( m_method.getModifiers() ) )
				return frame.caughtError( new EngineException(ErrorCode.JAVA_METHOD_NOT_IMPLEMENTED, "class", m_method.getDeclaringClass().getName(), "method", m_method.getName() ) ) ;
			Object[] javaArguments = new Object[m_method.getParameterCount()];
			Iterator<EvaluationResult> argumentIterator = arguments.iterator() ;
			for ( int i = 0 ; i < javaArguments.length ; ++i ) {
				if ( argumentIterator.hasNext() )
					javaArguments[i] = argumentIterator.next().toObject( m_method.getParameterTypes()[i] ) ;
				else
					javaArguments[i] = null ;
			}
			try {
				Object javaResult = m_method.invoke(null, javaArguments) ;
				if ( Void.TYPE.equals( m_method.getReturnType() ) ) {
					return ExecutionResult.QUIT ;
				} else {
					return frame.producedResult( EvaluationResult.fromJavaObject( javaResult ) ) ;
				}
			} catch (IllegalAccessException | IllegalArgumentException ex) {
				return frame.caughtError( new EngineException(ErrorCode.JAVA_EXCEPTION, new String[0], ex) ) ;
			} catch (InvocationTargetException ex) {
				return frame.caughtError( new EngineException(ErrorCode.JAVA_EXCEPTION, new String[0], ex.getTargetException()) ) ;
			} catch (Throwable ex) {
				return frame.caughtError( new EngineException(ErrorCode.JAVA_EXCEPTION, new String[0], ex) ) ;
			}
		}
		
	}
	
}