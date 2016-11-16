package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import javax.lang.model.element.Element;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.ReportType;

public abstract class Builder {

	private final RoutineTools m_builderTools ;
	
	protected Builder( RoutineTools builderTools ) { m_builderTools = builderTools ; }
	
	protected RoutineTools tools() { return m_builderTools ; } 

	protected void report(ReportType reportType, String message, Element element) { m_builderTools.report(reportType, message, element); }
	protected void report(ReportType reportType, String message, Ast.Node node) { m_builderTools.report(reportType, message, node); }
}
