package edu.vanderbilt.clinicalsystems.m.lang.model.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class MatchPattern extends Expression {

	private static final long serialVersionUID = 1L;
	
	public static class MatchSequence {
		private final List<Atom> m_atoms = new ArrayList<MatchPattern.Atom>() ;
		public MatchSequence( List<Atom> atoms ) {
			m_atoms.addAll( atoms ) ;
		}

		protected String unformattedRepresentation() {
			return m_atoms.stream().map( Atom::unformattedRepresentation ).collect( Collectors.joining() ) ;
		} 

		@Override public String toString() { return unformattedRepresentation() ; }

		@Override
		public boolean equals(Object obj) {
			if ( this == obj ) return true ;
			if ( null == obj ) return false ;
			if ( !(obj instanceof MatchSequence) ) return false ;
			MatchSequence matchSequence = (MatchSequence)obj ;
			return
				m_atoms.equals( matchSequence.m_atoms )
				;
		}
	}
	
	public static class Atom {
		private final Integer m_minimum ; /* null == 0 */ 
		private final Integer m_maximum ; /* null == infinite */
		private final MatchType m_matchType ;
		public Atom(Integer minimum, Integer maximum, MatchType matchType ) {
			m_minimum = minimum ;
			m_maximum = maximum ;
			m_matchType = matchType ;
		}
		public Integer minimum() { return m_minimum; }
		public Integer maximum() { return m_maximum; }
		public MatchType matchType() { return m_matchType; }

		protected String unformattedRepresentation() {
			String format ;
			if ( null == m_minimum && null == m_maximum )
				format = ".%3$s" ;
			else if ( null == m_maximum)
				format = "%1$d.%3$s" ;
			else if ( null == m_minimum)
				format = ".%2$d%3$s" ;
			else if ( m_minimum == m_maximum)
				format = "%1$sd%3$s" ;
			else
				format = "%1$d.%2$d%3$d" ;
			return String.format(format, m_minimum, m_maximum, m_matchType.unformattedRepresentation() ) ;
		}
	
		@Override public String toString() { return unformattedRepresentation() ; }

		@Override
		public boolean equals(Object obj) {
			if ( this == obj ) return true ;
			if ( null == obj ) return false ;
			if ( !(obj instanceof Atom) ) return false ;
			Atom atom = (Atom)obj ;
			return
				( m_minimum == null ? atom.m_minimum == null : m_minimum.equals( atom.m_minimum ) ) && 
				( m_maximum == null ? atom.m_maximum == null : m_maximum.equals( atom.m_maximum ) ) && 
				m_matchType.equals( atom.m_matchType )
				;
		}
	}
	
	public static abstract class MatchType {
		
		protected abstract String unformattedRepresentation() ;
		
		public interface Visitor<R> {
			R visitMatchType( MatchType matchType ) ;
			default R visitLiteral( LiteralMatchType literalMatchType ) { return visitMatchType(literalMatchType) ; } 
			default R visitCodeSet( CodeSetMatchType codeSetMatchType ) { return visitMatchType(codeSetMatchType) ; }
			default R visitUnion( UnionMatchType unionMatchType ) { return visitMatchType(unionMatchType) ; }
		}

		public abstract <R> R visit( Visitor<R> visitor ) ;
		
		@Override public String toString() {
			return unformattedRepresentation();
		}
		
		@Override
		public boolean equals(Object obj) {
			if ( this == obj ) return true ;
			if ( null == obj ) return false ;
			if ( !(obj instanceof MatchType) ) return false ;
			return true ;
		}
	}
	
	public static class LiteralMatchType extends MatchType {
		private final String m_value ;
		public LiteralMatchType(String value) {
			m_value = value;
		}

		public String value() { return m_value; }

		@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitLiteral(this) ; }

		@Override protected String unformattedRepresentation() { return "\"" + m_value + "\"" ; } 
		
		@Override
		public boolean equals(Object obj) {
			if ( this == obj ) return true ;
			if ( null == obj ) return false ;
			if ( !(obj instanceof LiteralMatchType) ) return false ;
			LiteralMatchType literalMatchType = (LiteralMatchType)obj ;
			return
				super.equals( literalMatchType ) &&
				m_value.equals( literalMatchType.m_value )
				;
		}
	}
	
	public static class CodeSetMatchType extends MatchType {
		private final EnumSet<PatternMatchCode> m_patternMatchCodes = EnumSet.noneOf(PatternMatchCode.class) ;
		public CodeSetMatchType( Collection<PatternMatchCode> patternMatchCodes ) {
			m_patternMatchCodes.addAll( patternMatchCodes ) ;
		}
		public EnumSet<PatternMatchCode> patternMatchCodes() { return m_patternMatchCodes; }

		@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitCodeSet(this) ; }

		@Override protected String unformattedRepresentation() {
			return m_patternMatchCodes.stream().map( PatternMatchCode::canonicalSymbol ).collect( Collectors.joining() ) ;
		}
		
		@Override
		public boolean equals(Object obj) {
			if ( this == obj ) return true ;
			if ( null == obj ) return false ;
			if ( !(obj instanceof CodeSetMatchType) ) return false ;
			CodeSetMatchType codeSetMatchType = (CodeSetMatchType)obj ;
			return
				super.equals( codeSetMatchType ) &&
				m_patternMatchCodes.equals( codeSetMatchType.m_patternMatchCodes )
				;
		}
	}
	
	public static class UnionMatchType extends MatchType {
		private final Collection<Atom> m_atoms = new ArrayList<MatchPattern.Atom>() ;
		public UnionMatchType( Collection<Atom> matches ) {
			m_atoms.addAll( matches ) ;
		}

		@Override public <R> R visit( Visitor<R> visitor ) { return visitor.visitUnion(this) ; }
		
		@Override protected String unformattedRepresentation() {
			return m_atoms.stream().map( Atom::unformattedRepresentation ).collect( Collectors.joining(",","(",")") ) ;
		} 

		@Override
		public boolean equals(Object obj) {
			if ( this == obj ) return true ;
			if ( null == obj ) return false ;
			if ( !(obj instanceof UnionMatchType) ) return false ;
			UnionMatchType unionMatchType = (UnionMatchType)obj ;
			return
				super.equals( unionMatchType ) &&
				m_atoms.containsAll( unionMatchType.m_atoms ) &&
				unionMatchType.m_atoms.containsAll( m_atoms )
				;
		}
	}
	
	private final MatchSequence m_matchSequence ;

	public MatchPattern(MatchSequence matchSequence) {
		m_matchSequence = matchSequence;
	}
	
	public MatchSequence matchSequence() { return m_matchSequence; }

	@Override
	public <R> R visit( Visitor<R> visitor ) { return visitor.visitMatchPattern(this) ; }

	@Override
	public void write(RoutineWriter writer) throws RoutineWriterException {
		writer.write(this);
	}

	@Override protected String unformattedRepresentation() {
		return m_matchSequence.unformattedRepresentation();
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) return true ;
		if ( null == obj ) return false ;
		if ( !(obj instanceof MatchPattern) ) return false ;
		MatchPattern matchPattern = (MatchPattern)obj ;
		return
			m_matchSequence.equals( matchPattern.m_matchSequence )
			;
	}
}
