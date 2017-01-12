package edu.vanderbilt.clinicalsystems.epic.api.oo;

import edu.vanderbilt.clinicalsystems.epic.annotation.EpicProvided;
import edu.vanderbilt.clinicalsystems.epic.annotation.EpicRef;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag;
import edu.vanderbilt.clinicalsystems.m.core.Value;

@RoutineUnit()
public class EpicCommunicationFoundation {
	
	public interface Dictionary {
	}
	
	public interface Element {
		@RoutineTag("zECFGet") Value getValue(String propertyName ) ;
		@RoutineTag("zECFGet") Value getValue(String propertyName, @EpicRef Value more ) ;
		@RoutineTag("zECFGet") Element getElement(String propertyName ) ;
		@RoutineTag("zECFGet") Dictionary getDictonary(String propertyName ) ;
	};

	@EpicProvided
	public interface Request extends Element {
		
	}

	@EpicProvided
	public interface Response {
		
	};
	
}
