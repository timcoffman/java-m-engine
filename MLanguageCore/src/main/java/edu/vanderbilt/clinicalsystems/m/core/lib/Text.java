package edu.vanderbilt.clinicalsystems.m.core.lib;

import static edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction.ASCII;
import static edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction.CHAR;
import static edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction.EXTRACT;
import static edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction.LENGTH;
import static edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction.PIECE;

import java.util.function.Consumer;

import edu.vanderbilt.clinicalsystems.m.core.annotation.Function;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Operator;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;
import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;

@RoutineUnit
public class Text {

	@Function(ASCII) public static native int ascii( String characters ) ; 
	@Function(ASCII) public static native int ascii( String characters, int position ) ; 

	@Function(CHAR) public static native String character( int ... ascii ) ;
	
	@Function(EXTRACT) public static native String extract( String source ) ;
	@Function(EXTRACT) public static native String extract( String source, int start ) ;
	@Function(EXTRACT) public static native String extract( String source, int start, int stop ) ;
	
	@Function(value=EXTRACT,assignment=true) public static native Consumer<String> extractAssign( String source ) ;
	@Function(value=EXTRACT,assignment=true) public static native Consumer<String> extractAssign( String source, int start ) ;
	@Function(value=EXTRACT,assignment=true) public static native Consumer<String> extractAssign( String source, int start, int stop ) ;
	
	@Function(EXTRACT) public static native String extractAssign( int ... ascii ) ;
	
	@Function(PIECE) public native static String piece( String searched, String sought ) ;
	@Function(PIECE) public native static String piece( String searched, String sought, int first ) ;
	@Function(PIECE) public native static String piece( String searched, String sought, int first, int last ) ;
	
	@Function(value=PIECE,assignment=true) public native static Consumer<String> pieceAssign( String searched, String sought ) ;
	@Function(value=PIECE,assignment=true) public native static Consumer<String> pieceAssign( String searched, String sought, int first ) ;
	@Function(value=PIECE,assignment=true) public native static Consumer<String> pieceAssign( String searched, String sought, int first, int last ) ;

	@Operator(OperatorType.FOLLOWS) public static native boolean follows( String leftHandSide, String rightHandSide ) ;

	@Function(LENGTH) public native static int length( String searched ) ;
	@Function(LENGTH) public native static int occurrencesPlusOne( String searched, String sought ) ;

}
