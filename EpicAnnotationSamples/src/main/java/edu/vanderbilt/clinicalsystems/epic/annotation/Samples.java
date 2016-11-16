package edu.vanderbilt.clinicalsystems.epic.annotation;

import static edu.vanderbilt.clinicalsystems.epic.api.Chronicles.znxIxID;
import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.ARRAY_TYPE;
import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.zECFGet;
import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.zECFGetElmt;
import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.zECFNew;
import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.zECFNumElmts;
import static edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation.zECFSetElmt;
import edu.vanderbilt.clinicalsystems.epic.api.oo.EpicCommunicationFoundation;
import edu.vanderbilt.clinicalsystems.m.core.Value;

@EpicRoutine
public class Samples {
	
	public Samples() {
	}
	
	
	public Samples( Object constructorParameter ) {
	}
	
	
	public void methodThatsNotATag() {
	}
	
	@EpicTag
	public void tagWithNoParameters() {
	}

	@EpicTag
	public void tagWithStringParameter( String x ) {
	}

	@EpicTag
	public void tagWithIntegerParameter( int x ) {
	}

	@EpicTag
	public void tagWithDoubleParameter( double x ) {
	}

	@EpicTag
	public void tagWithValueParameter( Value x ) {
	}

	@EpicTag
	public void tagWithOtherParameter( System x ) {
	}

	@EpicTag
	public void unaryOperators() {
		int prefixPlus = 0 ;
		int prefixMinus = 0 ;
		int postfixIncrement = 0 ;
		int prefixIncrement = 0 ;
		int postfixDecrement = 0 ;
		int prefixDecrement = 0 ;
		int x;
		x = +prefixPlus ;
		x = -prefixMinus ;
		x = postfixIncrement++ ;
		x = ++prefixIncrement ;
		x = postfixDecrement-- ;
		x = --prefixDecrement ;
	}

	@EpicTag
	public void loops() {
		int k ;
		for ( int x = 1 ; ; x+=7 )
			break ;
		for ( int x = 1 ; x <= 10 ; x+=2 )
			k = 0 ;
		for ( int x = 0 ; x < 10 ; ++x )
			k = 0 ;
		for ( int x = 10 ; x >= 1 ; --x )
			k = 0 ;
		
		int r = 0 ;
		while ( r < 10 )
			++r ;

		int q = 0 ;
		do {
			q = 0 ;
		} while ( ++q < 10 );
	}

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
		allergen = zECFGet("Allergen") ;
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
	    	zECFSetElmt(patientsArrObj,patients.get(ecfLine),ecfLine) ;
	    }
	}
	
	@EpicTag
	public void MyService2() {
		Value allergen, patients = Value.nullValue(), patientsArrObj = Value.nullValue(), patID = Value.nullValue() ;
		int ecfLine ;
		//
		// Get Request
		allergen = zECFGet("Allergen") ;
		//
		int ctr = 0 ;
		patID = znxIxID( "ZPT", 400, allergen, Value.nullValue() ) ;
		while ( patID != Value.nullValue() ) {
			++ctr ;
			patients.put(patID, ctr) ;
			patID = znxIxID( "ZPT", 400, allergen, patID) ;
		}
		patients.put(ctr, 0) ;
		
		// Send response
		// ==== Set Array Property Patients ====
	    patientsArrObj = zECFNew("Patients",Value.nullValue(),ARRAY_TYPE);
	    for ( ecfLine=1 ; ecfLine <= patients.get(0).toInt(); ++ecfLine ) {
	    	zECFSetElmt(patientsArrObj,patients.get(ecfLine),ecfLine) ;
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
	
	/**
	 * helper4 function, expecting "N z I a>7 S z=">7"\nE  S z="<=7""
	 */
	@EpicTag
	public String helper4( int a ) {
		String z ;
		if ( a>7 ) z = ">7" ;
		else z = "<=7" ;
		return z ;
	}

	/**
	 * main entry point, expecting "Q"
	 */
	@EpicTag
	public void main( String x, Integer y ) {
		/* nothing */
	}

	/**
	 * helper function, expecting "Q s_s"
	 */
	@EpicTag
	public String helper( String s ) {
		return s + s ;
	}
	
	/**
	 * helper2 function, expecting "Q b*c+a"
	 */
	@EpicTag
	public double helper2( double a, double b, double c ) {
		return a + b * c ;
	}
	
	/**
	 * helper3 function, expecting "N z S z=$$helper2(1.0,2.0,3.0) Q b*z+a"
	 */
	@EpicTag
	public Float helper3( Float a, Float b, Float c ) {
		float z = (float)helper2(1.0,2.0,3.0), k = 99 ; 
		long t = System.currentTimeMillis() ;
		return a + b * z ;
	}

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
