package edu.vanderbilt.clinicalsystems.epic.annotation;

import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.ARRAY_TYPE;

import java.io.PrintStream;

import edu.vanderbilt.clinicalsystems.epic.api.oo.EpicCommunicationFoundation;
import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.annotation.InjectRoutine;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;
import edu.vanderbilt.clinicalsystems.m.core.lib.ReadWrite;
import edu.vanderbilt.clinicalsystems.m.core.lib.Text;

@RoutineUnit
public class Algorithms {
	
	@RoutineTag
	public static int basicSummationLoop( int start, int increment, int stop) {
		int s = 0 ;
		for ( int x = start ; x <= stop ; x+=increment )
			s += x ;
		return s ;
	}
	
	@RoutineTag
	/**
	 * SET key=$ORDER(x(""))
	 */
	public static String basicSort( String source, String delimiter) {
		int n = Text.occurrencesPlusOne(source,delimiter) ;
		if ( n == 1 )
			return source ;
		Value x = Value.nullValue();
		for ( int i = 1; i <= n ; ++i )
			x.get( Text.piece(source, delimiter, i) ).assign( 1 ) ;
		
		String result = "" ;
		String key = x.nextKey("") ;
		while ( key != "" ) {
			if ( result != "" ) result += delimiter ;
			result += key ;
			key = x.nextKey(key) ;
		}
		return result ;
	}
	
}
