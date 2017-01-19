package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.ReportType;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.Comment;
import edu.vanderbilt.clinicalsystems.m.lang.model.MultilineBlock;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.Tag;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;

public class RoutineGenerator extends Generator<Routine,TypeElement> {
	
	public RoutineGenerator(RoutineTools builderTools) { super(builderTools) ; }
	
	@Override
	public Routine generate(TypeElement annotatedType, Listener routineListener ) {
		Routine routine = new Routine() ;
		
		routine.appendElement( new Tag( annotatedType.getSimpleName().toString() ) );
		for ( String comment : tools().commentsOnElement(annotatedType) )
				routine.appendElement( new Comment(comment) );
		routine.appendElement( new Command( CommandType.QUIT, Argument.NOTHING ) );
		
		for (Element element : annotatedType.getEnclosedElements()) {

			final RoutineTag epicTag = element.getAnnotation( RoutineTag.class ) ;
			if ( null == epicTag )
				continue ;
			
			if ( !element.getModifiers().contains( Modifier.PUBLIC ) )
				report( ReportType.WARNING, "tagged methods are expected to be public", element);
			
//			if ( !element.getModifiers().contains( Modifier.STATIC ) )
//				report( ReportType.WARNING, "tagged methods are expected to be static", element);
			
			element.accept( new ElementInterpreter<Void, Routine>(tools()) {

				@Override public Void visitExecutable(ExecutableElement executableElement, Routine routine) {
					
					MultilineBlock taggedBlock = tools().tags().generate(executableElement, routineListener);
					routine.appendElements( taggedBlock );
					return null;
				}
				
			}, routine) ;
		}
		
		return routine ;
	}
	
}