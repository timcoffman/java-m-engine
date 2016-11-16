package edu.vanderbilt.clinicalsystems.epic.annotation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
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

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.Generator;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineGenerator;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.factory.RoutineToolsFactory;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineLinearWriter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineNativeFormatter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineTreeFormatter;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineWriterException;

@SupportedAnnotationTypes("edu.vanderbilt.clinicalsystems.epic.annotation.EpicRoutine")
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
		for ( Element annotatedType : roundEnv.getElementsAnnotatedWith( edu.vanderbilt.clinicalsystems.epic.annotation.EpicRoutine.class ) ) {
			try {
				Routine routine = generateRoutine( (TypeElement)annotatedType ) ;
				writeRoutineFile( annotatedType, routine ) ;
				writeRoutineTree( annotatedType, routine ) ;
			} catch (IOException e) {
				processingEnv.getMessager().printMessage(Kind.ERROR,"cannot write file", annotatedType);
			}
		}
		
		return true;
	}
	
	private void writeRoutineFile( Element annotatedType, Routine routine ) throws IOException {
		String packageOfAnnotatedType = processingEnv.getElementUtils().getPackageOf(annotatedType).getQualifiedName().toString();
		FileObject resourceFile = processingEnv.getFiler().createResource( StandardLocation.CLASS_OUTPUT, packageOfAnnotatedType, annotatedType.getSimpleName() + ".m", annotatedType) ;
		processingEnv.getMessager().printMessage(Kind.NOTE, "writing " + resourceFile.toUri() );

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
	
	private void writeRoutineTree( Element annotatedType, Routine routine ) throws IOException {
		String packageOfAnnotatedType = processingEnv.getElementUtils().getPackageOf(annotatedType).getQualifiedName().toString();
		FileObject resourceFile = processingEnv.getFiler().createResource( StandardLocation.CLASS_OUTPUT, packageOfAnnotatedType, annotatedType.getSimpleName() + ".tree", annotatedType) ;
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
		ServiceLoader<RoutineToolsFactory> serviceLoader = ServiceLoader.load( RoutineToolsFactory.class ) ;
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

	private static final Generator.Listener ROUTINE_LISTENER = new Generator.Listener() {
		@Override public void generateSideEffect( Generator.Listener.Location location, RoutineElement element ) {
			throw new UnsupportedOperationException("routine generation does not allow side-effects") ;
		}
	} ;
	
	private Routine generateRoutine( TypeElement annotatedType ) throws IOException {
		RoutineTools routineTools = createRoutineTools( processingEnv, annotatedType );
		RoutineGenerator routineBuilder = new RoutineGenerator( routineTools ) ;
		return routineBuilder.generate( annotatedType, ROUTINE_LISTENER ) ;
	}
}
