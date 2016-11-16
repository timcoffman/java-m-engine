package edu.vanderbilt.clinicalsystems.m.engine.mumps;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import edu.vanderbilt.clinicalsystems.m.engine.ConnectionError;
import edu.vanderbilt.clinicalsystems.m.engine.ConnectionFactory;
import edu.vanderbilt.clinicalsystems.m.engine.ConnectionStringBuilder;

public class MumpsConnectionFactory implements ConnectionFactory {

	private class MumpsDatabaseInstance {
		private final String m_name ;
		private String m_location = null ;
		private String m_executable = null ;
		private boolean m_running = false ;
		private boolean m_initialized = false ;
		private Collection<MumpsConnection> m_connections = new ArrayList<MumpsConnection>();
		private boolean m_autoStart = false ;
		private boolean m_autoCreate = false ;
		
		public MumpsDatabaseInstance( String name ) {
			m_name = name ;
		}
		
		public void addConnection( MumpsConnection cxn ) throws ConnectionError {
			m_connections.add( cxn ) ;
			requireRunning() ;
		}
		
		public void dropConnection( MumpsConnection cxn ) {
			m_connections.remove( cxn ) ;
			if ( m_connections.isEmpty() )
				stop() ;
		}
		
		private void requiredInitialized() throws ConnectionError {
			if ( m_initialized )
				return ;
			
			if ( m_autoCreate )
				m_initialized = initializeDatabaseInstance( m_name, new File(m_location), new File(m_executable) ) ;
			else
				m_initialized = databaseInstanceInitialized( m_name, new File(m_location) ) ;
			
			if ( !m_initialized )
				throw new ConnectionError("database instance not initialized") ;
		}
		
		private void requireRunning() throws ConnectionError {
			if ( m_running ) return ;
			requiredInitialized() ;
			
			if ( m_autoStart )
				m_running = startDatabaseInstance( m_name, new File(m_location), new File(m_executable) ) ;
			else
				m_running = databaseInstanceRunning( m_name, new File(m_location), new File(m_executable) ) ;
		}
		
		public void stop() {
			if ( !m_running ) return ;
			m_running = !stopDatabaseInstance( m_name ) ;
		}

		public String location(String value) { return m_location = value ; }
		public String location() { return m_location ; }
		public String executable(String value) { return m_executable = value ; }
		public String executable() { return m_executable ; }
		public boolean autoStart(boolean autoStart) { return m_autoStart = autoStart ; }
		public boolean autoStart() { return m_autoStart ; }
		public boolean autoCreate(boolean autoCreate) { return m_autoCreate = autoCreate ; }
		public boolean autoCreate() { return m_autoCreate ; }
	}
	
	private Map<String,MumpsDatabaseInstance> m_instances = new HashMap<String, MumpsDatabaseInstance>();
	private Map<MumpsConnection,String> m_connections = new HashMap<MumpsConnection, String>();

	public MumpsConnectionFactory() {
	}
	
	private String databaseFileName( String name ) { return name.toLowerCase() + "db" ; }
	private String volumeName( String name ) { return name.toUpperCase() ; }
	
	private boolean databaseInstanceInitialized( String name, File location ) {
		File instanceFile = new File(location, databaseFileName(name) ) ;
		return instanceFile.exists() ;
	}
	
	private boolean initializeDatabaseInstance( String name, File location, File executable) {
		if ( databaseInstanceInitialized( name, location ) )
			return true ;
		
		ProcessBuilder processBuilder = new ProcessBuilder(executable.getAbsolutePath(),"-v",name.toUpperCase(),"-b","16","-s","1000",databaseFileName(name)) ;
		processBuilder.directory( location ) ;
		try {
			System.err.println("exec: " + processBuilder.command().stream().collect(Collectors.joining(" ") ) ) ;
			Process process = processBuilder.start() ;
			if ( !process.waitFor(120, TimeUnit.SECONDS) )
				return false ;
			return 0 == process.exitValue() ;
			
		} catch (IOException ex) {
			ex.printStackTrace();
			return false ;
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			return false ;
		}
	}
	
	private void copyStream( InputStream is, PrintStream ps ) {
		byte[] buffer = new byte[1024] ;
		int n ;
		try {
			while ( -1 != (n = is.read(buffer)) )
				ps.write( buffer, 0, n );
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private boolean databaseInstanceRunning( String name, File location, File executable) {
		return startDatabaseInstance( name, location, executable ) ;
	}
	
	private boolean startDatabaseInstance( String name, File location, File executable) {
		initializeDatabaseInstance(name, location, executable) ;
		ProcessBuilder processBuilder = new ProcessBuilder(executable.getAbsolutePath(),"-j2",databaseFileName(name)) ;
		processBuilder.directory( location ) ;
		try {
			System.err.println("exec: " + processBuilder.command().stream().collect(Collectors.joining(" ") ) ) ;
			Process process = processBuilder.start() ;
			InputStream stdout = new BufferedInputStream( process.getInputStream() );
			InputStream stderr = new BufferedInputStream( process.getErrorStream() );
			
			if ( !process.waitFor(120, TimeUnit.SECONDS) )
				return false ;
			copyStream( stdout, System.out ) ;
			copyStream( stderr, System.err ) ;
			
			int result = process.exitValue() ;
			if ( 0 == result || 17 == result )
				return true ;

			return false ;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false ;
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			return false ;
		}
	}
	
	private boolean stopDatabaseInstance( String name ) {
		return true ;
	}
	
	private MumpsDatabaseInstance acquireDatabaseInstance(MumpsConnection cxn) {
		return acquireDatabaseInstance( m_connections.get( cxn ) ) ;
	}
	
	private MumpsDatabaseInstance acquireDatabaseInstance(String name) {
		MumpsDatabaseInstance instance = m_instances.get(name) ;
		if ( null == instance )
			m_instances.put( name, instance = new MumpsDatabaseInstance(name) ) ;
		return instance ;
	}

	
	@Override
	public MumpsConnection createConnection(String connectionString) throws ConnectionError {
		ConnectionStringBuilder csb = ConnectionStringBuilder.parse(connectionString) ;
		String name = csb.target() ;
		MumpsDatabaseInstance instance = acquireDatabaseInstance(name) ;
		String location = instance.location( csb.option("location") ) ;
		String executable = instance.executable( csb.option("executable") ) ;
		instance.autoStart( "yes".equals( csb.option("auto-start") ) ) ;
		instance.autoCreate( "yes".equals( csb.option("auto-create") ) ) ;
		String initialization = csb.option("init") ;
		ProcessBuilder processBuilder ;
		if ( null != initialization )
			processBuilder = new ProcessBuilder(executable,"-x",initialization, databaseFileName(name)) ;
		else
			processBuilder = new ProcessBuilder(executable,databaseFileName(name)) ;
		processBuilder.directory( new File(location) ) ;
		
		MumpsConnection cxn = new MumpsConnection( processBuilder, new MumpsConnection.LifecycleListener() {
			
			@Override
			public void created(MumpsConnection cxn) {
			}
			
			@Override
			public void willOpen(MumpsConnection cxn) throws ConnectionError {
				acquireDatabaseInstance(cxn).addConnection(cxn) ;
			}
			
			@Override
			public void didOpen(MumpsConnection cxn) {
			}
			
			@Override
			public void willClose(MumpsConnection cxn) {
			}
			
			@Override
			public void didClose(MumpsConnection cxn) {
				acquireDatabaseInstance(cxn).dropConnection(cxn) ;
				m_connections.remove(cxn); 
			}
			
		}) ;
		m_connections.put(cxn,name);
		if ( null != initialization )
			try {
				cxn.open() ;
			} catch (IOException | InterruptedException ex) {
				throw new ConnectionError("failed to initializer (\"" + initialization + "\")", ex) ;
			}
		return cxn ;
	}

}
