package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.util.Objects;

import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;

public abstract class EvaluationResult {
	
	public abstract Constant toConstant() ;
	public abstract Object toObject(Class<?> ofType) ;
	
	public static EvaluationResult fromConstant( Constant constant ) {
		return new ConstantEvaluationResult(constant) ;
	}
	
	public static EvaluationResult fromJavaObject( Object obj ) {
		return new JavaEvaluationResult(obj) ;
	}
	
	private static class ConstantEvaluationResult extends EvaluationResult {
		private final Constant m_constant;
		public ConstantEvaluationResult(Constant constant) { Objects.requireNonNull(constant) ; m_constant = constant; }
		@Override public String toString() { return m_constant.toString() ; } 
		@Override public Constant toConstant() {
			return m_constant ;
		}
		@Override public Object toObject(Class<?> ofType) {
			if ( String.class.isAssignableFrom(ofType) )
				return m_constant.toString() ;
			else if ( Boolean.TYPE.isAssignableFrom(ofType) || Boolean.class.isAssignableFrom(ofType) )
				return m_constant.toBoolean() ;
			else if ( Long.TYPE.isAssignableFrom(ofType) || Long.class.isAssignableFrom(ofType) )
				return m_constant.toLong() ;
			else if ( Double.TYPE.isAssignableFrom(ofType) || Double.class.isAssignableFrom(ofType) )
				return m_constant.toDouble() ;
			else if ( Object.class.isAssignableFrom(ofType) )
				return m_constant.toString() ;
			else
				throw new UnsupportedOperationException( m_constant + " cannot be converted to a " + ofType) ;
		}
	}
	
	private static class JavaEvaluationResult extends EvaluationResult {
		private Object m_object;
		public JavaEvaluationResult(Object obj) { Objects.requireNonNull(obj) ; m_object = obj; }
		@Override public String toString() { return m_object.toString() ; } 
		@Override public Object toObject(Class<?> ofType) {
			return ofType.cast(m_object) ;
		}
		@Override public Constant toConstant() {
			if ( m_object instanceof String )
				return Constant.from( (String)m_object ) ;
			else if ( m_object instanceof Boolean )
				return Constant.from( (Boolean)m_object ) ;
			else if ( m_object instanceof Long)
				return Constant.from( (Long)m_object ) ;
			else if ( m_object instanceof Double )
				return Constant.from( (Double)m_object ) ;
			else
				throw new UnsupportedOperationException( m_object + " cannot be converted to a Constant" ) ;
		}
	}
	
}
