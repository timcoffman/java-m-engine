package edu.vanderbilt.clinicalsystems.m.engine.mumps;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

public class ProcessStreamPrinter implements Runnable {
	private final Process m_process ;
	private final Writer m_writer ;
	ProcessStreamPrinter( Process process, Writer writer ) {
		m_process = process ;
		m_writer = writer ;
	}
	
	@Override public void run() {
		try ( InputStreamReader reader = new InputStreamReader(m_process.getErrorStream()) ) {
			char[] buffer = new char[256] ;
			int n ;
			while ( -1 != ( n = reader.read(buffer) ) )
				m_writer.write( buffer, 0, n ) ;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
}