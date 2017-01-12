package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;

public interface Evaluator {

	EvaluationResult evaluate( Expression expression ) throws EngineException ;

}
