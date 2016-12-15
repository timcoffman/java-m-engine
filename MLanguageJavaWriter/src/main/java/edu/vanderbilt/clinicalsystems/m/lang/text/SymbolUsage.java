package edu.vanderbilt.clinicalsystems.m.lang.text;

import static edu.vanderbilt.clinicalsystems.m.lang.text.Representation.NATIVE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SymbolUsage {
	private final Optional<SymbolUsage> m_parent ;
	
	private static class Usage {
		private final Collection<Supplier<Optional<Representation>>> m_usedAs = new ArrayList<Supplier<Optional<Representation>>>();
		private boolean m_indexedByKey = false;
		private boolean m_passedByReference = false;
		private boolean m_usedAsParameter = false;
		
		public Collection<Supplier<Optional<Representation>>> usedAs() { return m_usedAs; }
		public Collection<Supplier<Optional<Representation>>> usedAs(Supplier<Optional<Representation>> usedAs) {
			Collection<Supplier<Optional<Representation>>> oldValue = new ArrayList<Supplier<Optional<Representation>>>( m_usedAs ) ;
			m_usedAs.add( usedAs ) ;
			return oldValue ;
		}
		
		public boolean indexedByKey() { return m_indexedByKey; }
		public boolean indexedByKey(boolean indexedByKey) {
			boolean oldValue = m_indexedByKey ;
			m_indexedByKey = indexedByKey;
			return oldValue ;
		}
		
		public boolean passedByReference() { return m_indexedByKey; }
		public boolean passedByReference(boolean passedByReference) {
			boolean oldValue = m_passedByReference ;
			m_passedByReference = passedByReference;
			return oldValue ;
		}
		
		public boolean usedAsParameter() { return m_indexedByKey; }
		public boolean usedAsParameter(boolean usedAsParameter) {
			boolean oldValue = m_usedAsParameter ;
			m_usedAsParameter = usedAsParameter;
			return oldValue ;
		}
		
	}

	private final Usage m_returns = new Usage() ;

	private final Map<String,Usage> m_used = new HashMap<String, Usage>();

	public static SymbolUsage createRoot() { return new SymbolUsage() ; } 
	
	private SymbolUsage() { m_parent = Optional.empty() ; }
	public SymbolUsage( SymbolUsage parent ) { m_parent = Optional.of(parent) ; }
	
	protected Optional<SymbolUsage> parent() { return m_parent ; }
	
	public Set<String> symbols() { return m_used.keySet(); }
	
	public String describe( String symbol ) {
		Usage usage = m_used.get( symbol );
		if ( null != usage ) {
			return usage.usedAs().stream().map( Supplier::get ).map( Object::toString ).collect( Collectors.joining(" + ")) ;
		} else if ( m_parent.isPresent() ) {
			return "parent -> " + m_parent.get().describe(symbol) ;
		} else {
			return "-not declared-" ;
		}
	}
	
	public Supplier<Optional<Representation>> impliedReturnType() {
		return impliedRepresentation(m_returns);
	}
	
	public boolean declared(String symbol) {
		return m_used.containsKey( symbol ) ;
	}
	
	private class SymbolImplication implements Supplier<Optional<Representation>> {
		private final String m_symbol ;
		public SymbolImplication( String symbol ) {
			m_symbol = symbol ;
		}
		@Override public Optional<Representation> get() {
			Usage usage = m_used.get( m_symbol );
			if ( null != usage ) {
				return impliedRepresentation(usage).get();
			} else if ( m_parent.isPresent() ) {
				return m_parent.get().impliedRepresentation(m_symbol).get() ;
			} else {
				return Optional.empty() ;
			}
		}
		@Override public String toString() { return describe(m_symbol); }
	}
	
	public Supplier<Optional<Representation>> impliedRepresentation(String symbol) {
		return new SymbolImplication(symbol) ;
	}
//	public Supplier<Optional<Representation>> impliedRepresentation(String symbol) {
//		return ()->{
//			Usage usage = m_used.get( symbol );
//			if ( null != usage ) {
//				return impliedRepresentation(usage).get();
//			} else if ( m_parent.isPresent() ) {
//				return m_parent.get().impliedRepresentation(symbol).get() ;
//			} else {
//				return Optional.empty() ;
//			}
//		} ;
//	}

	private Supplier<Optional<Representation>> impliedRepresentation(Usage usage) {
		if ( usage.indexedByKey() || usage.passedByReference() )
			return NATIVE.supplier() ;
		else
			return impliedRepresentation( usage.usedAs() );
	}
	
	private Supplier<Optional<Representation>> commonRepresentation( Collection<Supplier<Optional<Representation>>> representations ) {
		Optional<Representation> representation = representations.stream()
			.map(Supplier::get)
			.filter(Optional::isPresent)
			.map( Optional::get )
			.reduce( Representation::commonRepresentation )
			;
		return ()->representation ;
	}
	
	private Supplier<Optional<Representation>> impliedRepresentation(Collection<Supplier<Optional<Representation>>> representations) {
		return commonRepresentation(representations) ;
	}

	public void scopeReturns(Supplier<Optional<Representation>> representation) {
		m_parent
			.orElseThrow( ()->new IllegalStateException( "method returning from root context" ) )
			.scopeReturns( representation )
			;
	}

	public void scopeAccepts(int position,Supplier<Optional<Representation>> representation) {
		m_parent
			.orElseThrow( ()->new IllegalStateException( "method returning from root context" ) )
			.scopeAccepts( position, representation )
			;
	}

	public void declaredAs(String symbol) {
		Objects.requireNonNull( symbol ) ;
		declare( symbol ) ;
	}
	
	public void declaredAs(String symbol, Representation representation) {
		Objects.requireNonNull( symbol ) ;
		Objects.requireNonNull( representation ) ;
		declaredAs( symbol, representation.supplier() ) ;
	}
	
	public void declaredAs(String symbol, Supplier<Optional<Representation>> representation) {
		Objects.requireNonNull( symbol ) ;
		Objects.requireNonNull( representation ) ;
		declare( symbol ).usedAs( representation ) ;
//		System.out.println( "\"" + symbol + "\" -> " + representation ) ;
	}
	
	public void usedAs(String symbol, Supplier<Optional<Representation>> representation) {
		Objects.requireNonNull( symbol ) ;
		Objects.requireNonNull( representation ) ;
		used(symbol, u->u.usedAs(representation) ) ;
	}

	public void usedAs(String symbol, Representation representation) {
		Objects.requireNonNull( symbol ) ;
		Objects.requireNonNull( representation ) ;
		usedAs(symbol, representation.supplier()) ;
	}
	
	public void passedByReference(String symbol) {
		Objects.requireNonNull( symbol ) ;
		used(symbol, u->u.passedByReference(true) ) ;
	}
	
	public void indexedByKey(String symbol) {
		Objects.requireNonNull( symbol ) ;
		used(symbol, u->u.indexedByKey(true) ) ;
	}
	
	public void usedAsParameter(String symbol) {
		Objects.requireNonNull( symbol ) ;
		declare(symbol).usedAsParameter(true) ;
	}

	private Usage declare( String symbol ) {
		Objects.requireNonNull( symbol ) ;
		Usage usage = new Usage();
		m_used.put( symbol, usage ) ;
		return usage ;
	}
	
	protected void used( String symbol, Consumer<Usage> action ) {
		Usage usage = m_used.get(symbol);
		if ( null != usage ) {
			action.accept(usage);
		} else if ( m_parent.isPresent() ) {
			m_parent.get().used( symbol, action ) ;
		} else {
			action.accept( declare(symbol) ); // root, auto-declare it here
		}
	}
	
	
}
