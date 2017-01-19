package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.util.Objects;
import java.util.Optional;

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
			return constantToObject( m_constant, ofType ) ;
		}
		@Override public boolean equals(Object obj) {
			if ( null == obj ) return false ;
			if ( this == obj ) return true ;
			if ( !(obj instanceof EvaluationResult) ) return false ;
			EvaluationResult evalResult = (EvaluationResult)obj ;
			return m_constant.equals( evalResult.toConstant() ) ;
		}
	}

	private static Object constantToObject( Constant constant, Class<?> ofType ) {
		if ( String.class.isAssignableFrom(ofType) )
			return constant.value() ;
		else if ( Boolean.TYPE.isAssignableFrom(ofType) || Boolean.class.isAssignableFrom(ofType) )
			return constant.toBoolean() ;
		else if ( Long.TYPE.isAssignableFrom(ofType) || Long.class.isAssignableFrom(ofType) )
			return constant.toLong() ;
		else if ( Double.TYPE.isAssignableFrom(ofType) || Double.class.isAssignableFrom(ofType) )
			return constant.toDouble() ;
		else if ( Object.class.isAssignableFrom(ofType) )
			return constant.toString() ;
		else
			throw new UnsupportedOperationException( constant + " cannot be converted to a " + ofType) ;
	}
	
	private static class JavaEvaluationResult extends EvaluationResult {
		private Optional<Object> m_object;
		public JavaEvaluationResult(Object obj) { m_object = Optional.ofNullable(obj); }
		@Override public String toString() { return m_object.map(Object::toString).orElseThrow( ()->new NullPointerException() ) ; } 
		@Override public Object toObject(Class<?> ofType) {
			return m_object.map( ofType::cast ).get() ;
		}
		@Override public Constant toConstant() {
			return m_object.map( EvaluationResult::objectToConstant ).orElse( Constant.NULL ) ;
		}
		
		@Override public boolean equals(Object obj) {
			if ( null == obj ) return false ;
			if ( this == obj ) return true ;
			if ( !(obj instanceof EvaluationResult) ) return false ;
			EvaluationResult evalResult = (EvaluationResult)obj ;
			return m_object
				.map( (o)->o.equals( evalResult.toObject( o.getClass() ) ) )
				.orElse( null == evalResult.toObject(Object.class) )
				;
		}
	}
	
	private static Constant objectToConstant(Object obj) {
		if ( obj instanceof String )
			return Constant.from( (String)obj ) ;
		else if ( obj instanceof Boolean )
			return Constant.from( (Boolean)obj ) ;
		else if ( obj instanceof Long)
			return Constant.from( (Long)obj ) ;
		else if ( obj instanceof Double )
			return Constant.from( (Double)obj ) ;
		else
			throw new UnsupportedOperationException( obj + " cannot be converted to a Constant" ) ;
	}
	
}
