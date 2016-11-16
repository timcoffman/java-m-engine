package edu.vanderbilt.clinicalsystems.epic.annotation;

import static edu.vanderbilt.clinicalsystems.epic.api.Chronicles.znxIxID;
import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.ARRAY_TYPE;
import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.zECFGet;
import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.zECFGetElmt;
import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.zECFNew;
import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.zECFNumElmts;
import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.zECFSetElmt;
import edu.vanderbilt.clinicalsystems.epic.annotation.EpicTag;
import edu.vanderbilt.clinicalsystems.epic.api.oo.EpicCommunicationFoundation;
import edu.vanderbilt.clinicalsystems.m.core.Value;
/**
 *  sample service
 */
@EpicRoutine
public class Services {
	
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
	
	@EpicTag
	public void MyService() {
		Value allergen, patients = Value.nullValue(), patientsArrObj = Value.nullValue(), patID = Value.nullValue() ;
		int ecfLine ;
		//
		// Get Request
		allergen = zECFGet( "Allergen", Value.nullValue() ) ;
		//
		int ctr;
		for ( ctr=1; ; ++ctr ) {
			patID = znxIxID( "ZPT", 400, allergen, patID) ;
			if ( patID == Value.nullValue() )
				 break ;
			patients.put(patID, ctr) ;
		}
		patients.put(ctr, 0) ;
		
		// Send response
		// ==== Set Array Property Patients ====
	    patientsArrObj = zECFNew("Patients",Value.nullValue(),ARRAY_TYPE);
	    for ( ecfLine=1 ; ecfLine <= patients.get(0).toInt(); ++ecfLine ) {
	    	zECFSetElmt(patientsArrObj,patients.get(ecfLine)) ;
	    }
	}
	
//	@EpicTag
//	public void MyService3() {
//		Value allergen, patients = Value.nullValue(), patientsArrObj = Value.nullValue(), patID = Value.nullValue() ;
//		int ecfLine ;
//		//
//		// Get Request
//		allergen = zECFGet("Allergen") ;
//		//
//		ValueArray patientLines = new ValueArray(patients) ;
//		patID = znxIxID( "ZPT", 400, allergen, Value.nullValue() ) ;
//		while ( patID != Value.nullValue() ) {
//			patientLines.addLine( patID );
//			patID = znxIxID( "ZPT", 400, allergen, Value.nullValue() ) ;
//		}
//		
//		// Send response
//		// ==== Set Array Property Patients ====
//		patientsArrObj = zECFNew("Patients",Value.nullValue(),ARRAY_TYPE);
//		for ( ecfLine=patientLines.firstLine() ; ecfLine <= patientLines.lastLine(); ++ecfLine ) {
//			zECFSetElmt(patientsArrObj,patientLines.getLine(ecfLine),ecfLine) ;
//		}
//	}
	
	/*
	UpdateNames n ln,employee,id,name
	  f ln=1:1:$$zECFNumElmts("Employees") d
	  . s employee=$$zECFGetElmt("Employees","",ln)
	  . s id=$$zECFGet("Id",employee)
	  . s name=$$zECFGet("Name",employee)
	  . ; Code to change record names...
	  
	   */
	
	@EpicInject public EpicCommunicationFoundation.Request request ;
	@EpicInject public EpicCommunicationFoundation.Response response ;
	
	@EpicTag("UpdateNames")
	public void updatesNames() {
		for ( int ln=1 ; ln <= zECFNumElmts("Employees",null); ++ln  ) {
			Value employee = zECFGetElmt( "Employees", null, ln ) ;
			Value id = zECFGet( "Id", employee  ) ;
			Value name = zECFGet( "Name", employee ) ;
		}
	}

}
