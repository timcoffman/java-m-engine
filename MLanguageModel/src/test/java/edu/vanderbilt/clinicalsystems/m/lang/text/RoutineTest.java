package edu.vanderbilt.clinicalsystems.m.lang.text;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.StringWriter;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.Comment;
import edu.vanderbilt.clinicalsystems.m.lang.model.ParameterName;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.Tag;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Assignment;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.DeclarationList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Destination;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCallList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.FunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.TagReference;

public class RoutineTest {

	private RoutineNativeImmediateFormatter m_routineFormatter; 
	private RoutineWriter m_routineWriter;
	private StringBuffer m_buffer; 
	
	@Before
	public void configure() {
		StringWriter writer = new StringWriter() ;
		m_buffer = writer.getBuffer() ;
		m_routineFormatter = new RoutineNativeImmediateFormatter();
		m_routineFormatter.options().setUseTabsForIndentation( true );
		m_routineFormatter.options().setNumberOfSpacesForBlockIndentation(1);
		m_routineFormatter.options().setWriteAbbreviatedCommandSymbols( true );
		m_routineFormatter.options().setWriteAbbreviatedBuiltinFunctionSymbols( true );
		m_routineWriter = new RoutineLinearWriter(writer, m_routineFormatter) ;
	}
	
	@Test
	public void canWriteEmptyRoutine() throws RoutineWriterException {
		Routine routine = new Routine() ;
		routine.appendElement( new Tag("MYROUTINE") );
		m_routineWriter.write(routine);
		assertThat( m_buffer.toString(), equalTo("MYROUTINE\n") );
	}

	@Test
	public void canWriteEmptyRoutineWithComments() throws RoutineWriterException {
		Routine routine = new Routine() ;
		routine.appendElement( new Tag("MYROUTINE") );
		routine.appendElement( new Comment("lorem") );
		routine.appendElement( new Comment("ipsum") );
		m_routineWriter.write(routine);
		assertThat( m_buffer.toString(), equalTo("MYROUTINE;lorem ipsum\n\n") );
	}
	
	@Test
	public void canWriteEmptyRoutineWithCommentsOnMultipleLines() throws RoutineWriterException {
		Routine routine = new Routine() ;
		routine.appendElement( new Tag("MYROUTINE") );
		routine.appendElement( new Comment("lorem") );
		routine.appendElement( new Comment("ipsum") );
		m_routineFormatter.options().setCommentsPerLineLimit(1);
		m_routineWriter.write(routine);
		assertThat( m_buffer.toString(), equalTo("MYROUTINE;lorem\n\t;ipsum\n\n") );
	}

	@Test
	public void canWritePlainRoutine() throws RoutineWriterException {
		Routine routine = new Routine() ;
		routine.appendElement( new Tag("MYROUTINE") );
		routine.appendElement( new Command( CommandType.QUIT ) );
		m_routineWriter.write(routine);
		assertThat( m_buffer.toString(), equalTo("MYROUTINE\tQ \n\n") );
	}

	@Test
	public void canWriteMulitpleCommandsInRoutine() throws RoutineWriterException {
		Routine routine = new Routine() ;
		routine.appendElement( new Tag("MYROUTINE") );
		routine.appendElement( new Command( CommandType.WRITE, new ExpressionList( Constant.from("lorem")) ) );
		routine.appendElement( new Command( CommandType.WRITE, new ExpressionList( Constant.from("ipsum")) ) );
		routine.appendElement( new Command( CommandType.WRITE, new ExpressionList( Constant.from("dolor")) ) );
		m_routineWriter.write(routine);
		assertThat( m_buffer.toString(), equalTo("MYROUTINE\tW \"lorem\" W \"ipsum\" W \"dolor\"\n\n") );
	}
	
	@Test
	public void canWriteMulitpleVariableDeclarationsInRoutine() throws RoutineWriterException {
		Routine routine = new Routine() ;
		routine.appendElement( new Tag("MYROUTINE") );
		routine.appendElement( new Command( CommandType.NEW, new DeclarationList( new DirectVariableReference(Scope.LOCAL, "a")) ) );
		routine.appendElement( new Command( CommandType.WRITE, new ExpressionList( Constant.from("lorem")) ) );
		routine.appendElement( new Command( CommandType.NEW, new DeclarationList( new DirectVariableReference(Scope.LOCAL, "b")) ) );
		routine.appendElement( new Command( CommandType.NEW, new DeclarationList( new DirectVariableReference(Scope.LOCAL, "c")) ) );
		m_routineWriter.write(routine);
		assertThat( m_buffer.toString(), equalTo("MYROUTINE\tN a W \"lorem\" N b,c\n\n") );
	}

	@Test
	public void canWriteMulitpleCommandsInRoutineOnMultipleLines() throws RoutineWriterException {
		Routine routine = new Routine() ;
		routine.appendElement( new Tag("MYROUTINE") );
		routine.appendElement( new Command( CommandType.WRITE, new ExpressionList( Constant.from("lorem")) ) );
		routine.appendElement( new Command( CommandType.WRITE, new ExpressionList( Constant.from("ipsum")) ) );
		routine.appendElement( new Command( CommandType.WRITE, new ExpressionList( Constant.from("dolor")) ) );
		m_routineFormatter.options().setCommandsPerLineLimit(1);
		m_routineWriter.write(routine);
		assertThat( m_buffer.toString(), equalTo("MYROUTINE\tW \"lorem\"\n\tW \"ipsum\"\n\tW \"dolor\"\n\n") );
	}

	@Test
	public void canWriteSimpleRoutine() throws RoutineWriterException {
		Routine routine = new Routine() ;
		routine.appendElement( new Tag("MYROUTINE") );
		routine.appendElement( new Command( CommandType.NEW, new DeclarationList( new DirectVariableReference( Scope.LOCAL, "message" ) ) ) );
		routine.appendElement( new Command( CommandType.SET, new AssignmentList( new Assignment( Destination.wrap( new DirectVariableReference( Scope.LOCAL, "message" ) ), Constant.from("Hello, world!") ) ) ) );
		routine.appendElement( new Command( CommandType.WRITE, new ExpressionList( new DirectVariableReference( Scope.LOCAL, "message" ) ) ) );
		routine.appendElement( new Command( CommandType.QUIT ) );
		m_routineWriter.write(routine);
		assertThat( m_buffer.toString(), equalTo("MYROUTINE\tN message S message=\"Hello, world!\" W message Q \n\n") );
	}

	@Test
	public void canWriteTagsInRoutine() throws RoutineWriterException {
		Routine routine = new Routine() ;
		routine.appendElement( new Tag("MYROUTINE") );
		routine.appendElement( new Tag("FIRSTTAG") );
		routine.appendElement( new Tag("SECONDTAG") );
		m_routineWriter.write(routine);
		assertThat( m_buffer.toString(), equalTo("MYROUTINE\nFIRSTTAG\nSECONDTAG\n") );
	}
	
	@Test
	public void canWriteTagsWithContentsInRoutine() throws RoutineWriterException {
		Routine routine = new Routine() ;
		routine.appendElement( new Tag("MYROUTINE") );
		routine.appendElement( new Command( CommandType.QUIT ) );
		routine.appendElement( new Tag("FIRSTTAG") );
		routine.appendElement( new Command( CommandType.QUIT ) );
		routine.appendElement( new Tag("SECONDTAG") );
		routine.appendElement( new Command( CommandType.QUIT ) );
		m_routineWriter.write(routine);
		assertThat( m_buffer.toString(), equalTo("MYROUTINE\tQ \nFIRSTTAG\tQ \nSECONDTAG\tQ \n\n") );
	}
	
	@Test
	public void canWriteTagsWithParametersInRoutine() throws RoutineWriterException {
		Routine routine = new Routine() ;
		routine.appendElement( new Tag("MYROUTINE") );
		routine.appendElement( new Tag( "MYFUNCTION", Arrays.asList( new ParameterName("x"), new ParameterName("y") )) );
		m_routineWriter.write(routine);
		assertThat( m_buffer.toString(), equalTo("MYROUTINE\nMYFUNCTION(x,y)\n") );
	}

	@Test
	public void canWriteMultiplyTaggedRoutine() throws RoutineWriterException {
		Routine routine = new Routine() ;
		routine.appendElement( new Tag("MYROUTINE") );
		routine.appendElement( new Command( CommandType.NEW, new DeclarationList( new DirectVariableReference( Scope.LOCAL, "abc" ) ) ) );
		routine.appendElement( new Command( CommandType.WRITE, new ExpressionList( new RoutineFunctionCall( new TagReference("MYFUNCTION", "MYROUTINE" ), FunctionCall.Returning.UNKNOWN, Expression.list( Constant.from("123"), Constant.from("456") ) )) ) );
		routine.appendElement( new Command( CommandType.DO, new TaggedRoutineCallList( new TaggedRoutineCall( new TagReference( "MYFUNCTION", "MYROUTINE"), Expression.list( Constant.from("123"), Constant.from("456") ) ) ) ) );
		routine.appendElement( new Command( CommandType.QUIT ) );
		routine.appendElement( new Tag( "MYFUNCTION", Arrays.asList( new ParameterName("x"), new ParameterName("y") )) );
		routine.appendElement( new Command( CommandType.NEW, new DeclarationList( new DirectVariableReference( Scope.LOCAL, "abc" ) ) ) );
		routine.appendElement( new Command( CommandType.QUIT ) );
		m_routineWriter.write(routine);
		assertThat( m_buffer.toString(), equalTo("MYROUTINE\tN abc W $$MYFUNCTION^MYROUTINE(123,456) D MYFUNCTION^MYROUTINE(123,456) Q \nMYFUNCTION(x,y)\tN abc Q \n\n") );
	}
}
