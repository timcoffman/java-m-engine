package edu.vanderbilt.clinicalsystems.m.lang.text;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;

public class RoutineJavaUnitBuilder extends RoutineJavaBuilder<RoutineJavaBuilderContext> {

	private final JavaMethodContents m_methodContents ;

	public RoutineJavaUnitBuilder() {
		this( new JCodeModel() ) ;
	}
	
	public RoutineJavaUnitBuilder( JavaMethodContents methodContents ) {
		this( new JCodeModel(), methodContents ) ;
	}
	
	public RoutineJavaUnitBuilder( JCodeModel codeModel ) {
		this( codeModel, JavaMethodContents.EXECUTABLE ) ;
	}
	
	public RoutineJavaUnitBuilder( JCodeModel codeModel, JavaMethodContents methodContents ) {
		super( new RoutineJavaBuilderContextImpl( codeModel ) ) ;
		m_methodContents = methodContents ;
	}
	
	public RoutineJavaUnitBuilder build( String fullyQualifiedPackageName, Routine routine ) throws Exception {
		String className = routine.name().substring(0, 1).toUpperCase() + routine.name().substring(1).toLowerCase();
		final String fullyQualifiedName = null == fullyQualifiedPackageName || fullyQualifiedPackageName.isEmpty()
				? className
				: fullyQualifiedPackageName + "." + className
				;
		JDefinedClass definedClass = codeModel()._class( fullyQualifiedName, ClassType.CLASS ) ;
		context().forEachListener( (el)->el.createdClass(definedClass, routine.name() ) ) ;
		
		RoutineJavaClassBuilder classBuilder = new RoutineJavaClassBuilder( context(), m_methodContents ) ;
		classBuilder.analyze( routine, className ).build( definedClass );

		return this ;
	}
}
