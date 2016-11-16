package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Accumulates {@link RoutineFormatter} method calls for later {@link #playback} on another {@link RoutineFormatter}   
 */
class RoutineDeferringFormatter extends RoutineCallProducingFormatter {

	private Queue<FormattingCall> m_deferredCalls = new ArrayDeque<FormattingCall>() ;
	
	@Override
	protected void produce(FormattingCall formattingCall) { m_deferredCalls.offer(formattingCall) ; }

	/**
	 * Executes all accumulated {@link RoutineFormatter} method calls on the given {@link RoutineFormatter}
	 * <p>
	 * After this method completes, accumulated method calls are cleared
	 * 
	 * @param routineFormatter receives the method calls
	 * @throws IOException
	 */
	public void playback( RoutineFormatter routineFormatter ) throws IOException {
		FormattingCall deferredCall ;
		while ( null != (deferredCall = m_deferredCalls.poll()) )
			deferredCall.executeOn(routineFormatter);
	}
	
}
