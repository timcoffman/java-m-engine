package edu.vanderbilt.clinicalsystems.epic.api;

import edu.vanderbilt.clinicalsystems.epic.annotation.EpicRef;
import edu.vanderbilt.clinicalsystems.epic.annotation.EpicRoutineLibrary;
import edu.vanderbilt.clinicalsystems.epic.annotation.EpicTag;
import edu.vanderbilt.clinicalsystems.m.core.Value;

@EpicRoutineLibrary(value="EALIB",implicit=true)
public class EpicCommunicationFoundation {
	
	@EpicTag public static Value zECFGet(String propertyName ) { return zECFGet(propertyName,Value.nullValue()); }
	@EpicTag public static native Value zECFGet(String propertyName, Value parentId ) ;
	@EpicTag public static native Value zECFGet(String propertyName, Value parentId, @EpicRef Value more ) ;
	
	@EpicTag public static native int zECFNumElmts( String propertyName ) ;
	@EpicTag public static native int zECFNumElmts( String propertyName, Value parentId ) ;
	
	@EpicTag public static native Value zECFGetElmt( String propertyName, Value parentId, Value key ) ;
	@EpicTag public static native Value zECFGetElmt( String propertyName, Value parentId, Value key, @EpicRef Value more ) ;
	
	@EpicTag public static native Value zECFGetElmt( String propertyName, Value parentId, int key ) ;
	@EpicTag public static native Value zECFGetElmt( String propertyName, Value parentId, int key, @EpicRef Value more ) ;
	
	@EpicTag public static native String zECFDctNxKey( String propertyName ) ;
	@EpicTag public static native String zECFDctNxKey( String propertyName, Value parent ) ;
	@EpicTag public static native String zECFDctNxKey( String propertyName, Value parent, String afterKey ) ;

	
	@EpicTag public static native Value zECFSet(String propertyName, Value value ) ;
	@EpicTag public static native Value zECFSet(String propertyName, Value value, Value parentId ) ;
	
	public static final String SINGLE_TYPE = "S" ;
	public static final String ARRAY_TYPE = "A" ;
	public static final String DICTIONARY_TYPE = "D" ;
	public static final String LIST_TYPE = "L" ;
	public static final String TABLE_TYPE = "T" ;
	
	@EpicTag public static native Value zECFNew(String propertyName, Value parentId, String type ) ;
	
	@EpicTag public static native Value zECFNewElmtObj(Value collectionId ) ;
	@EpicTag public static native Value zECFNewElmtObj(Value collectionId, Value key ) ;
	
	@EpicTag public static native Value zECFSetElmt(Value parentId, Value value ) ;
	@EpicTag public static native Value zECFSetElmt(Value parentId, Value value, int key ) ;
	@EpicTag public static native Value zECFSetElmt(Value parentId, Value value, int key, Value more ) ;
	
	
}
