package edu.vanderbilt.clinicalsystems.m.lang.text;

import com.sun.codemodel.JCodeModel;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.Tag;

public class RoutineJavaBuilder {

	private final RoutineJavaBuilderContext m_builderContext ;

	public RoutineJavaBuilder( RoutineJavaBuilderContext builderContext ) {
		m_builderContext = builderContext ;
	}
	
	protected RoutineJavaBuilderContext context() { return m_builderContext; }
	protected RoutineJavaBuilderEnvironment env() { return context().env(); }

	protected JCodeModel codeModel() { return context().codeModel(); }
	
	protected Class<?> valueClass() { return env().valueClass(); }

	protected boolean endOfMethod(RoutineElement element) {
		if ( element instanceof Command ) {
			return endOfMethod( (Command)element ) ;
		} else if ( element instanceof Tag ) {
			return endOfMethod( (Tag)element ) ;
		} else {
			return false ;
		}
	}

	private boolean endOfMethod(Command command) {
		if ( CommandType.QUIT == command.commandType() && null == command.condition() )
			return true ;
		else if ( CommandType.GOTO == command.commandType() && null == command.condition() )
			return true ;
		else
			return false ;
	}
	
	private boolean endOfMethod(Tag tag) {
		if ( tag.parameterNames().iterator().hasNext() )
			return true ;
		else
			return false ;
	}

}