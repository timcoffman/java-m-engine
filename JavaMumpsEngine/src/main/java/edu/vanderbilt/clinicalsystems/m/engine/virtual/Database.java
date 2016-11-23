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

public class Database extends TreeNodeMap implements Installer {

	private RoutineNativeFormatter m_routineFormatter ;
	
	private final Map<String,Routine> m_compiledRoutines = new HashMap<String, Routine>(); 

	private GlobalContext m_globalContext = new GlobalContext() {

		@Override public Routine compiledRoutine(String routineName) {
			return m_compiledRoutines.get( routineName );
		}

		@Override
		public NodeMap root() {
			return Database.this ;
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
		
		insert( makeKey( BuiltinSystemVariable.ROUTINE ), new StandardNode() {

			@Override protected Node createNode() {
				/* should us a delegate here */
				return new StandardNode() {
			
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
							m_compiledRoutines.put( routine.name(), routine ) ;
							get( Integer.toString(0) ).assign( compiledRoutineValue(routine) );
						} catch (EngineException ex) { throw new RuntimeException(ex); }
					}
		
		//			@Override
		//			public Node create(String key) { throw new UnsupportedOperationException("system variable " + BuiltinSystemVariable.ROUTINE.canonicalSymbol() + " does not support creating sub-variables") ; }
		//
		//			@Override
		//			public void insert(String key, Node node) { throw new UnsupportedOperationException("system variable " + BuiltinSystemVariable.ROUTINE.canonicalSymbol() + " does not support inserting sub-variables") ; }
				} ;
			}
		});
	}

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
	
	public Node get(BuiltinSystemVariable builtinSystemVariable) { return get( makeKey(builtinSystemVariable) ); }
	public Node  at(BuiltinSystemVariable builtinSystemVariable) { return  at( makeKey(builtinSystemVariable) ); }
	
	private String compiledRoutineValue( Routine routine ) {
		return routine.name() + "<" + routine.hashCode() + ">";
	}
	
	@Override
	public void install( Routine routine ) throws RoutineWriterException {
		m_compiledRoutines.put( routine.name(), routine ) ;
		
		Node routinesNode = get( BuiltinSystemVariable.ROUTINE ) ;
		Node routineNode = routinesNode.get( routine.name() ) ;
		
		StringWriter sw = new StringWriter() ;
		routine.write( new RoutineLinearWriter( sw, m_routineFormatter) ) ;
		String[] lines = sw.toString().split("\\r*\\n") ;
		
		routineNode.dropAll() ;
		routineNode.get( Integer.toString(0) ).assign( compiledRoutineValue(routine) );
		if ( null != lines ) {
			for ( int i = 0 ; i < lines.length ; ++i )
				routineNode.get( Integer.toString(i+1) ).assign( lines[i] );
		}
	}
	
	@Override
	public Routine lookup( String routineName ) {
		return m_compiledRoutines.get( routineName ) ;
	}

	public Connection openConnection() {
		return new Connection(this, m_globalContext ) ;
	}
	
	public Connection openConnection( InputOutputDevice inputOutputDevice ) {
		return new Connection(this, m_globalContext, inputOutputDevice) ;
	}
}
