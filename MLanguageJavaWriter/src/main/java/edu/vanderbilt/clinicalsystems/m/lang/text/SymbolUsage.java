package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public class SymbolUsage {

	private final Set<String> m_declared = new HashSet<String>();
	private final Map<String,EnumSet<Representation>> m_used = new HashMap<String, EnumSet<Representation>>();

	public SymbolUsage() { }
	
	public Set<String> symbols() { return m_used.keySet(); }
	
	public void importUndeclared( SymbolUsage symbolUsage ) {
		m_declared.addAll( symbolUsage.m_declared ) ;
		m_used.putAll( symbolUsage.m_used );
	}
	
	public SymbolUsage undeclared() {
		SymbolUsage symbolUsage = new SymbolUsage() ;
		symbolUsage.m_used.putAll( m_used ) ;
		symbolUsage.m_used.keySet().removeAll( m_declared ) ;
		return symbolUsage ;
	}
	
	public boolean declared(String symbol) {
		return m_declared.contains( symbol ) ;
	}
	
	public JType impliedType(String symbol, RoutineJavaBuilderContext builderContext) {
		EnumSet<Representation> usage = m_used.get( symbol ) ;
		if ( null != usage ) {
			EnumSet<Representation> working = EnumSet.copyOf(usage) ;
			if ( !working.remove(Representation.ANY) ) {
				/* didn't contain any */
				if ( working.remove(Representation.BOOLEAN) ) {
					if ( working.isEmpty() )
						return builderContext.codeModel().BOOLEAN ;
				}
			}
		}		
		return builderContext.codeModel().ref( builderContext.env().valueClass() );
	}

	public void declaredAs(JExpression e, Representation representation) {
		String symbol = symbolFor(e);
		if ( null != symbol ) {
			m_declared.add( symbol ) ;
			usedAs(symbol, representation);
		}
	}
	
	public void usedAs(JExpression e, Representation representation) {
		String symbol = symbolFor(e);
		if ( null != symbol ) {
			usedAs( symbol, representation ) ;
		}
	}

	private String symbolFor( JExpression e ) {
		if ( e instanceof JVar )
			return symbolFor( (JVar)e ) ;
		else if ( e instanceof JFieldVar )
			return symbolFor( (JFieldVar)e ) ;
		else if ( e instanceof JFieldRef )
			return symbolFor( (JFieldRef)e ) ;
		else
			return null ;
	}
	
	public String symbolFor(JVar variable) {
		return variable.name() ;
	}

	public String symbolFor(JFieldVar field) {
		return field.name() ;
	}
	
	public String symbolFor(JFieldRef field) {
		java.io.ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
		java.io.PrintWriter pw = new java.io.PrintWriter(baos);
		field.generate( new JFormatter(pw, "" ));
		pw.flush();
		try {
			String name = baos.toString("UTF-8");
			return name;
		} catch (UnsupportedEncodingException ex) {
			return null ;
		}
	}
	
	private void usedAs(String symbol, Representation representation) {
		Objects.requireNonNull( symbol ) ;
		Objects.requireNonNull( representation ) ;
		EnumSet<Representation> usage = m_used.get( symbol ) ;
		if ( null == usage )
			m_used.put( symbol, usage = EnumSet.noneOf(Representation.class) ) ;
		usage.add(representation) ;
	}

}
