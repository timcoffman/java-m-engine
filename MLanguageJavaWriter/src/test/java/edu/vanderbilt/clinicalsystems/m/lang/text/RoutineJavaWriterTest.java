package edu.vanderbilt.clinicalsystems.m.lang.text;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.junit.Test;

import com.sun.codemodel.writer.SingleStreamCodeWriter;

import edu.vanderbilt.clinicalsystems.m.Core;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.Comment;
import edu.vanderbilt.clinicalsystems.m.lang.model.ParameterName;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.Tag;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;

public class RoutineJavaWriterTest {

	@Test
	public void canWriteClass() throws Exception {

		Routine routine = new Routine();
		routine.appendElement(new Tag("MyRoutine"));
		routine.appendElement(new Comment("my routine"));
		routine.appendElement(new Command(CommandType.QUIT, Argument.NOTHING));
		routine.appendElement(new Tag("MyMethod", Arrays
				.asList(new ParameterName("x"))));
		routine.appendElement(new Comment("my method"));
		routine.appendElement(new Command(CommandType.QUIT, new ExpressionList(
				new DirectVariableReference(Scope.TRANSIENT, "x"))));

		RoutineJavaUnitBuilder routineBuilder = new RoutineJavaUnitBuilder();
		Core.useLibrariesIn(routineBuilder.env());

		routineBuilder.build(
				RoutineJavaWriterTest.class.getPackage().getName(), routine);
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		routineBuilder.codeModel().build(new SingleStreamCodeWriter(s));

		System.out.println(s.toString("UTF-8"));
		assertThat(s.toString("UTF-8"), not(equalTo("")));
	}

}
