package edu.vanderbilt.clinicalsystems.m.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EngineException extends Exception {
	private static final long serialVersionUID = 1L;

	private final ErrorCode m_errorCode ;
	private final Map<String,String> m_context ;
	
	private static Map<String,String> createContext(String[] errorContextPairs) {
		Map<String,String> context = new HashMap<String, String>() ;
		for ( int i = 0 ; i < errorContextPairs.length-1 ; i+=2 ) {
			String key   = errorContextPairs[i+0] ;
			String value = errorContextPairs[i+1] ;
			context.put(key, value) ;
		}
		return context ;
	}
	
	private static String formatMessage( ErrorCode errorCode, Map<String,String> context ) {
		StringBuffer sb = new StringBuffer() ;
		Matcher m = Pattern.compile("(\\{[^\\}]+\\})").matcher( errorCode.messageFormat() ) ;
		while ( m.find() ) {
			String value = context.get( m.group(1) ) ;
			if ( null == value )
				value = "???" ;
			m.appendReplacement(sb, value) ;
		}
		m.appendTail(sb) ;
		return sb.toString() ;
	}
	
	public EngineException(ErrorCode errorCode, String ... errorContextPairs) {
		super(formatMessage(errorCode,createContext(errorContextPairs) ));
		m_errorCode = errorCode ;
		m_context = Collections.unmodifiableMap(createContext(errorContextPairs)) ;
	}
	
	public EngineException(ErrorCode errorCode, String [] errorContextPairs, Throwable cause) {
		super(formatMessage(errorCode,createContext(errorContextPairs) ),cause);
		m_errorCode = errorCode ;
		m_context = Collections.unmodifiableMap(createContext(errorContextPairs)) ;
	}

	public ErrorCode errorCode() { return m_errorCode ; }
	public Map<String,String> context() { return m_context ; }
	
}
