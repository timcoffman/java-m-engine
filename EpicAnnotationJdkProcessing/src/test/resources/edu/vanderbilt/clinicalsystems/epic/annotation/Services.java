package edu.vanderbilt.clinicalsystems.epic.annotation;

//import static edu.vanderbilt.clinicalsystems.epic.api.Chronicles.znxIxID;
import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.ARRAY_TYPE;
import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.annotation.InjectRoutine;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;
/**
 *  sample service
 */
@RoutineUnit
public class Services {

	@InjectRoutine
	private edu.vanderbilt.clinicalsystems.epic.api.Chronicles chronicles ;
	
	@InjectRoutine
	private edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation ecfContext ;

	/*
	MyService n allergen,patients,patientsArrObj,ecfLine,ctr,patID
	    ;
	    ; Get request
	    s allergen=$$zECFGet("Allergen","")
	    ;
	    f ctr=1:1 s patID=$$znxIxID("ZPT",400,allergen,patID) q:patID="" d
	    . s patients(ctr)=patID
	    s patients(0)=ctr
	    ;
	    ; Send response
	    ; ==== Set Array Property Patients ====
	    s patientsArrObj=$$zECFNew("Patients","","A")
	    f ecfLine=1:1:patients(0) d
	    . s %=$$zECFSetElmt(patientsArrObj,patients(ecfLine))
	    q
     */
	
	@RoutineTag
	public void MyService() {
		Value patients = Value.nullValue() ;
		String patId = "";
		//
		// Get Request
		String allergen = ecfContext.getProperty( "Allergen", "" ) ;
		//
		int ctr;
		for ( ctr=1; ; ++ctr ) {
			patId = chronicles.znxIxID( "ZPT", 400, allergen, patId) ;
			if ( patId == "" )
				 break ;
			patients.get(ctr).assign(patId) ;
		}
		patients.get(0).assign(ctr) ;
		
		// Send response
		// ==== Set Array Property Patients ====
	    String patientsNodeId = ecfContext.createProperty("Patients","",ARRAY_TYPE);
	    for ( long ecfLine=1 ; ecfLine <= patients.get(0).toLong(); ++ecfLine ) {
	    	ecfContext.setItem(patientsNodeId,patients.get(ecfLine).toString()) ;
	    }
	}

}
