package edu.vanderbilt.clinicalsystems.epic.annotation;

import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.annotation.InjectRoutine;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;

@RoutineUnit
public class ChroniclesMethods {

	@InjectRoutine
	private edu.vanderbilt.clinicalsystems.epic.api.Chronicles chronicles ;
	
	@RoutineTag
	public void chroniclesArrayFromIndex( String allergen, Value patients ) {
		String patId = "" ;
		long ctr;
		for ( ctr=1; ; ++ctr ) {
			patId = chronicles.znxIxID( "ZPT", 400, allergen, patId) ;
			if ( patId == "" )
				 break ;
			patients.get(ctr).assign(patId) ;
		}
		patients.get("0").assign(ctr) ;
	}
	
}
