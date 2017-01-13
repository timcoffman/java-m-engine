package edu.vanderbilt.clinicalsystems.m.junit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.internal.runners.statements.Fail;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.xml.sax.SAXException;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;
import edu.vanderbilt.clinicalsystems.m.engine.ErrorCode;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.CompiledRoutine;
import edu.vanderbilt.clinicalsystems.m.engine.virtual.Executor.ExecutionResult;
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
	
	public MLanguageTestRunner(Class<?> type) throws InitializationError {
		super(type);
		
		if ( !type.isAnnotationPresent(RoutineUnit.class) )
			throw new IllegalArgumentException( "test class " + type.getName() + " is run with " + MLanguageTestRunner.class.getSimpleName() + " but is not annotated with " + RoutineUnit.class.getName() ) ;
		
		ServiceLoader<RoutineParserFactory> routineParserLoader = ServiceLoader.load( RoutineParserFactory.class ) ;
		m_parserFactory = routineParserLoader.iterator().next();
		
		m_db = new edu.vanderbilt.clinicalsystems.m.engine.virtual.Database() ;
		
		install( type ) ;
	}
	
	private void install( Class<?> type ) {
		install( type, RoutineTools.determineRoutineName( type ) ) ;
	}
	
	private void install( Class<?> type, String routineName ) {
		
		CompiledRoutine compiledRoutine = m_db.lookup(routineName) ;
		if ( compiledRoutine != null )
			return ;
		
		if ( type.isAnnotationPresent(RoutineUnit.class) ) {
			URL testClassRoutineLocation = type.getResource( routineName + ".m" ) ;
			if ( null == testClassRoutineLocation )
//				throw new RuntimeException( "missing resource \"" + routineName + ".m\" for " + type.getName() ) ;
				installNativeJavaRoutine( type, routineName ) ;
			else
				installTranslatedRoutine( type, routineName, testClassRoutineLocation ) ;
		} else {
			installNativeJavaRoutine( type, routineName ) ;
		}
	}
		
	private void installTranslatedRoutine( Class<?> type, String routineName, URL testClassRoutineLocation ) {
		try ( InputStream testClassRoutineStream = testClassRoutineLocation.openStream() ) {
			
			Routine routine = parseRoutine( testClassRoutineStream ) ;
			m_db.install(routine);
			
		} catch (IOException ex) {
			throw new RuntimeException( ex );
		} catch (RoutineWriterException ex) {
			throw new RuntimeException( ex );
		}
		
		installDependencies( type, routineName ) ;
	}
	
	private void installNativeJavaRoutine( Class<?> type, String routineName ) {
		try {
			m_db.install( type, routineName );
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
	
	private void installDependencies( Class<?> type, String routineName ) {

		URL testClassMetaLocation = type.getResource( routineName + ".xml" ) ;
		if ( null == testClassMetaLocation )
			throw new RuntimeException( "missing resource \"" + routineName + ".xml\" for " + type.getName() ) ;
		Map<String,String> dependencies = lookupDependencies( type, routineName, testClassMetaLocation );
		
		for ( Map.Entry<String,String> entry : dependencies.entrySet() ) {
			try {
				install( Class.forName( entry.getValue()), entry.getKey() ) ;
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	public class TaggedRoutineInvoker extends Statement {

		private final String m_routineName ;
		private final String m_tagName ;
		
		public TaggedRoutineInvoker(String routineName, String tagName) {
			m_routineName = routineName;
			m_tagName = tagName;
		}

		@Override
		public void evaluate() throws Throwable {
			
			TaggedRoutineCall taggedRoutineCall = new TaggedRoutineCall( new TagReference(m_tagName, m_routineName), Collections.emptyList() );
			
			try ( edu.vanderbilt.clinicalsystems.m.engine.virtual.Connection cxn = m_db.openConnection() ) {
				ExecutionResult result = cxn.execute( new Command(CommandType.DO, new TaggedRoutineCallList( Arrays.asList( taggedRoutineCall) ) ) ) ;
				switch ( result ) {
				case CONTINUE:
					/* success */
					return ;
				case ERROR:
					if ( cxn.error().errorCode() == ErrorCode.JAVA_EXCEPTION )
						throw cxn.error().getCause() ;
					else
						throw new RuntimeException( "error while executing tagged routine " + RoutineTools.determineRoutineName( MLanguageTestRunner.this.getTestClass().getJavaClass() ) + "^" +  m_tagName, cxn.error() ) ;
				default:
					throw new RuntimeException( "tagged routine " + RoutineTools.determineRoutineName( MLanguageTestRunner.this.getTestClass().getJavaClass() ) + "^" +  m_tagName + " terminated unexpectedly" ) ;
				}
			}
			
		}
		
	}
	
	private Routine parseRoutine( InputStream s ) throws IOException {
		
		return m_parserFactory.createRoutineParser().parse(s) ;
		
	}
	
	@Override
    protected Statement methodBlock(FrameworkMethod method) {
		
		String routineName = RoutineTools.determineRoutineName( method.getMethod().getDeclaringClass() ) ;
		String tagName = RoutineTools.determineTagName( method.getMethod() ) ;
		try ( InputStream testClassRoutineStream = method.getMethod().getDeclaringClass().getResourceAsStream( routineName + ".m" ) ) {
			
			return new TaggedRoutineInvoker( routineName, tagName ) ;
			
		} catch (IOException ex) {
			
			return new Fail(ex) ;
			
		}
		
	}

	
	
}
