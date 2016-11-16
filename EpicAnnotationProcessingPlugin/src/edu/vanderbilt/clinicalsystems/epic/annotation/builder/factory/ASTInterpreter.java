package edu.vanderbilt.clinicalsystems.epic.annotation.builder.factory;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.ReportType;

public class ASTInterpreter<R,P> extends ASTAdapter<R,P> {
	private final RoutineJdtTools m_builderTools ;
	
	public ASTInterpreter(RoutineJdtTools builderTools) { m_builderTools = builderTools ; }
	
	protected void report( ReportType reportType, String message, ASTNode node) {
		m_builderTools.report( reportType, message, node );
	}

	@Override
	protected RuntimeException unsupportedAstNodeTypeException(ASTNode node) {
		RuntimeException ex = super.unsupportedAstNodeTypeException(node);
		report( ReportType.ERROR, ex.getMessage(), node);
		return ex;
	}
}
