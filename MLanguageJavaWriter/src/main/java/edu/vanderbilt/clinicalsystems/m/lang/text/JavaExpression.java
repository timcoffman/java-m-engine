package edu.vanderbilt.clinicalsystems.m.lang.text;

import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.BOOLEAN;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.DECIMAL;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.INTEGER;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.NATIVE;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.NUMERIC;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.STRING;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
public class JavaExpression<T extends JExpression> {
	
	private final T m_expr ;
	private final Supplier<Optional<Representation>> m_representation ; 
	
	public static <T extends JExpression> JavaExpression<T> producingAny    ( T expr ) { return new JavaExpression<T>(expr, NATIVE.supplier() ) ;}
	public static <T extends JExpression> JavaExpression<T> producingString ( T expr ) { return new JavaExpression<T>(expr, STRING.supplier() ) ;}
	public static <T extends JExpression> JavaExpression<T> producingNumeric( T expr ) { return new JavaExpression<T>(expr, NUMERIC.supplier()) ;}
	public static <T extends JExpression> JavaExpression<T> producingBoolean( T expr ) { return new JavaExpression<T>(expr, BOOLEAN.supplier()) ;}
	public static <T extends JExpression> JavaExpression<T> producingDecimal( T expr ) { return new JavaExpression<T>(expr, DECIMAL.supplier()) ;}
	public static <T extends JExpression> JavaExpression<T> producingInteger( T expr ) { return new JavaExpression<T>(expr, INTEGER.supplier()) ;}
	
	public JavaExpression( T expr ) {
		this( expr, NATIVE.supplier() ) ;
	}
	public JavaExpression( T expr, Supplier<Optional<Representation>> representation ) {
		m_expr = expr ;
		m_representation = representation ;
	}

	public Supplier<Optional<Representation>> representation() {
		return m_representation;
	}

	public T expr() {
		return m_expr ;
	}
	
	public Class<?> type( RoutineJavaBuilderEnvironment env ) {
		return env.typeFor( m_representation.get().orElse(NATIVE) ) ;
	}
	
	public JavaExpression<?> convert( Representation toRepresentation, RoutineJavaBuilderContext context ) {
		Representation representation = m_representation.get().orElse(NATIVE);
		if ( representation == toRepresentation )
			return this ;
		
		switch (representation) {
		case NATIVE:
			switch( toRepresentation ) {
			case STRING:
				return JavaInvocation.builder(context)
						.on( this )
						.invoke("toString")
						.acceptingNothing()
						.build() ;
			case INTEGER:
				return JavaInvocation.builder(context)
						.on( this )
						.invoke("toInt")
						.acceptingNothing()
						.build() ;
			case NUMERIC:
			case DECIMAL:
				return JavaInvocation.builder(context)
						.on( this )
						.invoke("toDouble")
						.acceptingNothing()
						.build() ;
			case BOOLEAN:
				return JavaInvocation.builder(context)
						.on( this )
						.invoke("toBoolean")
						.acceptingNothing()
						.build() ;
			default:
				return this ;
			}
		case NUMERIC:
		case DECIMAL:
			switch ( toRepresentation ) {
			case STRING:
				return JavaInvocation.builder(context)
					.on( String.class )
					.invoke( "valueOf" )
					.accepting( Double.TYPE )
					.supplying( (r)->this )
					.build() ;
			case NUMERIC:
			case DECIMAL:
			case INTEGER:
			default:
				return this;
			}
		default:
			return this ;
		}
	}
	
	public static List<Representation> determineRepresentations( Class<?> ... types ) {
		return determineRepresentations( Arrays.asList(types) );
	}
	
	public static List<Representation> determineRepresentations( List<Class<?>> types ) {
		return types.stream().map( (a)->determineRepresentation(a) ).collect( Collectors.toList() ) ;
	}
	
	public static Representation determineRepresentation( Class<?> type ) {
		if ( type.equals( java.lang.String.class ) || type.equals( java.lang.Character.TYPE ) )
			return STRING;
		else if ( type.equals( java.lang.Boolean.TYPE ) )
			return BOOLEAN; 
		else if ( type.equals( java.lang.Integer.TYPE ) || type.equals( java.lang.Long.TYPE ) || type.equals( java.lang.Byte.TYPE ) || type.equals( java.lang.Short.TYPE ) )
			return INTEGER;
		else if ( type.equals( java.lang.Float.TYPE ) || type.equals( java.lang.Double.TYPE ) )
			return DECIMAL;
		else
			return NATIVE;
	}
	
	public static Representation determineRepresentation( JType type, RoutineJavaBuilderContext context ) {
		if ( type.equals( context.codeModel().ref(java.lang.String.class) ) || type.equals( context.codeModel().CHAR ) )
			return STRING;
		else if ( type.equals( context.codeModel().BOOLEAN ) )
			return BOOLEAN; 
		else if ( type.equals( context.codeModel().INT ) || type.equals( context.codeModel().LONG ) || type.equals( context.codeModel().BYTE ) || type.equals( context.codeModel().SHORT ) )
			return INTEGER;
		else if ( type.equals( context.codeModel().FLOAT ) || type.equals( context.codeModel().DOUBLE ) )
			return DECIMAL;
		else
			return NATIVE;
	}
	
	public static <T extends JExpression> JavaExpression<T> from(T expr, Representation representation) {
		return from( expr, representation.supplier() ) ;
	}
	
	public static <T extends JExpression> JavaExpression<T> from(T expr, Supplier<Optional<Representation>> representation) {
		return new JavaExpression<T>(expr, representation) ;
	}
	
	public static JavaExpression<JVar> from(JVar variable, RoutineJavaBuilderContext context) {
		return new JavaExpression<JVar>(variable, determineRepresentation( variable.type(), context ).supplier() );

	}
}
