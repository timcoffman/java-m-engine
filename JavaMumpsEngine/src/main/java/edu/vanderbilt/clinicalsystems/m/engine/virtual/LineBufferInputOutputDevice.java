package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class LineBufferInputOutputDevice implements InputOutputDevice {

	private final Deque<String> m_input = new ArrayDeque<String>();
	private final Deque<String> m_output = new ArrayDeque<String>();
	
	@Override public String input(Integer characterLimit, Integer timeout) {
		return m_input.remove() ;
	}

	private final StringBuffer m_outputBuffer = new StringBuffer();
	
	@Override public void output(String text) throws IOException { m_outputBuffer.append(text) ; }
	@Override public void outputCarriageReturn() throws IOException { m_output.offer( m_outputBuffer.toString()) ; m_outputBuffer.setLength(0); }
	@Override public void outputPageFeed() throws IOException { /* nothing */ }
	@Override public void outputColumnMove(long column) throws IOException { /* nothing */ }

	public void offer( String text ) { m_input.offer(text) ; }
	public String take() { return m_output.poll() ; }
	
}