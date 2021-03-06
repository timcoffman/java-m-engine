package edu.vanderbilt.clinicalsystems.m.core.lib;

import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.ReadWriteCodeType.COLUMN_ALIGN;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.ReadWriteCodeType.NEWLINE;
import static edu.vanderbilt.clinicalsystems.m.core.annotation.support.ReadWriteCodeType.PAGEFEED;

import java.util.List;

import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Command;
import edu.vanderbilt.clinicalsystems.m.core.annotation.ReadWriteCode;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Variable;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinVariable;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;

@RoutineUnit
public class ReadWrite {
	
	public interface ReadWriteCommand { String command() ; }
	
	@ReadWriteCode(NEWLINE) public static ReadWriteCommand newline() { return () -> "!" ; } 
	@ReadWriteCode(COLUMN_ALIGN) public static ReadWriteCommand align( int column ) { return () -> "?" + column ; } 
	@ReadWriteCode(PAGEFEED) public static ReadWriteCommand clear() { return () -> "#" ; } 
	
	/*
	 * Usage
	 * ReadWrite.write( clear(), align(30), "Hello world!", newline() ) ;
	 */
	@Command(CommandType.WRITE) public static native void write( Object ... writeCommands ) ;
	@Command(CommandType.READ) public static native void read( Object ... readCommands ) ;
	
	public interface ChannelParameter { String name() ; String value() ; }
	public interface ChannelDirective { int channel() ; List<ChannelParameter> parameters() ; String namespace() ; }
	
	@Command(CommandType.USE) public static native void use( ChannelDirective ... channelDirectives ) ;
	@Command(CommandType.USE) public static native void use( Value channel ) ;
	@Command(CommandType.USE) public static native void use( String channel ) ;
	@Command(CommandType.USE) public static native void use( int channel ) ;
	@Command(CommandType.USE) public static native void use( double channel ) ;
	
	@Variable(BuiltinVariable.IO) public static native long channel() ;
}
