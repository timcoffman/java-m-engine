package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstantSupport {

	public static final String NULL_VALUE = "" ; 
	public static final String FALSE_VALUE = "0" ; 
	public static final String TRUE_VALUE = "1" ;

	public static boolean toBoolean(String value) { return !FALSE_VALUE.equals(value); }
	
	public static long toLong(String value) throws NumberFormatException { return Long.parseLong(value); }
	
	public static double toDouble(String value) throws NumberFormatException { return Double.parseDouble(value); }

	public static boolean representsNull(String value) {
		return null == value || value.isEmpty() ;
	}
	
	private static final Pattern NUMBER_FORMAT = Pattern.compile("^[-+]?(?:[1-9][0-9]*|[0])(\\.[0-9]*[1-9])?$") ;
	
	public static boolean representsNumber(String value) {
		return NUMBER_FORMAT.matcher(value).matches() ;
	}
	
	public static boolean representsNumber(String value, long checkValue) {
		Matcher m = NUMBER_FORMAT.matcher(value) ;
		if ( !m.matches() )
			return false ; // wrong format
		if ( null != m.group(1) )
			return false ; // not a whole number
		return toLong(value) == checkValue ;
	}
	
	public static boolean representsNumber(String value, double checkValue) {
		Matcher m = NUMBER_FORMAT.matcher(value) ;
		if ( !m.matches() )
			return false ; // wrong format
		return toDouble(value) == checkValue ;
	}
	

}
