package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.io.Reader;
import java.io.Writer;

import edu.vanderbilt.clinicalsystems.m.engine.Connection;
import edu.vanderbilt.clinicalsystems.m.engine.ConnectionError;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;

public class VirtualConnection implements Connection {

	private final LifecycleListener m_lifecycleListener ;
	
	public interface LifecycleListener {
		void created( VirtualConnection cxn ) throws ConnectionError ;
		void willOpen( VirtualConnection cxn ) throws ConnectionError ;
		void didOpen( VirtualConnection cxn ) throws ConnectionError ;
		void willClose( VirtualConnection cxn ) throws ConnectionError ;
		void didClose( VirtualConnection cxn ) throws ConnectionError ;
	}
	
	public VirtualConnection( LifecycleListener lifecycleListener ) throws ConnectionError {
		m_lifecycleListener = lifecycleListener ; m_lifecycleListener.created(this) ;
	}
	
	@Override
	public void submit(RoutineElement routineElement, Reader reader, Writer writer) throws ConnectionError {
		
	}
	
	public void open() throws ConnectionError {
		m_lifecycleListener.willOpen(this) ;
		m_lifecycleListener.didOpen(this) ;
	}
	
	@Override
	public void close() throws Exception {
		m_lifecycleListener.willClose(this) ;
		m_lifecycleListener.didClose(this) ;
	}

}
