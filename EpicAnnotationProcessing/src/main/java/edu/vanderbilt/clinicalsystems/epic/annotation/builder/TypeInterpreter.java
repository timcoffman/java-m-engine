package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import javax.lang.model.type.TypeMirror;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.ReportType;


public abstract class TypeInterpreter<R,P> extends TypeVisitorAdapter<R,P> {
	private final RoutineTools m_builderTools ;
	
	public TypeInterpreter(RoutineTools builderTools) { m_builderTools = builderTools ; }

	protected void report( ReportType reportType, String message, TypeMirror typeMirror ) {
		m_builderTools.report( reportType, message, typeMirror );
	}
	
	@Override
	protected RuntimeException unsupportedTypeMirrorTypeException(TypeMirror typeMirror, P parameter) {
		RuntimeException ex = super.unsupportedTypeMirrorTypeException(typeMirror, parameter);
		report( RoutineTools.ReportType.ERROR, ex.getMessage(), typeMirror );
		return ex;
	}
	
}