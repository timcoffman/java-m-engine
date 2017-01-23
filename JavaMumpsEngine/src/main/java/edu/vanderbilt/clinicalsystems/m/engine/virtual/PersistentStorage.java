package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinSystemVariable;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;


public interface PersistentStorage extends NodeMap, Installer {

	Routine interpretRoutine( String unparsedCode ) throws EngineException ;
	
	Node get(BuiltinSystemVariable builtinSystemVariable) ;
	Node  at(BuiltinSystemVariable builtinSystemVariable) ;
	
	CompiledRoutine compiledRoutine(String routineName) ;
}
