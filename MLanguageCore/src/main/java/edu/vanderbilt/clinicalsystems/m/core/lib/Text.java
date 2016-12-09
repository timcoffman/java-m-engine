package edu.vanderbilt.clinicalsystems.m.core.lib;

import static edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction.ASCII;
import static edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction.CHAR;
import static edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction.EXTRACT;
import static edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction.LENGTH;
import static edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction.PIECE;
import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Function;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Library;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Operator;
import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;

@Library
public class Text {

	@Function(ASCII) public static native int ascii( String characters ) ; 
	@Function(ASCII) public static native int ascii( String characters, int position ) ; 

	@Function(CHAR) public static native String character( int ... ascii ) ;
	
	@Function(EXTRACT) public static native String extract( String source ) ;
	@Function(EXTRACT) public static native String extract( String source, int start ) ;
	@Function(EXTRACT) public static native String extract( String source, int start, int stop ) ;
	
	@Function(value=EXTRACT,assignment=true) public static native void extractAssign( String source, String replacement ) ;
	@Function(value=EXTRACT,assignment=true) public static native void extractAssign( String source, int start, String replacement ) ;
	@Function(value=EXTRACT,assignment=true) public static native void extractAssign( String source, int start, int stop, String replacement ) ;
	
	@Function(EXTRACT) public static native String extractAssign( int ... ascii ) ;

	@Function(PIECE) public native static String piece( String searched, String sought ) ;
	@Function(PIECE) public native static String piece( String searched, String sought, int first ) ;
	@Function(PIECE) public native static String piece( String searched, String sought, int first, int last ) ;
	
	@Function(value=PIECE,assignment=true) public native static void pieceAssign( String searched, String sought, String replacement ) ;
	@Function(value=PIECE,assignment=true) public native static void pieceAssign( String searched, String sought, int first, String replacement ) ;
	@Function(value=PIECE,assignment=true) public native static void pieceAssign( String searched, String sought, int first, int last, String replacement ) ;

	@Operator(OperatorType.FOLLOWS) public static native boolean follows( String leftHandSide, String rightHandSide ) ;

	@Function(LENGTH) public native static int length( Value value ) ;
	@Function(LENGTH) public native static int occurrences( Value searched, Value sought ) ;

}