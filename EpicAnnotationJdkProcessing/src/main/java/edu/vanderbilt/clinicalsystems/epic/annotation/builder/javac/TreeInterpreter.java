package edu.vanderbilt.clinicalsystems.epic.annotation.builder.javac;

import com.sun.source.tree.Tree;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.ReportType;

public abstract class TreeInterpreter<R,P> extends TreeVisitorAdapter<R,P> {
	private final RoutineJdkTools m_builderTools ;
	
	public TreeInterpreter(RoutineJdkTools builderTools) { m_builderTools = builderTools ; }
	
	protected void report( ReportType reportType, String message, Tree tree) {
		m_builderTools.report( reportType, message, tree );
	}

	@Override
	protected RuntimeException unsupportedTreeTypeException(Tree tree, P parameter) {
		RuntimeException ex = super.unsupportedTreeTypeException(tree, parameter);
		report( ReportType.ERROR, ex.getMessage(), tree );
		return ex;
	}
	
}