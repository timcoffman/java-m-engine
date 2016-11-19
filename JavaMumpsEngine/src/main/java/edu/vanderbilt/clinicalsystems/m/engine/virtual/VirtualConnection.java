package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import edu.vanderbilt.clinicalsystems.m.engine.Connection;
import edu.vanderbilt.clinicalsystems.m.engine.ConnectionError;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;

public class VirtualConnection implements Connection {

	private final Database m_db ;
	private final BufferedInputOutputDevice m_io ;
	private edu.vanderbilt.clinicalsystems.m.engine.virtual.Connection m_cxn ;
	
	private final LifecycleListener m_lifecycleListener ;
	
	public interface LifecycleListener {
		void created( VirtualConnection cxn ) throws ConnectionError ;
		void willOpen( VirtualConnection cxn ) throws ConnectionError ;
		void didOpen( VirtualConnection cxn ) throws ConnectionError ;
		void willClose( VirtualConnection cxn ) throws ConnectionError ;
		void didClose( VirtualConnection cxn ) throws ConnectionError ;
	}
	
	public VirtualConnection( Database db, LifecycleListener lifecycleListener ) throws ConnectionError {
		m_db = db ;
		m_lifecycleListener = lifecycleListener ;
		m_lifecycleListener.created(this) ;
		m_io = new BufferedInputOutputDevice() ;
	}
	
	private class BufferedInputOutputDevice implements InputOutputDevice {

		private Reader m_reader ;
		private Writer m_writer ;
		
		public void attach( Reader reader ) { m_reader = reader ; }
		public void attach( Writer writer ) { m_writer = writer ; }
		public void attach( Reader reader , Writer writer ) { attach(reader); attach(writer); }
		
		private char[] m_charBuffer = new char[1] ;
		
		@Override
		public String input(Integer characterLimit, Integer timeout) throws IOException {
			if ( null == m_reader )
				throw new IOException("no input channel attached") ;
			StringBuffer buffer = new StringBuffer() ;
			int n = m_reader.read(m_charBuffer,0,1) ;
			while ( n > 0 ) {
				char c = m_charBuffer[0];
				if ( '\n' == c ) break ;
				buffer.append( c ) ;
				if ( null != characterLimit && buffer.length() >= characterLimit) ;
				n = m_reader.read(m_charBuffer,0,1) ;
			}
			return buffer.toString() ;
		}

		private StringBuffer m_outputBuffer = new StringBuffer() ;
		
		@Override
		public void output(String text) throws IOException {
			m_outputBuffer.append(text) ;
		}

		@Override
		public void outputCarriageReturn() throws IOException {
			m_outputBuffer.append("\n") ;
			if ( null == m_writer )
				throw new IOException("no output channel attached") ;
			m_writer.write( m_outputBuffer.toString() ) ;
			m_outputBuffer.setLength(0) ;
		}

		@Override
		public void outputPageFeed() throws IOException {
			/* nothing */
		}

		@Override
		public void outputColumnMove(int column) throws IOException {
			/* nothing */
		}
		
	}
	
	@Override
	public void submit(RoutineElement routineElement, Reader reader, Writer writer) throws ConnectionError {
		if ( routineElement instanceof Command ) {
			open() ;
			m_io.attach(reader,writer);
			m_cxn.execute( (Command)routineElement ) ;
			m_io.attach(null,null);
		}
	}
	
	public void open() throws ConnectionError {
		if ( null == m_cxn ) {
			m_lifecycleListener.willOpen(this) ;
			m_cxn = m_db.openConnection(m_io) ;
			m_lifecycleListener.didOpen(this) ;
		}
	}
	
	@Override
	public void close() throws Exception {
		if ( null != m_cxn ) {
			m_lifecycleListener.willClose(this) ;
			m_cxn.detachInputOutputDevice() ;
			m_lifecycleListener.didClose(this) ;
		}
	}

}
