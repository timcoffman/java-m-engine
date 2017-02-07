package edu.vanderbilt.clinicalsystems.m.lang.text;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.ServiceLoader;

import org.junit.Before;
import org.junit.Test;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JStatement;
import com.sun.codemodel.writer.SingleStreamCodeWriter;

import edu.vanderbilt.clinicalsystems.m.Core;
import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.annotation.InjectRoutine;
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
	
	private RoutineParser m_routineParser;
	private RoutineJavaUnitBuilder m_routineBuilder;
	private JCodeModel m_codeModel;
	
	@Before
	public void setupRoutineParser() {
		RoutineParserFactory factory = ServiceLoader.load(RoutineParserFactory.class).iterator().next() ;
		m_routineParser = factory.createRoutineParser();
		
	}
	@Before
	public void setupRoutineBuilder() {
		m_routineBuilder = new RoutineJavaUnitBuilder();
		Core.useLibrariesIn(m_routineBuilder.env());
		
		m_codeModel = m_routineBuilder.codeModel() ;
	}

	private static JMethod assertAndReturnMethodWithName( String name, JDefinedClass definedClass ) {
		return definedClass.methods().stream()
				.filter( (m)->name.equals(m.name()) )
				.findAny()
				.orElseThrow( ()->new AssertionError("method \"" + name + "\" not present on defined class") ); 
	}
	
	private static JFieldVar assertAndReturnFieldWithName( String name, JDefinedClass definedClass ) {
		return definedClass.fields().values().stream()
				.filter( (m)->name.equals(m.name()) )
				.findAny()
				.orElseThrow( ()->new AssertionError("field \"" + name + "\" not present on defined class") ); 
	}
	
	private JDefinedClass buildClassFromRoutineSource( String routineSource ) throws Exception {
		Routine routine = m_routineParser.parse(routineSource) ;
		m_routineBuilder.build(RoutineJavaWriterTest.class.getPackage().getName(), routine) ;
		
		return m_routineBuilder.codeModel().packages().next().classes().next() ;
	}
	
	@Test
	public void canInferMethodReturnType() throws Exception {
		JDefinedClass definedClass = buildClassFromRoutineSource(
				"MyRoutine Q\n" +
				"myCallee QUIT 1"
		) ;
		assertThat( definedClass, notNullValue() ) ;
		assertThat( definedClass.name(), equalTo("MyRoutine") ) ;
		JMethod method = assertAndReturnMethodWithName("myCallee",definedClass);
		assertThat( method.params().size(), equalTo(0) ) ;
		assertThat( method.type(), equalTo( m_codeModel.LONG ) ) ;
		
	}
	
	@Test
	public void canInferMethodReturnType2() throws Exception {
		JDefinedClass definedClass = buildClassFromRoutineSource(
				"MyRoutine Q\n" +
				"myFunc() N port\n" +
				"  S port=^%ZeOSUNQ(\"EPICCOMM\",\"TCP\",\"PORT\")\n" +
				"  Q:port=\"\" 7777\n" +
				"  Q port\n"
				) ;
		assertThat( definedClass, notNullValue() ) ;
		assertThat( definedClass.name(), equalTo("MyRoutine") ) ;
		JMethod method = assertAndReturnMethodWithName("myFunc",definedClass);
		assertThat( method.params().size(), equalTo(0) ) ;
		assertThat( method.type(), equalTo( m_codeModel.LONG ) ) ;
		
	}
	
	@Test
	public void canInferMethodParameterType() throws Exception {
		JDefinedClass definedClass = buildClassFromRoutineSource(
				"MyRoutine Q\n" +
						"myCaller QUIT $$myCallee(1)\n" +
						"myCallee(arg) QUIT arg"
				) ;
		assertThat( definedClass, notNullValue() ) ;
		assertThat( definedClass.name(), equalTo("MyRoutine") ) ;
		JMethod method = assertAndReturnMethodWithName("myCallee",definedClass);
		assertThat( method.params().size(), equalTo(1) ) ;
		assertThat( method.params().get(0).name(), equalTo("arg") ) ;
		assertThat( method.params().get(0).type(), equalTo( m_codeModel.LONG ) ) ;
		
	}
	
	@Test
	public void canProduceTransientIndexedAccess() throws Exception {
		JDefinedClass definedClass = buildClassFromRoutineSource(
				"MyRoutine Q\n" +
				"myCallee(transientData) QUIT transientData(\"a\",2,1,3.14)"
		) ;
		assertThat( definedClass, notNullValue() ) ;
		assertThat( definedClass.name(), equalTo("MyRoutine") ) ;
		JMethod method = assertAndReturnMethodWithName("myCallee",definedClass);
		assertThat( method.params().size(), equalTo(1) ) ;
		assertThat( method.type(), equalTo( m_codeModel._ref(Value.class) ) ) ;
		assertThat( method.body().getContents().size(), equalTo(1) ) ;
		JStatement returnSstatement = (JStatement) method.body().getContents().get(0);
		assertThat( render(returnSstatement), equalTo( "return transientData.get(\"a\").get(\"2\").get(\"1\").get(\"3.14\");\n" ) ) ;
		
		
	}
	
	@Test
	public void canProducePersistentIndexedAccess() throws Exception {
		JDefinedClass definedClass = buildClassFromRoutineSource(
				"MyRoutine Q\n" +
				"myCallee QUIT ^persistentData(\"a\",2,1,3.14)"
		) ;
		assertThat( definedClass, notNullValue() ) ;
		assertThat( definedClass.name(), equalTo("MyRoutine") ) ;

		JFieldVar field = assertAndReturnFieldWithName("persistentData",definedClass);
		assertThat( field.type(), equalTo( m_codeModel._ref(Value.class) ) ) ;
		assertThat( field.annotations().size(), equalTo(1) ) ;
		assertThat( field.annotations().iterator().next().getAnnotationClass(), equalTo( m_codeModel._ref(InjectRoutine.class) ) ) ;

		JMethod method = assertAndReturnMethodWithName("myCallee",definedClass);
		assertThat( method.params().size(), equalTo(0) ) ;
		assertThat( method.type(), equalTo( m_codeModel._ref(Value.class) ) ) ;
		assertThat( method.body().getContents().size(), equalTo(1) ) ;
		JStatement returnSstatement = (JStatement) method.body().getContents().get(0);
		assertThat( render(returnSstatement), equalTo( "return persistentData.get(\"a\").get(\"2\").get(\"1\").get(\"3.14\");\n" ) ) ;
	}
	
	private String render( JStatement statement ) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream() ;
		PrintWriter printWriter = new PrintWriter(baos,true); 
		statement.state( new JFormatter(printWriter) ) ;
		try {
			return baos.toString("UTF-8") ;
		} catch (UnsupportedEncodingException ex) {
			throw new AssertionError("failed to render generated java code for testing",ex) ;
		}
	}
	
}
