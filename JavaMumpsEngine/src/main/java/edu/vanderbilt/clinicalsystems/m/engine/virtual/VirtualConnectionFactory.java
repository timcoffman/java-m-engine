package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.vanderbilt.clinicalsystems.m.engine.ConnectionError;
import edu.vanderbilt.clinicalsystems.m.engine.ConnectionFactory;
import edu.vanderbilt.clinicalsystems.m.engine.ConnectionStringBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;

public class VirtualConnectionFactory implements ConnectionFactory {

	private class VirtualDatabaseInstance {
		private final Database m_db = new Database() ;
		private final String m_name ;
		private Collection<VirtualConnection> m_connections = new ArrayList<VirtualConnection>();
		
		public VirtualDatabaseInstance( String name ) {
			m_name = name ;
		}
		
		public Database database() { return m_db ; }
		
		public void addConnection( VirtualConnection cxn ) throws ConnectionError {
			m_connections.add( cxn ) ;
		}
		
		public void dropConnection( VirtualConnection cxn ) {
			m_connections.remove( cxn ) ;
			if ( m_connections.isEmpty() )
				stop() ;
		}
			
		public void stop() {
			/* nothing to stop */
		}

	}
	
	private Map<String,VirtualDatabaseInstance> m_instances = new HashMap<String, VirtualDatabaseInstance>();
	private Map<VirtualConnection,String> m_connections = new HashMap<VirtualConnection, String>();

	public VirtualConnectionFactory() {
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
		VirtualDatabaseInstance instance = acquireDatabaseInstance( csb.target() ) ;
		String initialization = csb.option("init") ;
		VirtualConnection cxn = new VirtualConnection( instance.database(), new VirtualConnection.LifecycleListener() {
			
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
				cxn.submit( new Command( CommandType.EXECUTE, new ExpressionList( Constant.from(initialization) ) ));
			} catch (ConnectionError ex) {
				throw new ConnectionError("failed to initialize (\"" + initialization + "\")", ex) ;
			}
		return cxn ;
	}

}
