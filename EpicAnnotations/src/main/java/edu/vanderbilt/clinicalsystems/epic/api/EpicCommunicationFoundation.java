package edu.vanderbilt.clinicalsystems.epic.api;

import edu.vanderbilt.clinicalsystems.epic.annotation.EpicRef;
import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag;

@RoutineUnit(value="EALIB")
public class EpicCommunicationFoundation {
	
	@RoutineTag public static Value zECFGet(String propertyName ) { return zECFGet(propertyName,Value.nullValue()); }
	@RoutineTag public static native Value zECFGet(String propertyName, Value parentId ) ;
	@RoutineTag public static native Value zECFGet(String propertyName, Value parentId, @EpicRef Value more ) ;
	
	@RoutineTag public static native int zECFNumElmts( String propertyName ) ;
	@RoutineTag public static native int zECFNumElmts( String propertyName, Value parentId ) ;
	
	@RoutineTag public static native Value zECFGetElmt( String propertyName, Value parentId, Value key ) ;
	@RoutineTag public static native Value zECFGetElmt( String propertyName, Value parentId, Value key, @EpicRef Value more ) ;
	
	@RoutineTag public static native Value zECFGetElmt( String propertyName, Value parentId, int key ) ;
	@RoutineTag public static native Value zECFGetElmt( String propertyName, Value parentId, int key, @EpicRef Value more ) ;
	
	@RoutineTag public static native String zECFDctNxKey( String propertyName ) ;
	@RoutineTag public static native String zECFDctNxKey( String propertyName, Value parent ) ;
	@RoutineTag public static native String zECFDctNxKey( String propertyName, Value parent, String afterKey ) ;

	
	@RoutineTag public static native Value zECFSet(String propertyName, Value value ) ;
	@RoutineTag public static native Value zECFSet(String propertyName, Value value, Value parentId ) ;
	
	public static final String SINGLE_TYPE = "S" ;
	public static final String ARRAY_TYPE = "A" ;
	public static final String DICTIONARY_TYPE = "D" ;
	public static final String LIST_TYPE = "L" ;
	public static final String TABLE_TYPE = "T" ;
	
	@RoutineTag public static native Value zECFNew(String propertyName, Value parentId, String type ) ;
	
	@RoutineTag public static native Value zECFNewElmtObj(Value collectionId ) ;
	@RoutineTag public static native Value zECFNewElmtObj(Value collectionId, Value key ) ;
	
	@RoutineTag public static native Value zECFSetElmt(Value parentId, Value value ) ;
	@RoutineTag public static native Value zECFSetElmt(Value parentId, Value value, int key ) ;
	@RoutineTag public static native Value zECFSetElmt(Value parentId, Value value, int key, Value more ) ;
	
	
}
