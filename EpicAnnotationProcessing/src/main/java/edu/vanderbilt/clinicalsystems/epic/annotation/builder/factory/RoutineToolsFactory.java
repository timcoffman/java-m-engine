package edu.vanderbilt.clinicalsystems.epic.annotation.builder.factory;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools;

public interface RoutineToolsFactory {
	RoutineTools create( ProcessingEnvironment processingEnvironment, TypeElement annotatedType ) ;
}
