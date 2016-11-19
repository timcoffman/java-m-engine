package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import edu.vanderbilt.clinicalsystems.m.lang.BuiltinSystemVariable;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineLinearWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineNativeFormatter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class Database extends TreeNodeMap implements Installer {

	private RoutineNativeFormatter m_routineFormatter ;
	
	private final Map<String,Routine> m_compiledRoutines = new HashMap<String, Routine>(); 

	private GlobalContext m_globalContext = new GlobalContext() {

		@Override public Routine compiledRoutine(String routineName) {
			return m_compiledRoutines.get( routineName );
		}
		
	} ;
	
	public Database() {
		m_routineFormatter = new RoutineNativeFormatter() ;
		m_routineFormatter.options().setCommandsPerLineLimit(1); 
		m_routineFormatter.options().setCommentsPerLineLimit(1); 
		m_routineFormatter.options().setNumberOfSpacesForBlockIndentation(1); 
		m_routineFormatter.options().setUseTabsForIndentation(false);
		m_routineFormatter.options().setNumberOfSpacesForIndentation(4);
		m_routineFormatter.options().setWriteAbbreviatedBuiltinFunctionSymbols(false);
		m_routineFormatter.options().setWriteAbbreviatedBuiltinVariableSymbols(false);
		m_routineFormatter.options().setWriteAbbreviatedCommandSymbols(false);
	}

	private String makeKey(BuiltinSystemVariable builtinSystemVariable) {
		return builtinSystemVariable.canonicalSymbol() ;
	}
	
	public Node get(BuiltinSystemVariable builtinSystemVariable) { return get( makeKey(builtinSystemVariable) ); }
	public Node  at(BuiltinSystemVariable builtinSystemVariable) { return  at( makeKey(builtinSystemVariable) ); }
	
	@Override
	public void install( Routine routine ) throws RoutineWriterException {
		m_compiledRoutines.put( routine.name(), routine ) ;
		
		Node routinesNode = get( BuiltinSystemVariable.ROUTINE ) ;
		Node routineNode = routinesNode.get( routine.name() ) ;
		
		StringWriter sw = new StringWriter() ;
		routine.write( new RoutineLinearWriter( sw, m_routineFormatter) ) ;
		String[] lines = sw.toString().split("\\r*\\n") ;
		
		routineNode.dropAll() ;
		routineNode.get( Integer.toString(0) ).assign( routine.name() + "<" + routine.hashCode() + ">" );
		if ( null != lines ) {
			for ( int i = 0 ; i < lines.length ; ++i )
				routineNode.get( Integer.toString(i+1) ).assign( lines[i] );
		}
	}

	public Connection openConnection() {
		return new Connection(this, m_globalContext) ;
	}
	
	public Connection openConnection( InputOutputDevice inputOutputDevice ) {
		return new Connection(this, m_globalContext, inputOutputDevice) ;
	}
}
