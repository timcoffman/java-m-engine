package edu.vanderbilt.clinicalsystems.m.engine;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConnectionStringBuilder {

	public static final String KEY_TARGET = "target" ;
	
	private final Map<String,String> m_options = new HashMap<String, String>();
	
	public ConnectionStringBuilder( String target ) {
		Objects.requireNonNull(target) ;
		withOption( KEY_TARGET, target ) ;
	}
	
	private ConnectionStringBuilder( Map<String,String> options ) {
		m_options.putAll(options) ;
	}
	
	public String target() { return option(KEY_TARGET) ; }
	
	public static ConnectionStringBuilder parse( String s ) {
		Objects.requireNonNull(s);
		if ( s.isEmpty() )
			throw new IllegalArgumentException("missing connection string") ;
		Map<String,String> options =
				Arrays.stream(s.split("[;]"))
				.map((e)->e.split("[=]"))
				.collect(Collectors.toMap((e)->e[0], (e)->e[1]))
				;
		return new ConnectionStringBuilder( options ) ;
	}
	
	private static String escape( String s ) {
		if ( null == s )
			return null ;
		try {
			return URLEncoder.encode(s,"UTF-8") ;
		} catch (UnsupportedEncodingException ex) {
			return s ;
		}
	}
	
	private static String unescape( String s ) {
		if ( null == s )
			return null ;
		try {
			return URLDecoder.decode(s,"UTF-8") ;
		} catch (UnsupportedEncodingException ex) {
			return s ;
		}
	}
	
	public ConnectionStringBuilder withOption( String key, String value ) {
		option(key,value) ;
		return this ;
	}
	
	public String option( String key ) {
		return unescape(m_options.get(escape(key))) ;
	}
	
	public String option( String key, String value ) {
		String formerValue = option(key) ;
		m_options.put( escape(key), escape(value) ) ;
		return formerValue ;
	}
	
	@Override public String toString() {
		return m_options.entrySet().stream().map((e)->e.getKey()+"="+e.getValue()).collect(Collectors.joining(";")) ;
	}
	
}
