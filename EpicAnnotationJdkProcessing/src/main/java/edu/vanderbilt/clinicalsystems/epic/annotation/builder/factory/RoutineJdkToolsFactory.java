package edu.vanderbilt.clinicalsystems.epic.annotation.builder.factory;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.javac.RoutineJdkTools;

public class RoutineJdkToolsFactory implements RoutineToolsFactory {

	@Override
	public RoutineTools create(ProcessingEnvironment processingEnvironment, TypeElement annotatedType) {
		return new RoutineJdkTools( processingEnvironment, annotatedType );
	}

}
