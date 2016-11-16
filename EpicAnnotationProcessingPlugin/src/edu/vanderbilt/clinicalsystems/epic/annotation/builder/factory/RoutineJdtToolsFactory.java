package edu.vanderbilt.clinicalsystems.epic.annotation.builder.factory;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools;

public class RoutineJdtToolsFactory implements RoutineToolsFactory {

	@Override
	public RoutineTools create(ProcessingEnvironment processingEnvironment, TypeElement annotatedType) {
		return new RoutineJdtTools(processingEnvironment, annotatedType) ;
	}

}
