package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.util.function.Function;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpression;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.Tag;

public class RoutineJavaBuilder<T extends RoutineJavaBuilderContext> {

	public enum JavaMethodContents {
		STUB,
		EXECUTABLE
	}
	
	private final T m_builderContext ;

	public RoutineJavaBuilder( T builderContext ) {
		m_builderContext = builderContext ;
	}
	
	public interface Builder<T> {
		void build( T location ) ;
	}

	public interface Binder<T extends JExpression> extends Function<Representation,JavaExpression<T>> {
	}

	protected T context() { return m_builderContext; }
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