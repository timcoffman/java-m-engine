package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.Generator.Listener;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.MultilineBlock;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;

public class PostConditionalLoopAssembler extends FlowAssembler<Ast.DoWhileLoop> {

	protected PostConditionalLoopAssembler(RoutineTools builderTools) {
		super(builderTools);
	}

	@Override
	public void assemble(Ast.DoWhileLoop doWhileLoop, Block block, Listener delegate) {
		
		Block bodyBlock = tools().blocks().generate( doWhileLoop.statement(), delegate ) ;
		
		Block conditionBlock = new MultilineBlock() ;
		try ( BlockManager conditionBlockManager = new BlockManager(conditionBlock, delegate) ) {
			Expression condition = tools().expressions().generate(doWhileLoop.condition(), conditionBlockManager) ;
			conditionBlockManager.prependElement( new Command(condition.inverted(), CommandType.QUIT, Argument.NOTHING ));
		}
		
		bodyBlock.appendElements(conditionBlock);

		Block inlineBlock = wrapInsideInlineBlock( bodyBlock ) ;
		
		block.appendElement( new Command( CommandType.FOR, Argument.NOTHING, inlineBlock ) ) ;
		
	}

}
