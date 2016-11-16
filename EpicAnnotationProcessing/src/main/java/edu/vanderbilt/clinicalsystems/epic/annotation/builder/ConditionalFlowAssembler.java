package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import java.util.Iterator;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;

public class ConditionalFlowAssembler extends FlowAssembler<Ast.If>{
	
	public ConditionalFlowAssembler( RoutineTools routineTools ) { super(routineTools) ; }
	
	@Override
	public void assemble(Ast.If ifNode, Block block) {
		try ( BlockManager blockManager = new BlockManager(block) ) {
			Expression condition = tools().expressions().generate( ifNode.condition(), blockManager ) ;
			Block thenBlock = tools().blocks().generate( ifNode.thenStatement(), null ) ;
			
			Command onlyThenCommand = consistsOfSingleCommand(thenBlock) ;
			if ( null != onlyThenCommand && null == ifNode.elseStatement() ) {
				blockManager.appendElement( new Command( condition, onlyThenCommand.commandType(), onlyThenCommand.argument(), onlyThenCommand.block() ) ) ;
			} else {
				Block inlineBlock = wrapInsideInlineBlock( thenBlock ) ;
				blockManager.appendElement( new Command( CommandType.IF, new ExpressionList(condition), inlineBlock ) ) ;
			}
		}
			
		if ( null != ifNode.elseStatement() ) {
			try ( BlockManager blockManager = new BlockManager(block) ) {
				Block elseBlock = tools().blocks().generate( ifNode.elseStatement(), null ) ;
				Block inlineBlock = wrapInsideInlineBlock( elseBlock ) ;
				blockManager.appendElement( new Command( CommandType.ELSE, Argument.NOTHING, inlineBlock ) ) ;
			}
		}
	}

	private static Command consistsOfSingleCommand( Block block ) {
		Iterator<RoutineElement> thenIter = block.elements().iterator() ;
		if ( thenIter.hasNext() ) { // has at least one element
			RoutineElement onlyRoutineElement = thenIter.next() ;
			if ( !thenIter.hasNext() ) { // has no more than one element
				if ( onlyRoutineElement instanceof Command ) // only element is a Command
					return (Command)onlyRoutineElement ;
			}
		}
		return null ;
	}
}
