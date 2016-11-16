package edu.vanderbilt.clinicalsystems.m.engine.mumps;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class BlockingOutputStreamWriter implements Runnable {
	private final OutputStream m_outputStream;
	private final BlockingQueue<String> m_sending = new LinkedBlockingQueue<String>(1000);
	private boolean m_shouldClose = false ;
	
	BlockingOutputStreamWriter( OutputStream outputStream ) {
		m_outputStream = outputStream ;
	}

	public static final BlockingOutputStreamWriter start( OutputStream outputStream, String name ) {
		BlockingOutputStreamWriter buffer = new BlockingOutputStreamWriter(outputStream);
		Thread thread = new Thread( buffer ) ;
		thread.setDaemon(true);
		thread.setName(name);
		thread.start();
		return buffer ;
	}
	
	public void offer(String line) { m_sending.offer(line); }
	
	public void close() { m_shouldClose = true ; }
	
	@Override public void run() {
		try ( OutputStreamWriter writer = new OutputStreamWriter(m_outputStream) ) {
			while ( !m_shouldClose ) {
				String sending = m_sending.poll( 500, TimeUnit.MILLISECONDS) ;
				if ( null != sending ) {
					System.out.println( m_outputStream.getClass().getSimpleName() + ": " + sending );
					writer.write(sending);
					writer.write('\n');
				}
			}
		} catch (IOException | InterruptedException ex) {
			ex.printStackTrace();
		}
	}
	
}