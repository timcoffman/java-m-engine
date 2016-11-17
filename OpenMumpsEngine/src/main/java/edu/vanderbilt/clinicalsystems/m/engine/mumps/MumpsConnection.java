package edu.vanderbilt.clinicalsystems.m.engine.mumps;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import edu.vanderbilt.clinicalsystems.m.engine.Connection;
import edu.vanderbilt.clinicalsystems.m.engine.ConnectionError;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineFormatter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineLinearWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineNativeFormatter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class MumpsConnection implements Connection {

	private static final String PROMPT = "M> ";

	private final LifecycleListener m_lifecycleListener ;
	
	private final ProcessBuilder m_processBuilder ;
	private Process m_process = null ;
	private BlockingInputStreamReader m_stdoutReader = null ;
	private BlockingInputStreamReader m_stderrReader = null ;
	private BlockingOutputStreamWriter m_stdinWriter = null ;
	
	private final RoutineFormatter m_routineFormatter ;
	
	public interface LifecycleListener {
		void created( MumpsConnection cxn ) throws ConnectionError ;
		void willOpen( MumpsConnection cxn ) throws ConnectionError ;
		void didOpen( MumpsConnection cxn ) throws ConnectionError ;
		void willClose( MumpsConnection cxn ) throws ConnectionError ;
		void didClose( MumpsConnection cxn ) throws ConnectionError ;
	}
	
	public MumpsConnection( ProcessBuilder processBuilder, LifecycleListener lifecycleListener ) throws ConnectionError {
		m_processBuilder = processBuilder ; 
		m_lifecycleListener = lifecycleListener ; m_lifecycleListener.created(this) ;
		
		m_routineFormatter = new RoutineNativeFormatter() ;
	}
	
	private static final Map <Integer,String> PROCESS_ERROR_MESSAGES = new HashMap<>() ;
	static {
		PROCESS_ERROR_MESSAGES.put( 0, "success" ) ;
		PROCESS_ERROR_MESSAGES.put( 2, "database not running" ) ;
		PROCESS_ERROR_MESSAGES.put(17, "database file already exists" ) ; /* EEXIST */
		PROCESS_ERROR_MESSAGES.put(47, "can't assign requested address (%1$d)" ) ;
		PROCESS_ERROR_MESSAGES.put(49, "can't assign requested address (%1$d)" ) ;
	}
	
	private static ConnectionError createConnectionError( Process process ) {
		return new ConnectionError( formatProcessExitMessage(process) ) ;
	}

	private static String formatProcessExitMessage(Process process) {
		String message = PROCESS_ERROR_MESSAGES.get(process.exitValue() );
		if ( null == message )
			message = "process exited (%1$d)" ;
		String formatProcessExitMessage = String.format( message, process.exitValue() );
		return formatProcessExitMessage;
	}
	
	
	private boolean expectPrompt( long milliseconds ) throws ConnectionError, InterruptedException {
		return m_stdoutReader.expectPartial(PROMPT,3000) ;
	}
	
	private void requirePrompt( long milliseconds ) throws ConnectionError, InterruptedException {
		if ( !m_process.isAlive() )
			throw createConnectionError( m_process ) ;
		if ( !expectPrompt(3000) )
			throw new ConnectionError("no prompt" ) ;
	}
	
	@Override
	public void submit(RoutineElement routineElement, Reader reader, Writer writer) throws ConnectionError {
				
				
		try {
			String commandText = formatRoutineElement(routineElement);
			
			try {
				boolean connected = false ;
				int n = 0 ;
				while ( !connected ) {
					try {
						open() ;
						requirePrompt(3000);
					} catch ( ConnectionError ex ) {
						if ( n > 2 )
							throw ex ;
						System.err.println( "connection failed; retrying... (" + ex.getMessage() + ")") ;
					}
					++n ;
				}
				
				m_stdinWriter.offer( commandText );
				
				boolean gotPrompt = expectPrompt(3000) ; 
				
				String line ;
				while ( null != (line = m_stdoutReader.poll() ) )
					writer.write( line );
				
			} finally {
				String errorLine ;
				while ( null != (errorLine = m_stderrReader.poll()) )
					System.err.println( errorLine );
			}
			
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			
		} catch (RoutineWriterException ex) {
			ex.printStackTrace();
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private String formatRoutineElement(RoutineElement element) throws RoutineWriterException {
		StringWriter commandWriter = new StringWriter() ;
		RoutineWriter routineWriter = new RoutineLinearWriter(commandWriter, m_routineFormatter);
		element.write(routineWriter);
		String commandText = commandWriter.toString() ;
		return commandText;
	}

	public void open() throws IOException, InterruptedException, ConnectionError {
		if ( null != m_process ) return ;
		m_lifecycleListener.willOpen(this) ;
		System.err.println("exec: " + m_processBuilder.command().stream().collect(Collectors.joining(" ") ) ) ;
		m_process = m_processBuilder.start() ;
		m_stdoutReader = BlockingInputStreamReader.start( m_process.getInputStream(), "connection stdout reader" ) ;
		m_stderrReader = BlockingInputStreamReader.start( m_process.getErrorStream(), "connection stderr reader" ) ;
		m_stdinWriter = BlockingOutputStreamWriter.start( m_process.getOutputStream(), "connection stdin writer" ) ;
		
		m_lifecycleListener.didOpen(this) ;
	}
	
	@Override
	public void close() throws Exception {
		if ( null != m_process ) {
			m_lifecycleListener.willClose(this) ;
			
			if ( m_process.isAlive() && m_stdoutReader.expectPartial(PROMPT, 3000)) {
				m_stdinWriter.offer("H") ;
			}
			
			if ( m_process.waitFor(10, TimeUnit.SECONDS) ) {
				// exited normally
				System.err.println( "process exited: " + formatProcessExitMessage( m_process ) ) ;
			} else {
				m_process.destroy();
			}

		}
		m_lifecycleListener.didClose(this) ;
	}

}
