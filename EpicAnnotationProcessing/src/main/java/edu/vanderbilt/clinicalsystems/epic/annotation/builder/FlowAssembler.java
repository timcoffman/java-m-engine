package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import java.util.Iterator;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.InlineBlock;
import edu.vanderbilt.clinicalsystems.m.lang.model.MultilineBlock;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;

public abstract class FlowAssembler<T> extends Assembler<T> {

	public FlowAssembler(RoutineTools builderTools) { super(builderTools); }

	protected Block wrapInsideInlineBlock(Block block) {
		/*
		 * if block contains no CommandType.QUIT,
		 *   wrap it in a CommandType.Do in an InlineBlock
		 * if block contains some CommandType.QUITs,
		 *   extract all Commands up to and including the QUITs
		 *   and wrap the remaining elements in a CommandType.Do in an InlineBlock 
		 */
		Iterator<RoutineElement> i ;
		
		RoutineElement lastElementOnlyAllowedInline = null ;
		i = block.elements().iterator() ;
		while ( i.hasNext() ) {
			RoutineElement element = i.next();
			if ( element instanceof Command && ((Command)element).commandType() == CommandType.QUIT )
				lastElementOnlyAllowedInline = element ;
		}
		
		Block inlineBlock = new InlineBlock() ;
		i = block.elements().iterator() ;
		while ( i.hasNext() ) {
			RoutineElement element = i.next();
			inlineBlock.appendElement(element);
			if ( element == lastElementOnlyAllowedInline ) {
				
				if ( i.hasNext() ) {
					MultilineBlock remainingElements = new MultilineBlock() ;
					while ( i.hasNext() )
						remainingElements.appendElement( i.next() );
					
					inlineBlock.appendElement( new Command( CommandType.DO, Argument.NOTHING, remainingElements ));
				}
			}
		}
		
		return inlineBlock ;
	}

}