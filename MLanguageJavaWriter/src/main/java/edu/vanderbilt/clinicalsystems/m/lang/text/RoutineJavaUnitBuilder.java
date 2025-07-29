package edu.vanderbilt.clinicalsystems.m.lang.text;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.text.repr.SymbolScope;

public class RoutineJavaUnitBuilder extends RoutineJavaBuilder<RoutineJavaBuilderContext> {

	private final JavaMethodContents m_methodContents ;
	private final SymbolScope m_symbolScope ;

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
		m_symbolScope = env().representationInference().createScope() ;
	}
	
	private RoutineJavaUnitBuilder( JavaMethodContents methodContents, RoutineJavaBuilderContext builderContext ) {
		super( builderContext ) ;
		m_methodContents = methodContents ;
		m_symbolScope = env().representationInference().createScope() ;
	}

	public RoutineJavaUnitBuilder withMethodContents(JavaMethodContents javaMethodContents) {
		return new RoutineJavaUnitBuilder( javaMethodContents, new RoutineJavaBuilderContextImpl(new JCodeModel(), env()) );
	}
	
	public RoutineJavaUnitBuilder build( String fullyQualifiedPackageName, Routine routine ) throws Exception {
		String symbol = context().symbolForIdentifier( routine.name() ); 
		String className = symbol.substring(0, 1).toUpperCase() + symbol.substring(1);
		final String fullyQualifiedName = null == fullyQualifiedPackageName || fullyQualifiedPackageName.isEmpty()
				? className
				: fullyQualifiedPackageName + "." + className
				;
		JDefinedClass definedClass ;
		switch (m_methodContents) {
		case EXECUTABLE:
			definedClass = codeModel()._class( fullyQualifiedName, ClassType.CLASS ) ;
			context().forEachListener( (el)->el.createdClass(definedClass, routine.name() ) ) ;
			break ;
		case IMPLEMENTATION:
			definedClass = codeModel()._class( fullyQualifiedName + "Impl", ClassType.CLASS ) ;
			definedClass._implements( context().codeModel()._class( fullyQualifiedName ) ) ;
			context().forEachListener( (el)->el.createdClass(definedClass, routine.name() ) ) ;
			break ;
		case STUB:
		default:
			definedClass = codeModel()._class( fullyQualifiedName, ClassType.INTERFACE ) ;
			context().forEachListener( (el)->el.createdClass(definedClass, routine.name() ) ) ;
			break ;
		}
		
		SymbolScope symbolScope = env().representationInference().createScope( m_symbolScope, "unit-scope:" + fullyQualifiedName + ":" + m_symbolScope.description() ) ;
		RoutineJavaClassBuilder classBuilder = new RoutineJavaClassBuilder( context(), symbolScope, m_methodContents ) ;
		classBuilder.analyze( routine, className ).build( definedClass );

		return this ;
	}
}
