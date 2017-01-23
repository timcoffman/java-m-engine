package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import edu.vanderbilt.clinicalsystems.m.engine.virtual.node.builtin.BuiltinHorologNode;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinSystemVariable;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinVariable;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class Database extends TreeNodeMap implements Installer {

	private final PersistentStorage m_persistentStorage ;
	
	private GlobalContext m_globalContext = new GlobalContext() {

		@Override public CompiledRoutine compiledRoutine(String routineName) {
			return m_persistentStorage.compiledRoutine( routineName );
		}

		@Override
		public NodeMap root( Scope scope ) {
			switch ( scope ) {
			case PERSISTENT:
				return m_persistentStorage ;
			case TRANSIENT:
			default:
				return Database.this ;
			}	
		}
		
	} ;
	public Database() {
		this( new InMemoryPersistentStorage() ) ;
	} ;
	public Database( PersistentStorage persistentStorage ) {
		m_persistentStorage = persistentStorage ;
		
		insert( makeKey( BuiltinVariable.HOROLOG ), new BuiltinHorologNode() );
	}
	
	private String makeKey(BuiltinVariable builtinVariable) {
		return builtinVariable.canonicalSymbol() ;
	}
	
	public PersistentStorage persistentStorage() {
		return m_persistentStorage;
	}
	
	public Node get(BuiltinSystemVariable builtinSystemVariable) { return m_persistentStorage.get(builtinSystemVariable); }
	public Node  at(BuiltinSystemVariable builtinSystemVariable) { return m_persistentStorage. at(builtinSystemVariable); }
	
	@Override
	public void install( Routine routine, TargetInstanceResolver targetInstanceResolver ) throws RoutineWriterException {
		m_persistentStorage.install( routine, targetInstanceResolver ) ;
	}
	
	@Override
	public void install( Class<?> type, TargetInstanceResolver targetInstanceResolver, String routineName ) throws RoutineWriterException {
		m_persistentStorage.install( type, targetInstanceResolver, routineName ) ;
	}
	
	@Override
	public CompiledRoutine lookup( String routineName ) {
		return m_persistentStorage.lookup( routineName ) ;
	}

	public Connection openConnection() {
		return new Connection(this, m_globalContext ) ;
	}
	
	public Connection openConnection( InputOutputDevice inputOutputDevice ) {
		return new Connection(m_globalContext, inputOutputDevice) ;
	}
}
