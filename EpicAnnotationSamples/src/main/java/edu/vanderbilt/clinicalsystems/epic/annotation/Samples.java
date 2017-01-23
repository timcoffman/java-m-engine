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
public class Samples {
	
	public Samples() {
	}
	
	
	public Samples( Object constructorParameter ) {
	}
	
	public void methodThatsNotATag() {
	}
	
	@RoutineTag
	public void tagWithNoParameters() {
	}

	@RoutineTag
	public void tagWithStringParameter( String x ) {
		ReadWrite.write(x);
	}

	@RoutineTag
	public void tagWithIntegerParameter( int x ) {
		ReadWrite.write(x);
	}

	@RoutineTag
	public void tagWithDoubleParameter( double x ) {
		ReadWrite.write(x);
	}

	@RoutineTag
	public void tagWithValueParameter( Value x ) {
		ReadWrite.write(x);
	}

	@RoutineTag
	public void tagWithOtherParameter( PrintStream x ) {
		ReadWrite.write(x);
	}

	@RoutineTag
	public int basicSummationLoop( int start, int increment, int stop) {
		int s = 0 ;
		for ( int x = start ; x <= stop ; x+=increment )
			s += x ;
		return s ;
	}
	
	@RoutineTag
	/**
	 * SET key=$ORDER(x(""))
	 */
	public String basicSort( String source, String delimiter) {
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


	@RoutineTag
	public String enhancedSort(String source, String delimiter) {
		int n = Text.occurrencesPlusOne(source,delimiter) ;
		if ( n == 1 )
			return source ;
		Value x = Value.nullValue();
		for ( int i = 1; i <= n ; ++i )
			x.get( Text.piece(source, delimiter, i) ).assign( 1 ) ;
		
		String result = "" ;
		for ( String key : x.keys() ) {
			if ( result != "" ) result += delimiter ;
			result += key ;
		}
		return result ;
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
	
	@InjectRoutine
	private edu.vanderbilt.clinicalsystems.epic.api.Chronicles chronicles ;
	
	@InjectRoutine
	private edu.vanderbilt.clinicalsystems.epic.api.EpicCommunicationFoundation ecfContext ;
	
	@RoutineTag("MYSERVICE")
	public void myService() {
		String patID, allergen ;
		Value patients = Value.nullValue() ;
		int ecfLine ;
		//
		// Get Request
		allergen = ecfContext.getProperty("Allergen") ;
		//
		patID = "" ;
		long ctr;
		for ( ctr=1; ; ++ctr ) {
			patID = chronicles.znxIxID( "ZPT", 400, allergen, patID) ;
			if ( patID == "" )
				 break ;
			patients.get(ctr).assign(patID) ;
		}
		patients.get("0").assign(ctr) ;
		
		// Send response
		// ==== Set Array Property Patients ====
		String patientsArrNodeId = ecfContext.createProperty("Patients",ARRAY_TYPE);
	    for ( ecfLine=1 ; ecfLine <= patients.get(0).toLong(); ++ecfLine ) {
	    	ecfContext.setItem(patientsArrNodeId,patients.get(ecfLine).toString(),ecfLine) ;
	    }
	}
	
	@RoutineTag("MYSERVICE2")
	public void myService2() {
		String patID, allergen;
		Value patients = Value.nullValue();
		int ecfLine ;
		//
		// Get Request
		allergen = ecfContext.getProperty("Allergen") ;
		//
		int ctr = 0 ;
		patID = chronicles.znxIxID( "ZPT", 400, allergen, "" ) ;
		while ( patID != "" ) {
			++ctr ;
			patients.get(ctr).assign(patID) ;
			patID = chronicles.znxIxID( "ZPT", 400, allergen, patID) ;
		}
		patients.get(0).assign(ctr) ;
		
		// Send response
		// ==== Set Array Property Patients ====
		String patientsArrNodeId = ecfContext.createProperty("Patients",ARRAY_TYPE);
	    for ( ecfLine=1 ; ecfLine <= patients.get(0).toLong(); ++ecfLine ) {
	    	ecfContext.setItem(patientsArrNodeId,patients.get(ecfLine).toString(),ecfLine) ;
	    }
	}
	
	/*
	UpdateNames n ln,employee,id,name
	  f ln=1:1:$$zECFNumElmts("Employees") d
	  . s employee=$$zECFGetElmt("Employees","",ln)
	  . s id=$$zECFGet("Id",employee)
	  . s name=$$zECFGet("Name",employee)
	  . ; Code to change record names...
	  
	   */
	
	@InjectRoutine public EpicCommunicationFoundation.Request request ;
	@InjectRoutine public EpicCommunicationFoundation.Response response ;
	
	@RoutineTag("UpdateNames")
	public void updateNames() {
		for ( int ln=1 ; ln <= ecfContext.propertyArrayLength("Employees"); ++ln  ) {
			String employeeNodeId = ecfContext.getChildOfProperty( "Employees", ln ) ;
			String id = ecfContext.getProperty( "Id", employeeNodeId ) ;
			String name = ecfContext.getProperty( "Name", employeeNodeId ) ;
		}
	}
	
}
