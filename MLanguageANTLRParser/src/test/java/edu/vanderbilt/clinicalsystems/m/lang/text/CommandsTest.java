package edu.vanderbilt.clinicalsystems.m.lang.text;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.ParameterPassMethod;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Assignment;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Destination;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCallList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.IndirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.TagReference;

public class CommandsTest {

	@Test
	public void canParseTaggedRoutineCallCommand() throws IOException {
		final List<? extends Command> commands = new RoutineANTLRParser().parseCommandSequence( "DO MYTAG^MYROUTINE(\"abc\")" ) ; 
		assertThat( commands.size(), equalTo(1) );
		Command command = commands.get(0) ;
		assertThat( command.commandType(), equalTo( CommandType.DO ) );
		assertThat( command.argument(), instanceOf( TaggedRoutineCallList.class ) );
		assertThat( ((TaggedRoutineCallList)command.argument()).elements(), hasItem( new TaggedRoutineCall( new TagReference("MYTAG", "MYROUTINE"), Arrays.asList(Constant.from("abc"))) ) );
	}
	
	@Test
	public void canParseMergeCommand() throws IOException {
		final List<? extends Command> commands = new RoutineANTLRParser().parseCommandSequence( "MERGE @INDIRECT1=@INDIRECT2@(\"KEY1\",\"KEY2\")" ) ;
		assertThat( commands.size(), equalTo(1) );
		Command command = commands.get(0) ;
		assertThat( command.commandType(), equalTo( CommandType.MERGE) );
		assertThat( command.argument(), instanceOf( AssignmentList.class ) );
		assertThat( ((AssignmentList)command.argument()).elements(), hasItem(
				new Assignment(
						Destination.wrap( new IndirectVariableReference( new DirectVariableReference(Scope.TRANSIENT, "INDIRECT1") ) ),
						new IndirectVariableReference( new DirectVariableReference(ParameterPassMethod.BY_VALUE, Scope.TRANSIENT, "INDIRECT2"), Arrays.asList(Constant.from("KEY1"), Constant.from("KEY2")) )
					)
			) );
	}
	
}
