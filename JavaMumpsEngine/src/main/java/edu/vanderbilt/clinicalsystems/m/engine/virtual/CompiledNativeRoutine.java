package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import edu.vanderbilt.clinicalsystems.m.engine.virtual.Executor.ExecutionResult;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.ParameterName;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.Tag;

class CompiledNativeRoutine implements CompiledRoutine {

	private final Routine m_routine;

	public CompiledNativeRoutine(Routine routine) {
		m_routine = routine ;
	}
	
	@Override
	public String name() {
		return m_routine.name() ;
	}

	@Override public String compiledRepresentation() {
		return m_routine.name() + "<" + m_routine.hashCode() + ">";
	}

	@Override
	public CompiledTag compiledTag(String tagName, int parameterCount) {
		if ( null == tagName ) {
			return new CompiledNativeTag((Tag)m_routine.root().elements().iterator().next());
		} else {
			Iterator<RoutineElement> elementIterator = m_routine.findTagByName(tagName);
			if ( elementIterator.hasNext() )
				return new CompiledNativeTag((Tag)elementIterator.next());
			else
				return null ;
		}
	}

	private class CompiledNativeTag implements CompiledTag {

		private final String m_tagName;
		private final List<String> m_parameterNames;

		public CompiledNativeTag(Tag tag) {
			m_tagName = tag.name() ;
			m_parameterNames = StreamSupport.stream(tag.parameterNames().spliterator(),false).map(ParameterName::name).collect(Collectors.toList()) ;
		}
		
		@Override
		public CompiledRoutine compiledRoutine() {
			return CompiledNativeRoutine.this ;
		}
		
		@Override
		public String name() {
			return m_tagName ;
		}

		@Override
		public List<String> parameterNames() {
			return Collections.unmodifiableList(m_parameterNames) ;
		}
		
		@Override
		public ExecutionResult execute( ExecutionFrame frame, List<EvaluationResult> arguments ) {
			Iterator<RoutineElement> elementIterator = m_routine.findTagByName(m_tagName) ;

			Iterator<EvaluationResult> argumentIterator = arguments.iterator() ;
			for ( String parameterName : m_parameterNames ) {
				Node localParameterNode = frame.createLocalNode( parameterName ) ;
				if ( argumentIterator.hasNext() )
					localParameterNode.assign( argumentIterator.next().toConstant().value() ) ;
			}
			
			ExecutionResult result = ExecutionResult.CONTINUE ;
			while ( ExecutionResult.CONTINUE == result && elementIterator.hasNext() ) {
				RoutineElement element = elementIterator.next() ;
				if ( element instanceof Command ) {
					result = frame.execute( (Command)element ) ;
				}
			}
			
			return result ;
		}
		
	}
	
}