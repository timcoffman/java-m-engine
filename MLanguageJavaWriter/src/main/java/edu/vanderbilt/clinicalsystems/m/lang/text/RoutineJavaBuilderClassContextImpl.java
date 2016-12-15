package edu.vanderbilt.clinicalsystems.m.lang.text;

import com.sun.codemodel.JCodeModel;

class RoutineJavaBuilderClassContextImpl extends RoutineJavaBuilderContextImpl implements RoutineJavaBuilderClassContext {

	private final String m_outerClassName ;
	
	public RoutineJavaBuilderClassContextImpl( JCodeModel codeModel, String outerClassName, RoutineJavaBuilderEnvironment environment ) {
		super(codeModel, environment) ;
		m_outerClassName = outerClassName ;
	}
	
	@Override public String outerClassName() { return m_outerClassName ; }

}