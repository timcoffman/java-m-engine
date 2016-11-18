package edu.vanderbilt.clinicalsystems.m.lang.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.DeclarationList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;

public abstract class Block implements RoutineElement {

	private final List<RoutineElement> m_elements = new ArrayList<RoutineElement>();
	
	
	public Block() {
		/* empty block */
	}
	
	public Block( Collection<RoutineElement> elements ) {
		m_elements.addAll( elements ) ;
	}
	
	public void prependElement( RoutineElement element ) { m_elements.add(0,element) ; postProcess() ; }
	public void prependElements( Block block ) { int index = 0 ; for ( RoutineElement element : block.elements() ) m_elements.add( index++, element ) ; postProcess() ; }
	
	public void appendElement( RoutineElement element ) { m_elements.add(element) ; postProcess() ; }
	public void appendElements( Block block ) { for ( RoutineElement element : block.elements() ) m_elements.add( element ) ; postProcess() ; }

	public Iterable<RoutineElement> elements() { return m_elements ; }

	private final Map<String,Integer> m_tagPositions = new HashMap<String, Integer>() ;
	private void clearTagPositionCache() { m_tagPositions.clear(); }
	
	public Iterator<RoutineElement> findTagByName( String name ) {
		Integer position = m_tagPositions.get( name ) ;
		if ( null == position ) {
			position = 0 ; 
			while ( position < m_elements.size() ) {
				RoutineElement element = m_elements.get( position ) ;
				if ( element instanceof Tag && name.equalsIgnoreCase( ((Tag)element).name() ) ) {
					m_tagPositions.put( name, position ) ;
					break ;
				}
				++position ;
			}
		}
		return m_elements.listIterator(position) ;
	}

	public boolean isEmpty() { return m_elements.isEmpty() ; }
	
	@Override
	public String toString() { return "{ ... " + m_elements.size() + " ... }"; }

	private void postProcess() {
		clearTagPositionCache() ;
		removeDeadCommands() ;
		mergeAdjacentDeclarations() ;
	}

	private void removeDeadCommands() {
		ListIterator<RoutineElement> i = m_elements.listIterator() ;
		while ( i.hasNext() ) {
			RoutineElement element = i.next();
			if ( element instanceof Command ) {
				Command command = (Command)element;
				Expression condition = command.condition() ;
				if ( null != condition ) {
					Expression simplifiedExpression = condition.simplified();
					if ( simplifiedExpression instanceof Constant ) {
						Constant value = (Constant)simplifiedExpression ;
						if ( value.toBoolean() ) {
							/* always true, replace it with a command with no condition */
							i.remove();
							i.add( new Command(null, command.commandType(), command.argument(), command.block() ) );
						} else {
							/* always false, remove it */
							i.remove();
						}
					}
				}
			}
		}
	}
	
	private void mergeAdjacentDeclarations() {
		DeclarationList precedingDeclarationList = null ;
		Iterator<RoutineElement> i = m_elements.iterator() ;
		while ( i.hasNext() ) {
			RoutineElement element = i.next();
			if ( element instanceof Command && ((Command)element).commandType() == CommandType.NEW ) {
				DeclarationList declarationList = (DeclarationList)(((Command)element)).argument();
				if ( null != precedingDeclarationList ) {
					precedingDeclarationList.append( declarationList );
					i.remove() ;
				} else {
					precedingDeclarationList = declarationList ; 
				}
			} else if ( element instanceof Comment ) {
				/* can be safely ignored */
			} else {
				precedingDeclarationList = null ;
			}
		}
	}
	
}
