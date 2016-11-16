package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.MultilineBlock;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;

public class ConditionalLoopAssembler extends FlowAssembler<Ast.WhileLoop> {

	protected ConditionalLoopAssembler(RoutineTools builderTools) {
		super(builderTools);
	}

	@Override
	public void assemble(Ast.WhileLoop whileLoopNode, Block block) {
		
		Block bodyBlock = tools().blocks().generate( whileLoopNode.statement(), null ) ;
		
		Block conditionBlock = new MultilineBlock() ;
		try ( BlockManager conditionBlockManager = new BlockManager(conditionBlock) ) {
			Expression condition = tools().expressions().generate(whileLoopNode.condition(), conditionBlockManager) ;
			conditionBlockManager.prependElement( new Command(condition.inverted(), CommandType.QUIT, Argument.NOTHING ));
		}
		
		bodyBlock.prependElements(conditionBlock);

		Block inlineBlock = wrapInsideInlineBlock( bodyBlock ) ;
		
		block.appendElement( new Command( CommandType.FOR, Argument.NOTHING, inlineBlock ) ) ;

	}

}
