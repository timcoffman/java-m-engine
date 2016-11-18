package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class Constant extends Expression {
	
	private final String m_value ;
	
	public static final String NULL_VALUE = "" ; 
	public static final String FALSE_VALUE = "0" ; 
	public static final String TRUE_VALUE = "1" ;
	
	public static final Constant NULL = new Constant() ; 
	public static final Constant FALSE = new Constant(FALSE_VALUE) ; 
	public static final Constant TRUE = new Constant(TRUE_VALUE) ; 

	public Constant() {
		this(NULL_VALUE) ;
	}
	
	public Constant( String value ) {
		if ( null == value ) throw new IllegalArgumentException( "Constant value cannot be null; use Constant.from(null)" ) ;
		m_value = value ;
	}
	
	public static Constant from( String value ) {
		if ( null == value || NULL.equals( value ) )
			return NULL ;
		else
			return new Constant( value ) ;
	}
	
	public static Constant from( boolean value ) {
		return from( value ? TRUE_VALUE : FALSE_VALUE ) ;
	}
	
	public static Constant from( int value ) {
		return from( Integer.toString(value) ) ;
	}

	public static Constant from( double value ) {
		return from( Double.toString(value) ) ;
	}
	
	public String value() { return m_value; }

	public boolean toBoolean() { return !FALSE_VALUE.equals(m_value); }
	
	@Override
	public Expression inverted() {
		return Constant.from( !toBoolean() );
	}

	@Override
	protected String unformattedRepresentation() { return "\"" + m_value + "\"" ; } 

	@Override public <R> R visit( Visitor<R> visitor ) {
		return visitor.visitConstant(this) ;
	}
	
	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write( this ) ;
	}

	private static final Pattern NUMBER_FORMAT = Pattern.compile("^[-+]?(?:[1-9][0-9]*|[0])(\\.[0-9]*[1-9])?$") ;

	public boolean representsNull() {
		return m_value.isEmpty() ;
	}
	
	public boolean representsNumber() {
		return NUMBER_FORMAT.matcher(m_value).matches() ;
	}
	
	public boolean representsNumber(long checkValue) {
		Matcher m = NUMBER_FORMAT.matcher(m_value) ;
		if ( !m.matches() )
			return false ; // wrong format
		if ( null != m.group(1) )
			return false ; // not a whole number
		return Long.parseLong( m_value ) == checkValue ;
	}
	
	public boolean representsNumber(double checkValue) {
		Matcher m = NUMBER_FORMAT.matcher(m_value) ;
		if ( !m.matches() )
			return false ; // wrong format
		return Double.parseDouble( m_value ) == checkValue ;
	}
	
	public Constant negated() {
		if ( m_value.startsWith("-") )
			return new Constant( m_value.substring("-".length()) ) ; // without the minus
		else if ( m_value.startsWith("+") )
			return new Constant( "-" + m_value.substring("+".length()) ) ; // without the plus, with a new minus
		else
			return new Constant( "-" + m_value ); // with a new minus
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( null == obj ) {
			return representsNull() ;
		} else if ( obj == this ) {
			return true ;
		} else if ( obj instanceof Constant ) {
			return m_value.equals( ((Constant)obj).value() ) ;
		} else if ( obj instanceof String ) {
			return m_value.equals( obj ) ;
		} else {
			return false ;
		}
	}

}
