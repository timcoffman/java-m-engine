package edu.vanderbilt.clinicalsystems.m.core.lib;

import edu.vanderbilt.clinicalsystems.m.core.annotation.Command;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Library;
import edu.vanderbilt.clinicalsystems.m.core.annotation.ReadWriteCode;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;

@Library
public class ReadWrite {
	
	public interface ReadWriteCommand { String command() ; }
	
	@ReadWriteCode public static ReadWriteCommand newline() { return () -> "!" ; } 
	@ReadWriteCode public static ReadWriteCommand align( int column ) { return () -> "?" + column ; } 
	@ReadWriteCode public static ReadWriteCommand clear() { return () -> "#" ; } 
	
	@Command(CommandType.WRITE) public static native void write( Object ... writeCommands ) ;
	@Command(CommandType.READ) public static native void read( Object ... readCommands ) ;
	
	/*
	 * Usage
	 * ReadWrite.write( clear(), align(30), "Hello world!", newline() ) ;
	 */
}
