package edu.vanderbilt.clinicalsystems.m.lang.text;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.InlineBlock;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.LoopDefinition;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;

public class LoopDefinitionParserTest {

	@Test
	public void canParseLoopDefinitionWithStartExpression() {
		List<? extends Command> commands = new RoutineANTLRParser().parseCommandSequence("FOR i=1") ;
		assertThat( commands, hasItem(
				equalTo(
					new Command( CommandType.FOR,
							new LoopDefinition(
								new DirectVariableReference(Scope.TRANSIENT, "i"),
								Constant.from(1),
								null,
								null
							),
							new InlineBlock()
					)
				)
			)) ;
	}
	
	@Test
	public void canParseLoopDefinitionWithStartAndStepExpressions() {
		List<? extends Command> commands = new RoutineANTLRParser().parseCommandSequence("FOR i=1:1") ;
		assertThat( commands, hasItem(
				equalTo(
						new Command( CommandType.FOR,
								new LoopDefinition(
										new DirectVariableReference(Scope.TRANSIENT, "i"),
										Constant.from(1),
										Constant.from(1),
										null
										),
										new InlineBlock()
								)
						)
				)) ;
	}
	
	@Test
	public void canParseLoopDefinitionWithStartAndStepAndStopExpressions() {
		List<? extends Command> commands = new RoutineANTLRParser().parseCommandSequence("FOR i=1:1:10") ;
		assertThat( commands, hasItem(
				equalTo(
						new Command( CommandType.FOR,
								new LoopDefinition(
										new DirectVariableReference(Scope.TRANSIENT, "i"),
										Constant.from(1),
										Constant.from(1),
										Constant.from(10)
										),
										new InlineBlock()
								)
						)
				)) ;
	}
	
}
