package edu.vanderbilt.clinicalsystems.epic.annotation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.Generator;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineGenerator;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.RoutineDependency;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.TaggedRoutineDependency;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.factory.RoutineToolsFactory;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineLinearWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineNativeFormatter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineTreeFormatter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

@SupportedAnnotationTypes("edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class Processor extends AbstractProcessor {

	public Processor() {
		super() ;
	}
	
	@Override
	public synchronized void init( final ProcessingEnvironment processingEnvironment) {
		super.init(processingEnvironment);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for ( Element annotatedType : roundEnv.getElementsAnnotatedWith( RoutineUnit.class ) ) {
			try {
				RoutineTranslationInfo translation = generateRoutine( (TypeElement)annotatedType ) ;
				writeRoutineFile( (TypeElement)annotatedType, translation.routine() ) ;
				writeRoutineMetaFile( (TypeElement)annotatedType, translation ) ;
				writeRoutineTree( (TypeElement)annotatedType, translation.routine() ) ;
			} catch (IOException e) {
				processingEnv.getMessager().printMessage(Kind.ERROR,"cannot write file", annotatedType);
			}
		}
		
		return true;
	}
	
	private void writeRoutineFile( TypeElement annotatedType, Routine routine ) throws IOException {
		String packageOfAnnotatedType = processingEnv.getElementUtils().getPackageOf(annotatedType).getQualifiedName().toString();
		String routineName = RoutineTools.determineRoutineName( annotatedType ) ;
		FileObject resourceFile = processingEnv.getFiler().createResource( StandardLocation.CLASS_OUTPUT, packageOfAnnotatedType, routineName + ".m", annotatedType) ;
		processingEnv.getMessager().printMessage(Kind.NOTE, "writing " + resourceFile.toUri() );

		System.out.println("************************************************************");
		System.out.println("********** processing " + annotatedType.getSimpleName() );
		System.out.println("********** writing    " + resourceFile.toUri() );
		System.out.println("************************************************************");

		RoutineNativeFormatter routineFormatter = new RoutineNativeFormatter() ;
		routineFormatter.options().setCommandsPerLineLimit(1);
		routineFormatter.options().setCommentsPerLineLimit(1);
		routineFormatter.options().setUseTabsForIndentation(true);
		routineFormatter.options().setWriteAbbreviatedBuiltinFunctionSymbols(false);
		routineFormatter.options().setWriteAbbreviatedCommandSymbols(false);
		try ( Writer writer = new BufferedWriter(resourceFile.openWriter()) ) {
			
			RoutineLinearWriter routineWriter = new RoutineLinearWriter(writer, routineFormatter) ;
			routine.write(routineWriter);
			
		} catch (RoutineWriterException ex) {
			processingEnv.getMessager().printMessage(Kind.ERROR, ex.getMessage(), annotatedType);
		} catch (RuntimeException ex) {
			processingEnv.getMessager().printMessage(Kind.ERROR, ex.getMessage(), annotatedType);
		}
		
	}
	
	private void writeRoutineMetaFile( TypeElement annotatedType, RoutineTranslationInfo translation ) throws IOException {
		String packageOfAnnotatedType = processingEnv.getElementUtils().getPackageOf(annotatedType).getQualifiedName().toString();
		String routineName = RoutineTools.determineRoutineName( annotatedType ) ;
		FileObject resourceFile = processingEnv.getFiler().createResource( StandardLocation.CLASS_OUTPUT, packageOfAnnotatedType, routineName + ".xml", annotatedType) ;
		processingEnv.getMessager().printMessage(Kind.NOTE, "writing " + resourceFile.toUri() );

		System.out.println("************************************************************");
		System.out.println("********** processing " + annotatedType.getSimpleName() );
		System.out.println("********** writing    " + resourceFile.toUri() );
		System.out.println("************************************************************");

		try {
			javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance() ;
			javax.xml.parsers.DocumentBuilder documentBuilder = factory.newDocumentBuilder() ;
			org.w3c.dom.Document doc = documentBuilder.newDocument() ;
			org.w3c.dom.Element routineElement = doc.createElement("routine") ;
			doc.appendChild(routineElement) ;
			routineElement.setAttribute("name", translation.routine().name() );
			
			for ( RoutineDependency dependendency : translation.dependencies() ) {
				org.w3c.dom.Element dependencyElement = doc.createElement("dependency") ;
				dependencyElement.setAttribute("routine", dependendency.routineName() );
				dependencyElement.setAttribute("class", dependendency.dependsOnType().getQualifiedName().toString() );
				if ( dependendency instanceof TaggedRoutineDependency ) {
					TaggedRoutineDependency tagDep = (TaggedRoutineDependency)dependendency ;
					dependencyElement.setAttribute("tag", tagDep.tagName() );
					dependencyElement.setAttribute("method", tagDep.dependsOnMethod().getSimpleName().toString() );
				}
				routineElement.appendChild(dependencyElement) ;
			}
			
			for ( String tagName : translation.routine().tagNames() ) {
				org.w3c.dom.Element tagElement = doc.createElement("tag") ;
				tagElement.setAttribute("name", tagName );
				routineElement.appendChild(tagElement) ;
			}
			
			javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
			javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			try ( Writer writer = new BufferedWriter(resourceFile.openWriter()) ) {
				transformer.transform(
						new javax.xml.transform.dom.DOMSource(doc),
						new javax.xml.transform.stream.StreamResult(writer)
					);
			}
		} catch (ParserConfigurationException | TransformerException ex) {
			ex.printStackTrace();
		}
		
	}
	
	private void writeRoutineTree( TypeElement annotatedType, Routine routine ) throws IOException {
		String packageOfAnnotatedType = processingEnv.getElementUtils().getPackageOf(annotatedType).getQualifiedName().toString();
		String routineName = RoutineTools.determineRoutineName( annotatedType ) ;
		FileObject resourceFile = processingEnv.getFiler().createResource( StandardLocation.CLASS_OUTPUT, packageOfAnnotatedType, routineName + ".tree", annotatedType) ;
		processingEnv.getMessager().printMessage(Kind.NOTE, "writing " + resourceFile.toUri() );
		
		RoutineTreeFormatter routineFormatter = new RoutineTreeFormatter() ;
		try ( Writer writer = new BufferedWriter(resourceFile.openWriter()) ) {
			
			RoutineLinearWriter routineWriter = new RoutineLinearWriter(writer, routineFormatter) ;
			routine.write(routineWriter);
			
		} catch (RoutineWriterException ex) {
			processingEnv.getMessager().printMessage(Kind.ERROR, ex.getMessage(), annotatedType);
		} catch (RuntimeException ex) {
			processingEnv.getMessager().printMessage(Kind.ERROR, ex.getMessage(), annotatedType);
		}
	}

	private RoutineTools createRoutineTools(ProcessingEnvironment processingEnvironment, TypeElement annotatedType) {
		Throwable firstThrowable = null ;
		ServiceLoader<RoutineToolsFactory> serviceLoader = ServiceLoader.load( RoutineToolsFactory.class, Processor.class.getClassLoader() ) ;
		Iterator<RoutineToolsFactory> i = serviceLoader.iterator() ;
		while ( i.hasNext() ) {
			RoutineToolsFactory factory = i.next();
			try {
				return factory.create(processingEnvironment, annotatedType) ;
			} catch ( Throwable throwable ) {
				if ( null == firstThrowable )
					firstThrowable = throwable ;
			}
		}
		throw new RuntimeException( "no RoutineToolsFactory available to create RoutineTools for this compiler", firstThrowable ) ;
	}
	
	private static abstract class RoutineListener implements Generator.Listener {
		@Override public void generateSideEffect( Generator.Listener.Location location, RoutineElement element ) {
			throw new UnsupportedOperationException("routine generation does not allow side-effects") ;
		}
	}

	private class RoutineTranslationInfo {
		private final Routine m_routine;
		private final Set<RoutineDependency> m_dependencies = new HashSet<RoutineDependency>();
		
		public RoutineTranslationInfo(Routine routine, Collection<RoutineDependency> dependencies) {
			m_routine = routine;
			m_dependencies.addAll( dependencies ) ;
		}
		public Routine routine() { return m_routine ; }
		public Set<RoutineDependency> dependencies() { return m_dependencies ; }
	}
	
	private RoutineTranslationInfo generateRoutine( TypeElement annotatedType ) throws IOException {
		RoutineTools routineTools = createRoutineTools( processingEnv, annotatedType );
		RoutineGenerator routineBuilder = new RoutineGenerator( routineTools ) ;
		Collection<RoutineDependency> dependencies = new HashSet<RoutineTools.RoutineDependency>();
		Generator.Listener listener = new RoutineListener() {
			@Override public void publishDependency(RoutineDependency dependency) {
				dependencies.add( dependency ) ;
			}
		} ;
		Routine routine = routineBuilder.generate( annotatedType, listener  ) ;
		return new RoutineTranslationInfo( routine, dependencies ) ;
	}
}
