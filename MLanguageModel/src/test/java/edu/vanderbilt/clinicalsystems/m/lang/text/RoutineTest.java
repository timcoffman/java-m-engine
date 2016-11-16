package edu.vanderbilt.clinicalsystems.m.lang.text;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.StringWriter;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle;
import edu.vanderbilt.clinicalsystems.m.lang.RoutineAccess;
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
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.FunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.TagReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;

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
		routine.appendElement( new Command( CommandType.WRITE, new ExpressionList( new Constant("lorem")) ) );
		routine.appendElement( new Command( CommandType.WRITE, new ExpressionList( new Constant("ipsum")) ) );
		routine.appendElement( new Command( CommandType.WRITE, new ExpressionList( new Constant("dolor")) ) );
		m_routineWriter.write(routine);
		assertThat( m_buffer.toString(), equalTo("MYROUTINE\tW \"lorem\" W \"ipsum\" W \"dolor\"\n\n") );
	}
	
	@Test
	public void canWriteMulitpleVariableDeclarationsInRoutine() throws RoutineWriterException {
		Routine routine = new Routine() ;
		routine.appendElement( new Tag("MYROUTINE") );
		routine.appendElement( new Command( CommandType.NEW, new DeclarationList( new VariableReference(Scope.LOCAL, ReferenceStyle.DIRECT, "a")) ) );
		routine.appendElement( new Command( CommandType.WRITE, new ExpressionList( new Constant("lorem")) ) );
		routine.appendElement( new Command( CommandType.NEW, new DeclarationList( new VariableReference(Scope.LOCAL, ReferenceStyle.DIRECT, "b")) ) );
		routine.appendElement( new Command( CommandType.NEW, new DeclarationList( new VariableReference(Scope.LOCAL, ReferenceStyle.DIRECT, "c")) ) );
		m_routineWriter.write(routine);
		assertThat( m_buffer.toString(), equalTo("MYROUTINE\tN a W \"lorem\" N b,c\n\n") );
	}

	@Test
	public void canWriteMulitpleCommandsInRoutineOnMultipleLines() throws RoutineWriterException {
		Routine routine = new Routine() ;
		routine.appendElement( new Tag("MYROUTINE") );
		routine.appendElement( new Command( CommandType.WRITE, new ExpressionList( new Constant("lorem")) ) );
		routine.appendElement( new Command( CommandType.WRITE, new ExpressionList( new Constant("ipsum")) ) );
		routine.appendElement( new Command( CommandType.WRITE, new ExpressionList( new Constant("dolor")) ) );
		m_routineFormatter.options().setCommandsPerLineLimit(1);
		m_routineWriter.write(routine);
		assertThat( m_buffer.toString(), equalTo("MYROUTINE\tW \"lorem\"\n\tW \"ipsum\"\n\tW \"dolor\"\n\n") );
	}

	@Test
	public void canWriteSimpleRoutine() throws RoutineWriterException {
		Routine routine = new Routine() ;
		routine.appendElement( new Tag("MYROUTINE") );
		routine.appendElement( new Command( CommandType.NEW, new DeclarationList( new VariableReference( Scope.LOCAL, ReferenceStyle.DIRECT, "message" ) ) ) );
		routine.appendElement( new Command( CommandType.SET, new AssignmentList( new Assignment( new VariableReference( Scope.LOCAL, ReferenceStyle.DIRECT, "message" ), new Constant("Hello, world!") ) ) ) );
		routine.appendElement( new Command( CommandType.WRITE, new ExpressionList( new VariableReference( Scope.LOCAL, ReferenceStyle.DIRECT, "message" ) ) ) );
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
		routine.appendElement( new Command( CommandType.NEW, new DeclarationList( new VariableReference( Scope.LOCAL, ReferenceStyle.DIRECT, "abc" ) ) ) );
		routine.appendElement( new Command( CommandType.WRITE, new ExpressionList( new RoutineFunctionCall( new TagReference(Scope.LOCAL, ReferenceStyle.DIRECT, "MYFUNCTION", null, RoutineAccess.LOCAL ), FunctionCall.Returning.UNKNOWN, Expression.list( new Constant("123"), new Constant("456") ) )) ) );
		routine.appendElement( new Command( CommandType.DO, new TaggedRoutineCall("MYFUNCTION", null, RoutineAccess.LOCAL, Expression.list( new Constant("123"), new Constant("456") ) ) ) );
		routine.appendElement( new Command( CommandType.QUIT ) );
		routine.appendElement( new Tag( "MYFUNCTION", Arrays.asList( new ParameterName("x"), new ParameterName("y") )) );
		routine.appendElement( new Command( CommandType.NEW, new DeclarationList( new VariableReference( Scope.LOCAL, ReferenceStyle.DIRECT, "abc" ) ) ) );
		routine.appendElement( new Command( CommandType.QUIT ) );
		m_routineWriter.write(routine);
		assertThat( m_buffer.toString(), equalTo("MYROUTINE\tN abc W $$MYFUNCTION(123,456) D MYFUNCTION(123,456) Q \nMYFUNCTION(x,y)\tN abc Q \n\n") );
	}
}
