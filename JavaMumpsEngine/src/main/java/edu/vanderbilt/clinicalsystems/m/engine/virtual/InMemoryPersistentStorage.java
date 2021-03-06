package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import edu.vanderbilt.clinicalsystems.m.engine.EngineException;
import edu.vanderbilt.clinicalsystems.m.engine.ErrorCode;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinSystemVariable;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineLinearWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineNativeFormatter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineParser;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineParserFactory;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;


public class InMemoryPersistentStorage extends TreeNodeMap implements PersistentStorage {

	private RoutineNativeFormatter m_routineFormatter ;
	
	private final Map<String,CompiledRoutine> m_compiledRoutines = new HashMap<String, CompiledRoutine>(); 

	public InMemoryPersistentStorage() {
		
		m_routineFormatter = new RoutineNativeFormatter() ;
		m_routineFormatter.options().setCommandsPerLineLimit(1); 
		m_routineFormatter.options().setCommentsPerLineLimit(1); 
		m_routineFormatter.options().setNumberOfSpacesForBlockIndentation(1); 
		m_routineFormatter.options().setUseTabsForIndentation(false);
		m_routineFormatter.options().setNumberOfSpacesForIndentation(4);
		m_routineFormatter.options().setWriteAbbreviatedBuiltinFunctionSymbols(false);
		m_routineFormatter.options().setWriteAbbreviatedBuiltinVariableSymbols(false);
		m_routineFormatter.options().setWriteAbbreviatedCommandSymbols(false);
		
		insert( makeKey( BuiltinSystemVariable.ROUTINE ), new CompiledRoutinesByNameNode());
		
	}
	
	private class CompiledRoutinesByNameNode extends StandardNode {

		@Override protected Node createNode() {
			return new CompiledRoutineNode() ;
		}
	}
	
	private class CompiledRoutineNode extends StandardNode {
		@Override
		public void merge(NodeMap srcNodeMap) {
			dropAll();
			super.merge(srcNodeMap);
			StringBuilder sb = new StringBuilder();
			if ( !isEmpty() )
				for ( String key = firstKey() ; key != null ; key = keyFollowing(key) ) {
					sb.append( at(key).value() ) ;
					sb.append( "\n" ) ;
				}
			try {
				Routine routine = interpretRoutine( sb.toString() ) ;
				CompiledNativeRoutine compiledRoutine = new CompiledNativeRoutine( routine );
				m_compiledRoutines.put( routine.name(), compiledRoutine ) ;
				get( Integer.toString(0) ).assign( compiledRoutine.compiledRepresentation() );
			} catch (EngineException ex) { throw new RuntimeException(ex); }
		}
	}

	@Override public CompiledRoutine compiledRoutine(String routineName) {
		return m_compiledRoutines.get( routineName );
	}

	@Override
	public Routine interpretRoutine( String unparsedCode ) throws EngineException {
		ServiceLoader<RoutineParserFactory> serviceLoader = ServiceLoader.load( RoutineParserFactory.class ) ;
		RoutineParserFactory routineParserFactory = serviceLoader.iterator().next() ;
		RoutineParser routineParser = routineParserFactory.createRoutineParser() ;
		Reader reader = new StringReader(unparsedCode);
		
		try {
			return routineParser.parse( reader );
		} catch (IOException e) {
			throw new EngineException(ErrorCode.CANNOT_PARSE,"code",unparsedCode);
		}
	}

	private String makeKey(BuiltinSystemVariable builtinSystemVariable) {
		return builtinSystemVariable.canonicalSymbol() ;
	}

	@Override
	public Node get(BuiltinSystemVariable builtinSystemVariable) { return get( makeKey(builtinSystemVariable) ); }
	@Override
	public Node  at(BuiltinSystemVariable builtinSystemVariable) { return  at( makeKey(builtinSystemVariable) ); }
	
	@Override
	public void install( Routine routine, TargetInstanceResolver targetInstanceResolver ) throws RoutineWriterException {
		CompiledRoutine compiledRoutine = new CompiledNativeRoutine(routine, targetInstanceResolver);
		m_compiledRoutines.put( routine.name(), compiledRoutine ) ;
		
		Node routinesNode = get( BuiltinSystemVariable.ROUTINE ) ;
		Node routineNode = routinesNode.get( routine.name() ) ;
		
		StringWriter sw = new StringWriter() ;
		try {
			routine.write( new RoutineLinearWriter( sw, m_routineFormatter) ) ;
		} catch ( Throwable ex ) {
			/* just stop writing */
		}
		String[] lines = sw.toString().split("\\r*\\n") ;
		
		routineNode.dropAll() ;
		routineNode.get( Integer.toString(0) ).assign( compiledRoutine.compiledRepresentation() );
		if ( null != lines ) {
			for ( int i = 0 ; i < lines.length ; ++i )
				routineNode.get( Integer.toString(i+1) ).assign( lines[i] );
		}
	}
	
	@Override
	public void install( Class<?> type, TargetInstanceResolver targetInstanceResolver, String routineName ) throws RoutineWriterException {
		CompiledRoutine compiledRoutine = new CompiledJavaClassRoutine( type, targetInstanceResolver, routineName );
		m_compiledRoutines.put( routineName, compiledRoutine ) ;

		Node routinesNode = get( BuiltinSystemVariable.ROUTINE ) ;
		Node routineNode = routinesNode.get( routineName ) ;
		routineNode.dropAll() ;
		routineNode.get( Integer.toString(0) ).assign( compiledRoutine.compiledRepresentation() );
	}
	
	@Override
	public CompiledRoutine lookup( String routineName ) {
		return m_compiledRoutines.get( routineName ) ;
	}

}
