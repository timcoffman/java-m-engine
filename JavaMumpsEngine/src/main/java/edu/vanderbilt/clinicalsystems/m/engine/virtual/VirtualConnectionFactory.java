package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.vanderbilt.clinicalsystems.m.engine.ConnectionError;
import edu.vanderbilt.clinicalsystems.m.engine.ConnectionFactory;
import edu.vanderbilt.clinicalsystems.m.engine.ConnectionStringBuilder;

public class VirtualConnectionFactory implements ConnectionFactory {

	private class VirtualDatabaseInstance {
		private final String m_name ;
		private boolean m_running = false ;
		private boolean m_initialized = false ;
		private Collection<VirtualConnection> m_connections = new ArrayList<VirtualConnection>();
		private boolean m_autoStart = false ;
		private boolean m_autoCreate = false ;
		
		public VirtualDatabaseInstance( String name ) {
			m_name = name ;
		}
		
		public void addConnection( VirtualConnection cxn ) throws ConnectionError {
			m_connections.add( cxn ) ;
			requireRunning() ;
		}
		
		public void dropConnection( VirtualConnection cxn ) {
			m_connections.remove( cxn ) ;
			if ( m_connections.isEmpty() )
				stop() ;
		}
		
		private void requiredInitialized() throws ConnectionError {
			if ( m_initialized )
				return ;
			
			if ( m_autoCreate )
				m_initialized = initializeDatabaseInstance( m_name ) ;
			else
				m_initialized = databaseInstanceInitialized( m_name ) ;
			
			if ( !m_initialized )
				throw new ConnectionError("database instance not initialized") ;
		}
		
		private void requireRunning() throws ConnectionError {
			if ( m_running ) return ;
			requiredInitialized() ;
			
			if ( m_autoStart )
				m_running = startDatabaseInstance( m_name ) ;
			else
				m_running = databaseInstanceRunning( m_name ) ;
		}
		
		public void stop() {
			if ( !m_running ) return ;
			m_running = !stopDatabaseInstance( m_name ) ;
		}

		public boolean autoStart(boolean autoStart) { return m_autoStart = autoStart ; }
		public boolean autoStart() { return m_autoStart ; }
		public boolean autoCreate(boolean autoCreate) { return m_autoCreate = autoCreate ; }
		public boolean autoCreate() { return m_autoCreate ; }
	}
	
	private Map<String,VirtualDatabaseInstance> m_instances = new HashMap<String, VirtualDatabaseInstance>();
	private Map<VirtualConnection,String> m_connections = new HashMap<VirtualConnection, String>();

	public VirtualConnectionFactory() {
	}
	
	private boolean databaseInstanceInitialized( String name ) {
		return false ;
	}
	
	private boolean initializeDatabaseInstance( String name ) {
		if ( databaseInstanceInitialized( name ) )
			return true ;
		
		return false ;
	}
	
	private boolean databaseInstanceRunning( String name ) {
		return startDatabaseInstance( name ) ;
	}
	
	private boolean startDatabaseInstance( String name ) {
		initializeDatabaseInstance(name) ;
		return false ;
	}
	
	private boolean stopDatabaseInstance( String name ) {
		return true ;
	}
	
	private VirtualDatabaseInstance acquireDatabaseInstance(VirtualConnection cxn) {
		return acquireDatabaseInstance( m_connections.get( cxn ) ) ;
	}
	
	private VirtualDatabaseInstance acquireDatabaseInstance(String name) {
		VirtualDatabaseInstance instance = m_instances.get(name) ;
		if ( null == instance )
			m_instances.put( name, instance = new VirtualDatabaseInstance(name) ) ;
		return instance ;
	}

	
	@Override
	public VirtualConnection createConnection(String connectionString) throws ConnectionError {
		ConnectionStringBuilder csb = ConnectionStringBuilder.parse(connectionString) ;
		String name = csb.target() ;
		VirtualDatabaseInstance instance = acquireDatabaseInstance(name) ;
		instance.autoStart( "yes".equals( csb.option("auto-start") ) ) ;
		instance.autoCreate( "yes".equals( csb.option("auto-create") ) ) ;
		String initialization = csb.option("init") ;
		VirtualConnection cxn = new VirtualConnection( new VirtualConnection.LifecycleListener() {
			
			@Override
			public void created(VirtualConnection cxn) {
			}
			
			@Override
			public void willOpen(VirtualConnection cxn) throws ConnectionError {
				acquireDatabaseInstance(cxn).addConnection(cxn) ;
			}
			
			@Override
			public void didOpen(VirtualConnection cxn) {
			}
			
			@Override
			public void willClose(VirtualConnection cxn) {
			}
			
			@Override
			public void didClose(VirtualConnection cxn) {
				acquireDatabaseInstance(cxn).dropConnection(cxn) ;
				m_connections.remove(cxn); 
			}
			
		}) ;
		m_connections.put(cxn,name);
		if ( null != initialization )
			try {
				cxn.open() ;
			} catch (ConnectionError ex) {
				throw new ConnectionError("failed to initialize (\"" + initialization + "\")", ex) ;
			}
		return cxn ;
	}

}
