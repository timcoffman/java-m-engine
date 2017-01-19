package edu.vanderbilt.clinicalsystems.m.junit;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.internal.runners.statements.Fail;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.xml.sax.SAXException;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools;
import edu.vanderbilt.clinicalsystems.m.core.annotation.InjectRoutine;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;
import edu.vanderbilt.clinicalsystems.m.engine.ErrorCode;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.CompiledRoutine;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.ExecutionFrame;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.Executor.ExecutionResult;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.InputOutputDevice;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.Installer.TargetInstanceResolver;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.LineBufferInputOutputDevice;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCallList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.TagReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineParserFactory;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

public class MLanguageTestRunner extends BlockJUnit4ClassRunner {

	private final RoutineParserFactory m_parserFactory;
	private final edu.vanderbilt.clinicalsystems.m.engine.virtual.Database m_db ;
	private static ThreadLocal<edu.vanderbilt.clinicalsystems.m.engine.virtual.Connection> tl_currentConnection
		= new ThreadLocal<edu.vanderbilt.clinicalsystems.m.engine.virtual.Connection>() ;
	
	public MLanguageTestRunner(Class<?> type) throws InitializationError {
		super(type);
		
		if ( !type.isAnnotationPresent(RoutineUnit.class) )
			throw new IllegalArgumentException( "test class " + type.getName() + " is run with " + MLanguageTestRunner.class.getSimpleName() + " but is not annotated with " + RoutineUnit.class.getName() ) ;
		
		ServiceLoader<RoutineParserFactory> routineParserLoader = ServiceLoader.load( RoutineParserFactory.class ) ;
		m_parserFactory = routineParserLoader.iterator().next();
		
		m_db = new edu.vanderbilt.clinicalsystems.m.engine.virtual.Database() ;
		
		install( type, 1 ) ;
	}
	
	private void install( Class<?> type, int transitiveDependencyLimit ) {
		install( type, RoutineTools.determineRoutineName( type ), transitiveDependencyLimit ) ;
	}
	
	private void install( Class<?> type, String routineName, int transitiveDependencyLimit ) {
		
		CompiledRoutine compiledRoutine = m_db.lookup(routineName) ;
		if ( compiledRoutine != null )
			return ;
		
		if ( type.isAnnotationPresent(RoutineUnit.class) ) {
			URL testClassRoutineLocation = type.getResource( routineName + ".m" ) ;
			if ( null == testClassRoutineLocation )
//				throw new RuntimeException( "missing resource \"" + routineName + ".m\" for " + type.getName() ) ;
				installNativeJavaRoutine( type, routineName ) ;
			else
				installTranslatedRoutine( type, routineName, testClassRoutineLocation, transitiveDependencyLimit ) ;
		} else {
			installNativeJavaRoutine( type, routineName ) ;
		}
	}

	
	private class InjectedInstanceResolver implements TargetInstanceResolver {

		@Override
		public Object resolve(Class<?> type, String routineName, ExecutionFrame frame ) {
			
			Object targetInstance = frame.getProperty( "target-instance", Object.class) ;
			
			Optional<Field> field = injectedFields( targetInstance, type, routineName ).findAny()  ;
			if ( !field.isPresent() )
				return null ;

			boolean wasAccessible = field.get().isAccessible() ;
			try {
				field.get().setAccessible(true) ;
				return field.get().get(targetInstance) ;
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				return null ;
			} finally {
				if ( wasAccessible != field.get().isAccessible() )
					field.get().setAccessible( wasAccessible );
			}
			
		}
		
	}
	
	private final InjectedInstanceResolver m_injectedInstanceResolver = new InjectedInstanceResolver() ;
	
	private void installTranslatedRoutine( Class<?> type, String routineName, URL testClassRoutineLocation, int transitiveDependencyLimit ) {
		try ( InputStream testClassRoutineStream = testClassRoutineLocation.openStream() ) {
			
			Routine routine = parseRoutine( testClassRoutineStream ) ;
			m_db.install(routine, m_injectedInstanceResolver);
			
		} catch (IOException ex) {
			throw new RuntimeException( ex );
		} catch (RoutineWriterException ex) {
			throw new RuntimeException( ex );
		}
		
		injectedFields( type )
			.map( Field::getType )
			.forEach( (fieldType)->install(fieldType,transitiveDependencyLimit) )
			;
		
		installDependencies( type, routineName, transitiveDependencyLimit ) ;
	}
	private void installNativeJavaRoutine( Class<?> type, String routineName ) {
		try {
			m_db.install( type, m_injectedInstanceResolver, routineName );
		} catch (RoutineWriterException ex) {
			throw new RuntimeException( ex );
		}
	}
	
	private Map<String,String> lookupDependencies( Class<?> type, String routineName, URL testClassMetaLocation ) {
		Map<String,String> dependencies = new HashMap<String, String>() ;
		try ( InputStream testClassMetaStream = testClassMetaLocation.openStream() ) {

			javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance() ;
			javax.xml.parsers.DocumentBuilder documentBuilder = factory.newDocumentBuilder() ;
			org.w3c.dom.Document doc = documentBuilder.parse( testClassMetaStream ) ;
			javax.xml.xpath.XPath xpath = javax.xml.xpath.XPathFactory.newInstance().newXPath() ;
			org.w3c.dom.NodeList nodes = (org.w3c.dom.NodeList)xpath.evaluate("/routine/dependency", doc, javax.xml.xpath.XPathConstants.NODESET) ; ;
			for ( int i = 0 ; i < nodes.getLength() ; ++i ) {
				org.w3c.dom.Element dependencyElement = (org.w3c.dom.Element)nodes.item(i) ;
				String dependsOnRoutineName = dependencyElement.getAttribute("routine") ;
				String dependsOnClassName = dependencyElement.getAttribute("class") ;
				dependencies.put( dependsOnRoutineName, dependsOnClassName ) ;
			}
				
		} catch (ParserConfigurationException | SAXException | XPathExpressionException ex) {
			throw new RuntimeException( ex );
		} catch (IOException ex) {
			throw new RuntimeException( ex );
		}

		return dependencies ;
	}
	
	private void installDependencies( Class<?> type, String routineName, int transitiveDependencyLimit ) {
//		if ( transitiveDependencyLimit < 1 )
//			return ;

		URL testClassMetaLocation = type.getResource( routineName + ".xml" ) ;
		if ( null == testClassMetaLocation )
			throw new RuntimeException( "missing resource \"" + routineName + ".xml\" for " + type.getName() ) ;
		Map<String,String> dependencies = lookupDependencies( type, routineName, testClassMetaLocation );
		
		for ( Map.Entry<String,String> entry : dependencies.entrySet() ) {
			try {
				install( Class.forName( entry.getValue()), entry.getKey(), transitiveDependencyLimit - 1  ) ;
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	private static Stream<Field> injectedFields( Object targetInstance ) {
		return Arrays.stream( targetInstance.getClass().getDeclaredFields() )
			.filter( (field)->field.isAnnotationPresent(InjectRoutine.class) )
			;
	}
	
	private static Stream<Field> injectedFields( Object targetInstance, Class<?> type ) {
		return injectedFields(targetInstance)
			.filter( (field)->type == null || field.getType().isAssignableFrom( type ) )
			;
	}
	
	private static Stream<Field> injectedFields( Object targetInstance, String routineName ) {
		return injectedFields(targetInstance)
				.filter( (field)->routineName == null || routineName.equals( RoutineTools.determineRoutineName(field.getType()) ) )
				;
	}
	
	private static Stream<Field> injectedFields( Object targetInstance, Class<?> type, String routineName ) {
		return injectedFields(targetInstance)
				.filter( (field)->type == null || field.getType().isAssignableFrom( type ) )
				.filter( (field)->routineName == null || routineName.equals( RoutineTools.determineRoutineName(field.getType()) ) )
				;
	}
	
	public static <T> void inject( Object routineInstance, Class<T> type, T injectInstance ) throws Exception {
		Optional<Exception> anyException = injectedFields(routineInstance,type)
			.map( (field)->injectField(field, routineInstance, injectInstance) )
			.filter( Optional::isPresent )
			.findAny()
			.map(Optional::get)
			;
		if ( anyException.isPresent() )
			throw anyException.get() ;
	}
	
	private static Optional<Exception> injectField( Field field, Object routineInstance, Object injectInstance ) {
		boolean wasAccessible = field.isAccessible() ;
		try {
			field.setAccessible(true);
			field.set(routineInstance, injectInstance) ;
			return Optional.empty() ;
		} catch (IllegalArgumentException | IllegalAccessException ex) {
			return Optional.of(ex) ;
		} finally {
			if ( wasAccessible != field.isAccessible() )
				field.setAccessible( wasAccessible );
		}
	}
		
	public class TaggedRoutineInvoker extends Statement {

		private final String m_routineName ;
		private final String m_tagName ;
		private final Object m_testInstance ;
		
		public TaggedRoutineInvoker(String routineName, String tagName, Object testInstance) {
			m_routineName = routineName;
			m_tagName = tagName;
			m_testInstance = testInstance ;
		}

		@Override
		public void evaluate() throws Throwable {
			
			TaggedRoutineCall taggedRoutineCall = new TaggedRoutineCall( new TagReference(m_tagName, m_routineName), Collections.emptyList() );
			
			edu.vanderbilt.clinicalsystems.m.engine.virtual.Connection preexistingCxn = tl_currentConnection.get() ;
			InputOutputDevice io = new LineBufferInputOutputDevice() ;
			try ( edu.vanderbilt.clinicalsystems.m.engine.virtual.Connection cxn = m_db.openConnection(io) ) {
				tl_currentConnection.set(cxn);
				
				TargetInstanceResolver selfResolver = (t,r,f)->m_testInstance ;
				cxn.setLocalProperty( "target-instance-resolver", selfResolver ) ;
				ExecutionResult result = cxn.execute( new Command(CommandType.DO, new TaggedRoutineCallList( Arrays.asList( taggedRoutineCall) ) ) ) ;
				switch ( result ) {
				case CONTINUE:
					/* success */
					return ;
				case ERROR:
					if ( cxn.error().errorCode() == ErrorCode.JAVA_EXCEPTION )
						throw cxn.error().getCause() ;
					else
//						throw new RuntimeException( "error while executing tagged routine " + RoutineTools.determineRoutineName( MLanguageTestRunner.this.getTestClass().getJavaClass() ) + "^" +  m_tagName, cxn.error() ) ;
						throw cxn.error() ;
				default:
					throw new RuntimeException( "tagged routine " + RoutineTools.determineRoutineName( MLanguageTestRunner.this.getTestClass().getJavaClass() ) + "^" +  m_tagName + " terminated unexpectedly" ) ;
				}
			} finally {
				tl_currentConnection.set(preexistingCxn);
			}
			
		}
		
	}
	
	public static edu.vanderbilt.clinicalsystems.m.engine.virtual.Connection currentConnection() {
		return tl_currentConnection.get();
	}
	
	private Routine parseRoutine( InputStream s ) throws IOException {
		
		return m_parserFactory.createRoutineParser().parse(s) ;
		
	}
	
	@Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
		
		/* "test" is an instance of the test class
		 * can be used for accessing tagged methods, fields, etc.
		 */
		
		String routineName = RoutineTools.determineRoutineName( method.getMethod().getDeclaringClass() ) ;
		String tagName = RoutineTools.determineTagName( method.getMethod() ) ;
		try ( InputStream testClassRoutineStream = method.getMethod().getDeclaringClass().getResourceAsStream( routineName + ".m" ) ) {
			
			return new TaggedRoutineInvoker( routineName, tagName, test ) ;
			
		} catch (IOException ex) {
			
			return new Fail(ex) ;
			
		}
		
	}

	
	
}
