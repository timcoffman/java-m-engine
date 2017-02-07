package edu.vanderbilt.clinicalsystems.m.lang.text;

import static edu.vanderbilt.clinicalsystems.m.lang.model.expression.PatternMatchCode.ALPHABETIC;
import static edu.vanderbilt.clinicalsystems.m.lang.model.expression.PatternMatchCode.CONTROL;
import static edu.vanderbilt.clinicalsystems.m.lang.model.expression.PatternMatchCode.EVERYTHING;
import static edu.vanderbilt.clinicalsystems.m.lang.model.expression.PatternMatchCode.LOWERCASE;
import static edu.vanderbilt.clinicalsystems.m.lang.model.expression.PatternMatchCode.NUMERIC;
import static edu.vanderbilt.clinicalsystems.m.lang.model.expression.PatternMatchCode.PUNCTUATION;
import static edu.vanderbilt.clinicalsystems.m.lang.model.expression.PatternMatchCode.UPPERCASE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BinaryOperation;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.MatchPattern;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.PatternMatchCode;

public class PatternMatchParserTest {

	@Test
	public void canParsePatternMatchWithLowerLimit() {
		assertThatPatternMatchIsParsedAs(
				"3.\"abc\"",
				atLeast(3).literal("abc")
			);
	}
	
	@Test
	public void canParsePatternMatchWithUpperLimit() {
		assertThatPatternMatchIsParsedAs(
				".7\"abc\"",
				noMoreThan(7).literal("abc")
				);
	}
	
	@Test
	public void canParsePatternMatchWithLowerAndUpperLimit() {
		assertThatPatternMatchIsParsedAs(
				"3.7\"abc\"",
				atLeast(3).butNoMoreThan(7).literal("abc")
			);
	}
	
	@Test
	public void canParsePatternMatchWithNoLimits() {
		assertThatPatternMatchIsParsedAs(
				".\"abc\"",
				anyNumber().literal("abc")
				);
	}
	
	@Test
	public void canParsePatternMatchWithLiteral() {
		assertThatPatternMatchIsParsedAs(
				"1\"abc\"",
				exactly(1).literal("abc")
			);
	}
	
	@Test
	public void canParsePatternMatchWithUnion() {
		assertThatPatternMatchIsParsedAs(
				"1(3\"abc\",7A)",
				exactly(1).ofAny(
						exactly(3).literal("abc"),
						exactly(7).ofCodes( ALPHABETIC )
						)
				);
	}
	
	@Test
	public void canParsePatternMatchWithCodeSet() {
		assertThatPatternMatchIsParsedAs(
				"1A",
				exactly(1).ofCodes( ALPHABETIC )
			) ;
		assertThatPatternMatchIsParsedAs(
				"1ANP",
				exactly(1).ofCodes( ALPHABETIC, NUMERIC, PUNCTUATION )
				) ;
		assertThatPatternMatchIsParsedAs(
				"1PNALCUE",
				exactly(1).ofCodes( ALPHABETIC, NUMERIC, PUNCTUATION, CONTROL, EVERYTHING, LOWERCASE, UPPERCASE )
				) ;
	}
	
	private void assertThatPatternMatchIsParsedAs( String pattern, MatchPattern.Atom ... atoms ) {
		assertThatPatternMatchIsParsedAs( pattern, sequence(atoms) ) ;
	}
	
	private void assertThatPatternMatchIsParsedAs( String pattern, MatchPattern.MatchSequence sequence ) {
		assertThatPatternMatchIsParsedAs( pattern, pattern(sequence) ) ;
	}
	
	private void assertThatPatternMatchIsParsedAs( String pattern, MatchPattern matchPattern ) {
		Expression expr = new RoutineANTLRParser().parseExpression("x?" + pattern ) ;
		assertThat( expr, equalTo(
					new BinaryOperation(
							new DirectVariableReference(Scope.TRANSIENT, "x"),
							OperatorType.MATCH,
							matchPattern
					)
				)
			) ;
	}
	
	private MatchPattern pattern( MatchPattern.MatchSequence sequence ) {
		return new MatchPattern(sequence) ;
	}
	
	private MatchPattern.MatchSequence sequence( MatchPattern.Atom ... atoms ) {
		return new MatchPattern.MatchSequence( Arrays.asList(atoms) ) ;
	}
	
	private AtomBuilder exactly(int count) {
		return new AtomBuilder(count,count) ;
	}
	
	private AtomBuilder atLeast(int min) {
		return new AtomBuilder(min,null) ;
	}
	
	private AtomBuilder noMoreThan(int max) {
		return new AtomBuilder(null,max) ;
	}
	
	private AtomBuilder anyNumber() {
		return new AtomBuilder(null,null) ;
	}
	
	private AtomBuilder range(int min, int max) {
		return new AtomBuilder(min,max) ;
	}
	
	private static class AtomBuilder {
		private Integer m_min = null ; 
		private Integer m_max = null ; 
		public AtomBuilder( Integer min, Integer max ) { m_min = min ; m_max = max ; }
		
		public AtomBuilder butAtLeast(int min) { m_min = min ; return this ; }
		public AtomBuilder butNoMoreThan(int max) { m_max = max ; return this ; }
		
		public MatchPattern.Atom of( MatchPattern.MatchType matchType ) {
			return new MatchPattern.Atom(m_min, m_max, matchType ) ;
		}
		public MatchPattern.Atom literal( String value ) {
			return of( new MatchPattern.LiteralMatchType(value) ) ;
		}
		public MatchPattern.Atom ofCodes( PatternMatchCode ... codes ) {
			return of( new MatchPattern.CodeSetMatchType( Arrays.asList(codes) ) ) ;
		}
		public MatchPattern.Atom ofAny( MatchPattern.Atom ... atoms ) {
			return of( new MatchPattern.UnionMatchType( Arrays.asList( atoms ) ) ) ;
		}
	}
	
}
