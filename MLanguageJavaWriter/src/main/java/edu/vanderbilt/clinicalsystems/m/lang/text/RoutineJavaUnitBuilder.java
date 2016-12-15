package edu.vanderbilt.clinicalsystems.m.lang.text;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;

public class RoutineJavaUnitBuilder extends RoutineJavaBuilder<RoutineJavaBuilderContext> {

	public RoutineJavaUnitBuilder() {
		super( new RoutineJavaBuilderContextImpl( new JCodeModel() ) ) ;
	}
	
	public RoutineJavaUnitBuilder( JCodeModel codeModel ) {
		super( new RoutineJavaBuilderContextImpl( codeModel ) ) ;
	}
	
	public RoutineJavaUnitBuilder build( String fullyQualifiedPackageName, Routine routine ) throws Exception {
		String className = routine.name().substring(0, 1).toUpperCase() + routine.name().substring(1).toLowerCase();
		final String fullyQualifiedName = null == fullyQualifiedPackageName || fullyQualifiedPackageName.isEmpty()
				? className
				: fullyQualifiedPackageName + "." + className
				;
		JDefinedClass definedClass = codeModel()._class( fullyQualifiedName, ClassType.CLASS ) ;
		RoutineJavaClassBuilder classBuilder = new RoutineJavaClassBuilder( context() ) ;
		classBuilder.analyze( routine, className ).build( definedClass );

		return this ;
	}
}
