package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.MatchPattern;

public class PatternMatcher {

	private final MatchPattern m_matchPattern ;

	public PatternMatcher(MatchPattern matchPattern) {
		m_matchPattern  = matchPattern ;
	}

	public boolean matches(Constant text) {
		return false;
	}

}
