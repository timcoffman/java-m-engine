package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import javax.lang.model.element.Element;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.ReportType;


public abstract class ElementInterpreter<R,P> extends ElementVisitorAdapter<R,P> {
	private final RoutineTools m_builderTools ;
	
	public ElementInterpreter(RoutineTools builderTools) { m_builderTools = builderTools ; }

	protected void report( ReportType reportType, String message, Element element ) {
		m_builderTools.report( reportType, message, element );
	}
	
	@Override
	protected RuntimeException unsupportedElementTypeException(Element element, P parameter) {
		RuntimeException ex = super.unsupportedElementTypeException(element, parameter);
		report( ReportType.ERROR, ex.getMessage(), element );
		return ex;
	}
	
}