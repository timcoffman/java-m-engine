package edu.vanderbilt.clinicalsystems.epic.api;

import edu.vanderbilt.clinicalsystems.m.core.Ref;
import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;

@RoutineUnit(value="EALIBECF1")
public class EpicCommunicationFoundation {
	
	@RoutineTag("zECFGet") public native String getProperty(String propertyName ) ;
	@RoutineTag("zECFGet") public native String getProperty(String propertyName, String parentId ) ;
	@RoutineTag("zECFGet") public native String getProperty(String propertyName, String parentId, Ref<Boolean> more ) ;
	
	@RoutineTag("zECFNumElmts") public native long propertyArrayLength( String propertyName ) ;
	@RoutineTag("zECFNumElmts") public native long propertyArrayLength( String propertyName, Value parentId ) ;
	
	@RoutineTag("zECFGetElmt") public native String getChildOfProperty( String propertyName, String key ) ;
	@RoutineTag("zECFGetElmt") public native String getChildOfProperty( String propertyName, String key, Ref<Boolean> more ) ;
	@RoutineTag("zECFGetElmt") public native String getChildOfProperty( String propertyName, String parentId, String key ) ;
	@RoutineTag("zECFGetElmt") public native String getChildOfProperty( String propertyName, String parentId, String key, Ref<Boolean> more ) ;
	
	@RoutineTag("zECFGetElmt") public native String getChildOfProperty( String propertyName, long key ) ;
	@RoutineTag("zECFGetElmt") public native String getChildOfProperty( String propertyName, long key, Ref<Boolean> more ) ;
	@RoutineTag("zECFGetElmt") public native String getChildOfProperty( String propertyName, String parentId, long key ) ;
	@RoutineTag("zECFGetElmt") public native String getChildOfProperty( String propertyName, String parentId, long key, Ref<Boolean> more ) ;
	
	@RoutineTag("zECFDctNxKey") public native String getItemOfProperty( String propertyName ) ;
	@RoutineTag("zECFDctNxKey") public native String getItemOfProperty( String propertyName, String parent ) ;
	@RoutineTag("zECFDctNxKey") public native String getItemOfProperty( String propertyName, String parent, String afterKey ) ;

	
	@RoutineTag("zECFSet") public native Void setProperty(String propertyName, String value ) ;
	@RoutineTag("zECFSet") public native Void setProperty(String propertyName, String value, String parentId ) ;
	
	public static final String SINGLE_TYPE = "S" ;
	public static final String ARRAY_TYPE = "A" ;
	public static final String DICTIONARY_TYPE = "D" ;
	public static final String LIST_TYPE = "L" ;
	public static final String TABLE_TYPE = "T" ;
	
	@RoutineTag("zECFNew") public native String createProperty(String propertyName, String type ) ;
	@RoutineTag("zECFNew") public native String createProperty(String propertyName, String parentId, String type ) ;
	
	@RoutineTag("zECFNewElmtObj") public native String addItem(String collectionId ) ;
	@RoutineTag("zECFNewElmtObj") public native String addItem(String collectionId, String key ) ;
	
	@RoutineTag("zECFSetElmt") public native Void setItem(String parentId, String value ) ;
	@RoutineTag("zECFSetElmt") public native Void setItem(String parentId, String value, long key ) ;
	@RoutineTag("zECFSetElmt") public native Void setItem(String parentId, String value, long key, Ref<Boolean> more ) ;
	
	
}
