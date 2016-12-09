package edu.vanderbilt.clinicalsystems.m.lang.text;

import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.ANY;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.BOOLEAN;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.DECIMAL;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.INTEGER;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.NUMERIC;
import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.STRING;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
public class JavaExpression<T extends JExpression> {
	
	private final T m_expr ;
	private final Representation m_representation ; 
	
	public static <T extends JExpression> JavaExpression<T> producingAny    ( T expr ) { return new JavaExpression<T>(expr, ANY    ) ;}
	public static <T extends JExpression> JavaExpression<T> producingString ( T expr ) { return new JavaExpression<T>(expr, STRING ) ;}
	public static <T extends JExpression> JavaExpression<T> producingNumeric( T expr ) { return new JavaExpression<T>(expr, NUMERIC) ;}
	public static <T extends JExpression> JavaExpression<T> producingBoolean( T expr ) { return new JavaExpression<T>(expr, BOOLEAN) ;}
	public static <T extends JExpression> JavaExpression<T> producingDecimal( T expr ) { return new JavaExpression<T>(expr, DECIMAL) ;}
	public static <T extends JExpression> JavaExpression<T> producingInteger( T expr ) { return new JavaExpression<T>(expr, INTEGER) ;}
	
	public JavaExpression( T expr ) {
		this( expr, Representation.ANY ) ;
	}
	public JavaExpression( T expr, Representation representation ) {
		m_expr = expr ;
		m_representation = representation ;
	}

	public Representation representation() {
		return m_representation;
	}

	public T expr() {
		return m_expr ;
	}
	
	public Class<?> type( RoutineJavaBuilderEnvironment env ) {
		switch ( m_representation ) {
		case NUMERIC:
		case DECIMAL:
			return java.lang.Double.TYPE ;
		case INTEGER:
			return java.lang.Integer.TYPE ;
		case BOOLEAN:
			return java.lang.Boolean.TYPE ;
		case STRING:
			return java.lang.String.class ;
		case ANY:
		default:
			return env.valueClass() ;
		}
	}
	
	public JavaExpression<?> convert( Representation toRepresentation, RoutineJavaBuilderContext context ) {
		if ( m_representation == toRepresentation )
			return this ;
		
		switch (m_representation) {
		case ANY:
			switch( toRepresentation ) {
			case STRING:
				return JavaInvocation.builder(context)
						.invoke("toString")
						.on( java.lang.Object.class )
						.acceptingNothing()
						.build() ;
			default:
				return this ;
			}
		default:
			return this ;
		}
	}
	
	public static Representation both( Representation a, Representation b ) {
		if ( a.equals(b) )
			return a ;
		
		// they're not exactly equal
		if ( ANY.equals(a) || ANY.equals(b) )
			return ANY ;
		
		// they're both more specific than ANY
		// TODO
		return ANY ;
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
			return ANY;
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
			return ANY;
	}
	
	public static <T extends JExpression> JavaExpression<T> from(T expr, Representation representation) {
		return new JavaExpression<T>(expr, representation) ;
	}
	
	public static JavaExpression<JVar> from(JVar variable, RoutineJavaBuilderContext context) {
		return new JavaExpression<JVar>(variable, determineRepresentation( variable.type(), context ) );

	}
}
