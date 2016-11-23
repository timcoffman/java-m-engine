package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class Constant extends Expression {
	
	private final String m_value ;
		
	public static final Constant NULL = new Constant() ; 
	public static final Constant FALSE = new Constant(ConstantSupport.FALSE_VALUE) ; 
	public static final Constant TRUE = new Constant(ConstantSupport.TRUE_VALUE) ; 

	public Constant() {
		this(ConstantSupport.NULL_VALUE) ;
	}
	
	public Constant( String value ) {
		if ( null == value )
			throw new IllegalArgumentException( "Constant value cannot be null; use Constant.from(null)" ) ;
		m_value = value ;
	}
	
	public static Constant from( String value ) {
		if ( null == value || NULL.equals( value ) )
			return NULL ;
		else
			return new Constant( value ) ;
	}
	
	public static Constant from( boolean value ) {
		return from( value ? ConstantSupport.TRUE_VALUE : ConstantSupport.FALSE_VALUE ) ;
	}
	
	public static Constant from( int value ) {
		return from( Integer.toString(value) ) ;
	}

	public static Constant from( double value ) {
		return from( Double.toString(value).replaceAll("\\.0$", "") ) ; /* remove trailing zeros */
	}
	
	public String value() { return m_value; }

	public boolean toBoolean() { return ConstantSupport.toBoolean(m_value); }
	
	public long toLong() throws NumberFormatException { return ConstantSupport.toLong(m_value); }
	
	public double toDouble() throws NumberFormatException { return ConstantSupport.toDouble(m_value); }
	
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

	public boolean representsNull() {
		return ConstantSupport.representsNull(m_value) ;
	}
	
	public boolean representsNumber() {
		return ConstantSupport.representsNumber(m_value) ;
	}
	
	public boolean representsNumber(long checkValue) {
		return ConstantSupport.representsNumber(m_value,checkValue) ;
	}
	
	public boolean representsNumber(double checkValue) {
		return ConstantSupport.representsNumber(m_value,checkValue) ;
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
