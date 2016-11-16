package edu.vanderbilt.clinicalsystems.epic.api.oo;

import edu.vanderbilt.clinicalsystems.epic.annotation.EpicProvided;
import edu.vanderbilt.clinicalsystems.epic.annotation.EpicRef;
import edu.vanderbilt.clinicalsystems.epic.annotation.EpicRoutineLibrary;
import edu.vanderbilt.clinicalsystems.epic.annotation.EpicTag;
import edu.vanderbilt.clinicalsystems.m.core.Value;

@EpicRoutineLibrary()
public class EpicCommunicationFoundation {
	
	public interface Dictionary {
	}
	
	public interface Element {
		@EpicTag("zECFGet") Value getValue(String propertyName ) ;
		@EpicTag("zECFGet") Value getValue(String propertyName, @EpicRef Value more ) ;
		@EpicTag("zECFGet") Element getElement(String propertyName ) ;
		@EpicTag("zECFGet") Dictionary getDictonary(String propertyName ) ;
	};

	@EpicProvided
	public interface Request extends Element {
		
	}

	@EpicProvided
	public interface Response {
		
	};
	
}
