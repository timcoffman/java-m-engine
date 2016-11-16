package edu.vanderbilt.clinicalsystems.m.engine.mumps;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingInputStreamReader implements Runnable {
	private final InputStream m_inputStream;
	private final BlockingQueue<String> m_received = new LinkedBlockingQueue<String>(1000);
	private final StringWriter m_receiving = new StringWriter();
	
	BlockingInputStreamReader( InputStream inputStream ) {
		m_inputStream = inputStream ;
	}

	public static final BlockingInputStreamReader start( InputStream inputStream, String name ) {
		BlockingInputStreamReader buffer = new BlockingInputStreamReader(inputStream);
		Thread thread = new Thread( buffer ) ;
		thread.setDaemon(true);
		thread.setName(name);
		thread.start();
		return buffer ;
	}
	
	public String partial() { return m_receiving.toString() ; }
	
	public String peek() { return m_received.peek() ; }
	public String poll() { return m_received.poll() ; }
	
	public String take() throws InterruptedException { return m_received.take() ; }
	
	@Override public void run() {
		try ( InputStreamReader reader = new InputStreamReader(m_inputStream) ) {
			char[] buffer = new char[256] ;
			int n ;
			while ( -1 != ( n = reader.read(buffer) ) ) {
				for ( int p=0 ; p < n ; ++p) {
					if ( buffer[p] == '\n' ) {
						String line = m_receiving.toString();
						m_receiving.getBuffer().setLength(0);
						System.err.println( m_inputStream.getClass().getSimpleName() + ": " + line);
						m_received.offer( line ) ;
					} else {
						m_receiving.write( buffer, p, 1 ) ;
					}
				}
				System.err.println( m_inputStream.getClass().getSimpleName() + ": " + m_receiving.toString() + "...");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public boolean expectPartial(String partial, long milliseconds) throws InterruptedException {
		long start = System.currentTimeMillis() ;
		while ( System.currentTimeMillis() - start < milliseconds ) {
			if ( partial.equals( partial() ) )
				return true ;
			Thread.sleep(100);
		}
		return false ;
	}
	
}